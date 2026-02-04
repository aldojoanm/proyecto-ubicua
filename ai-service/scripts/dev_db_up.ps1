param(
  [string]$ComposeProject = "ai-service",
  [string]$DbService = "postgres",
  [string]$ComposeFile = "",
  [string]$BindHost = "127.0.0.1",
  [int]$StartPort = 5434,
  [int]$MaxPort = 5444,
  [string]$DbName = "ai_service",
  [string]$DbUser = "postgres",
  [string]$DbPassword = "postgres"
)

$ErrorActionPreference = "Stop"
try { [Console]::OutputEncoding = [System.Text.Encoding]::UTF8 } catch {}
$OutputEncoding = [System.Text.Encoding]::UTF8

function Write-Info($m){ Write-Host "[INFO] $m" -ForegroundColor Cyan }
function Write-Ok($m){ Write-Host "[OK]  $m" -ForegroundColor Green }
function Write-Warn($m){ Write-Host "[WARN] $m" -ForegroundColor Yellow }
function Write-Err($m){ Write-Host "[ERR] $m" -ForegroundColor Red }

function Get-RepoRoot { Split-Path -Parent $PSScriptRoot }

function Get-PidsListening([int]$p){
  $pids = @()
  try {
    $conns = Get-NetTCPConnection -State Listen -LocalPort $p -ErrorAction Stop
    $pids = $conns.OwningProcess | Sort-Object -Unique
  } catch {
    $lines = (netstat -ano | findstr (":$p " )) 2>$null
    foreach ($l in $lines) {
      $cols = ($l -split "\s+") | Where-Object { $_ -ne "" }
      if ($cols.Count -ge 5) { $pids += [int]$cols[-1] }
    }
    $pids = $pids | Sort-Object -Unique
  }
  return $pids
}

function Find-FreePort([int]$from, [int]$to){
  for ($p=$from; $p -le $to; $p++){
    $pids = Get-PidsListening -p $p
    if ($pids.Count -eq 0) { return $p }
  }
  return $null
}

function Upsert-Env([string]$path, [string]$key, [string]$value){
  $line = "$key=$value"
  if (-not (Test-Path $path)) {
    Set-Content -Path $path -Value $line -Encoding ascii
    return
  }
  $raw = Get-Content $path -Raw
  $k = [regex]::Escape($key)
  if ($raw -match "(?m)^$k=") {
    $raw = [regex]::Replace($raw, "(?m)^$k=.*$", $line)
  } else {
    $raw = $raw.TrimEnd() + "`r`n" + $line + "`r`n"
  }
  Set-Content -Path $path -Value $raw -Encoding ascii
}

function Docker-ComposePsQ([string]$project, [string]$file, [string]$service){
  try {
    $out = & docker compose -p $project -f $file ps -q $service 2>$null
    $id = ($out | Select-Object -First 1)
    if ($id) { return $id.Trim() }
    return $null
  } catch {
    return $null
  }
}

function Get-HostPortFromContainer([string]$containerId){
  $j = & docker inspect $containerId | ConvertFrom-Json
  $hp = $j[0].NetworkSettings.Ports."5432/tcp"[0].HostPort
  if ($hp) { return [int]$hp }
  return $null
}

function Wait-ContainerHealthy([string]$containerId, [int]$timeoutSec){
  $deadline = (Get-Date).AddSeconds($timeoutSec)
  while ((Get-Date) -lt $deadline) {
    try {
      $j = & docker inspect $containerId | ConvertFrom-Json
      $health = $j[0].State.Health.Status
      if (-not $health) {
        # si no hay healthcheck, con que esté running alcanza
        if ($j[0].State.Running -eq $true) { return $true }
      } else {
        if ($health -eq "healthy") { return $true }
      }
    } catch {}
    Start-Sleep -Milliseconds 700
  }
  return $false
}

$repoRoot = Get-RepoRoot
if ([string]::IsNullOrWhiteSpace($ComposeFile)) {
  $ComposeFile = Join-Path $repoRoot "docker-compose.yml"
}
if (-not (Test-Path $ComposeFile)) { throw "No encuentro docker-compose.yml en $ComposeFile" }

$envFile = Join-Path $repoRoot ".env"

Write-Info "RepoRoot = $repoRoot"
Write-Info "Compose  = $ComposeFile"
Write-Info "Project  = $ComposeProject"

Push-Location $repoRoot
try {
  # 1) Si ya existe contenedor, reusamos su puerto publicado
  $existingId = Docker-ComposePsQ -project $ComposeProject -file $ComposeFile -service $DbService
  $hostPort = $null
  if ($existingId) {
    $hostPort = Get-HostPortFromContainer -containerId $existingId
    if ($hostPort) {
      Write-Info "Reusando puerto ya publicado para postgres: $hostPort"
    }
  }

  # 2) Si no hay contenedor, elegimos puerto libre y levantamos
  if (-not $hostPort) {
    $candidate = Find-FreePort -from $StartPort -to $MaxPort
    if (-not $candidate) { throw "No encontre un puerto libre entre $StartPort..$MaxPort" }
    $hostPort = $candidate
    Write-Ok  ("Puerto {0} libre." -f $hostPort)

    # Upsert .env (para docker compose)
    Upsert-Env $envFile "AI_PG_BIND_HOST" $BindHost
    Upsert-Env $envFile "AI_PG_HOST_PORT" $hostPort
    Upsert-Env $envFile "AI_PG_DB" $DbName
    Upsert-Env $envFile "AI_PG_USER" $DbUser
    Upsert-Env $envFile "AI_PG_PASSWORD" $DbPassword
  }

  Write-Info ("Target   = {0}:{1} -> postgres(5432) db={2} user={3}" -f $BindHost, $hostPort, $DbName, $DbUser)

  # 3) Set env vars para Spring (proceso actual)
  $jdbc = "jdbc:postgresql://localhost:$hostPort/$DbName"
  $env:DB_URL = $jdbc
  $env:DB_USER = $DbUser
  $env:DB_PASS = $DbPassword

  $env:AI_DB_URL = $jdbc
  $env:AI_DB_USER = $DbUser
  $env:AI_DB_PASSWORD = $DbPassword
  $env:AI_DB_PORT = "$hostPort"
  $env:AI_DB_NAME = $DbName

  # También persistimos en .env para que dev_up/dev_all lo lean si hace falta
  Upsert-Env $envFile "DB_URL" $jdbc
  Upsert-Env $envFile "DB_USER" $DbUser
  Upsert-Env $envFile "DB_PASS" $DbPassword
  Upsert-Env $envFile "AI_DB_URL" $jdbc
  Upsert-Env $envFile "AI_DB_USER" $DbUser
  Upsert-Env $envFile "AI_DB_PASSWORD" $DbPassword
  Upsert-Env $envFile "AI_DB_PORT" $hostPort
  Upsert-Env $envFile "AI_DB_NAME" $DbName

  Write-Ok (" .env actualizado: {0}" -f $envFile)
  Write-Ok " Env vars Spring seteadas (DB_URL / AI_DB_URL)."

  # 4) Levantar servicio DB
  Write-Info ("docker compose -p {0} -f {1} up -d {2}" -f $ComposeProject, $ComposeFile, $DbService)
  & docker compose -p $ComposeProject -f $ComposeFile up -d $DbService | Out-Host
  Write-Ok "Compose up solicitado."

  # 5) Obtener containerId real
  $cid = Docker-ComposePsQ -project $ComposeProject -file $ComposeFile -service $DbService
  if (-not $cid) {
    # fallback por labels
    $cid = (& docker ps -q --filter "label=com.docker.compose.project=$ComposeProject" --filter "label=com.docker.compose.service=$DbService" | Select-Object -First 1)
  }
  if (-not $cid) { throw "No encuentro contenedor del servicio '$DbService' en project '$ComposeProject'." }

  Write-Info ("ContainerId={0}" -f $cid)

  if (-not (Wait-ContainerHealthy -containerId $cid -timeoutSec 180)) {
    throw "Postgres no llego a healthy dentro del timeout."
  }
  Write-Ok "Postgres esta healthy."

  # 6) Asegurar pgvector
  Write-Info "Asegurando extension pgvector (CREATE EXTENSION IF NOT EXISTS vector)..."
  & docker exec $cid psql -U $DbUser -d $DbName -c "CREATE EXTENSION IF NOT EXISTS vector;" | Out-Host

  # Ver version
  $ver = & docker exec $cid psql -U $DbUser -d $DbName -t -A -c "SELECT extversion FROM pg_extension WHERE extname='vector';" 2>$null
  if ($ver) {
    Write-Ok ("pgvector OK => vector:{0}" -f $ver.Trim())
  } else {
    Write-Ok "pgvector OK"
  }

  Write-Host ""
  Write-Host "==================== CONNECTION ====================" -ForegroundColor Gray
  Write-Host ("JDBC_URL : {0}" -f $jdbc)
  Write-Host ("PSQL     : docker exec -it {0} psql -U {1} -d {2}" -f $cid, $DbUser, $DbName)
  Write-Host "====================================================" -ForegroundColor Gray

} finally {
  Pop-Location
}
