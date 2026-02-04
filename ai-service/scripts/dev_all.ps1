[CmdletBinding()]
param(
  [ValidateSet("up","down","status","logs","smoke")]
  [string]$Cmd = "up",

  # App
  [string]$Profile = "dev",
  [int]$Port = 8091,
  [string]$BaseUrl = "",
  [int]$HealthTimeoutSec = 180,
  [switch]$RestartIfSameApp,

  # Down options
  [switch]$Purge,

  # DB
  [int]$DbStartPort = 5434,
  [int]$DbMaxPort = 5444,
  [string]$ComposeProject = "ai-service",
  [string]$DbService = "postgres"
)

$ErrorActionPreference = "Stop"
try { [Console]::OutputEncoding = [System.Text.Encoding]::UTF8 } catch {}
$OutputEncoding = [System.Text.Encoding]::UTF8

function Write-Info($m){ Write-Host ("[INFO] {0}" -f $m) -ForegroundColor Cyan }
function Write-Ok($m){   Write-Host ("[OK]  {0}" -f $m) -ForegroundColor Green }
function Write-Warn($m){ Write-Host ("[WARN] {0}" -f $m) -ForegroundColor Yellow }
function Write-Err($m){  Write-Host ("[ERR] {0}" -f $m) -ForegroundColor Red }

function Get-RepoRoot { Split-Path -Parent $PSScriptRoot }

function Get-PowerShellExe {
  # Usa el mismo “engine” cuando sea posible
  if ($PSVersionTable.PSEdition -eq "Core") {
    $cmd = Get-Command pwsh -ErrorAction SilentlyContinue
    if ($cmd) { return $cmd.Source }
  }
  $cmd2 = Get-Command powershell -ErrorAction SilentlyContinue
  if ($cmd2) { return $cmd2.Source }
  return "powershell"
}

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

function Stop-Pids([int[]]$pids){
  foreach ($procId in $pids) {
    if ($procId -le 0) { continue }
    try {
      $p = Get-Process -Id $procId -ErrorAction SilentlyContinue
      if ($p) {
        Write-Info ("Stopping PID={0} ({1})..." -f $procId, $p.ProcessName)
        Stop-Process -Id $procId -Force -ErrorAction SilentlyContinue
      }
    } catch {}
  }
}

# ---------------- Paths ----------------
$repoRoot   = Get-RepoRoot
$composeFile = Join-Path $repoRoot "docker-compose.yml"

$psExe     = Get-PowerShellExe
$devDbUp   = Join-Path $PSScriptRoot "dev_db_up.ps1"
$devUp     = Join-Path $PSScriptRoot "dev_up.ps1"
$devSmoke  = Join-Path $PSScriptRoot "dev_smoke.ps1"
$devDown   = Join-Path $PSScriptRoot "dev_down.ps1"

if (-not (Test-Path $composeFile)) { throw ("No encuentro docker-compose.yml en: {0}" -f $composeFile) }
if (-not (Test-Path $devDbUp))     { throw ("No encuentro script: {0}" -f $devDbUp) }
if (-not (Test-Path $devUp))       { throw ("No encuentro script: {0}" -f $devUp) }
if (-not (Test-Path $devDown))     { Write-Warn ("No encuentro dev_down.ps1 (down se hará interno): {0}" -f $devDown) }
if (-not (Test-Path $devSmoke))    { Write-Warn ("No encuentro dev_smoke.ps1 (smoke se omitirá): {0}" -f $devSmoke) }

if ([string]::IsNullOrWhiteSpace($BaseUrl)) { $BaseUrl = ("http://127.0.0.1:{0}" -f $Port) }
$BaseUrl = $BaseUrl.TrimEnd("/")

function Compose-Cmd([string[]]$args){
  & docker compose -p $ComposeProject -f $composeFile @args
}

function Get-ComposeContainerId([string]$service){
  $id = $null
  try {
    $out = Compose-Cmd @("ps","-q",$service) 2>$null
    $id = ($out | Select-Object -First 1)
  } catch {}
  if (-not $id) {
    $id = (& docker ps -q --filter "label=com.docker.compose.project=$ComposeProject" --filter "label=com.docker.compose.service=$service" | Select-Object -First 1)
  }
  if ($id) { return $id.Trim() }
  return $null
}

# ---------------- Commands ----------------
function Do-Up {
  Write-Info "=== UP (DB + APP) ==="
  Write-Info ("RepoRoot = {0}" -f $repoRoot)
  Write-Info ("Compose  = {0}" -f $composeFile)
  Write-Info ("Project  = {0}" -f $ComposeProject)
  Write-Info ("App      = profile={0} port={1} base={2}" -f $Profile, $Port, $BaseUrl)

  # Si el puerto está ocupado y el usuario pide restart, lo liberamos antes de dev_up
  $pids = Get-PidsListening -p $Port
  if ($pids.Count -gt 0) {
    if ($RestartIfSameApp) {
      Write-Warn ("Puerto {0} ocupado. RestartIfSameApp=ON => deteniendo PID(s): {1}" -f $Port, ($pids -join ", "))
      Stop-Pids -pids $pids
      Start-Sleep -Milliseconds 800
    } else {
      throw ("El puerto {0} ya está en uso. PID(s): {1}. Usa -RestartIfSameApp o libera el puerto." -f $Port, ($pids -join ", "))
    }
  }

  # 1) DB
  Write-Info "=== DB UP ==="
  & $psExe -NoProfile -ExecutionPolicy Bypass -File $devDbUp `
    -ComposeProject $ComposeProject `
    -DbService $DbService `
    -ComposeFile $composeFile `
    -StartPort $DbStartPort `
    -MaxPort $DbMaxPort | Out-Host

  Write-Ok "DB listo."

  # 2) APP
  Write-Info "=== APP UP ==="
  & $psExe -NoProfile -ExecutionPolicy Bypass -File $devUp `
    -Profile $Profile `
    -Port $Port `
    -BaseUrl $BaseUrl `
    -HealthTimeoutSec $HealthTimeoutSec | Out-Host

  Write-Ok "APP lista."

  # 3) SMOKE (si existe)
  if (Test-Path $devSmoke) {
    Write-Info "=== SMOKE ==="
    & $psExe -NoProfile -ExecutionPolicy Bypass -File $devSmoke -BaseUrl $BaseUrl -TimeoutSec 5 -Retries 2 | Out-Host
    Write-Ok "Smoke OK."
  } else {
    Write-Warn "dev_smoke.ps1 no encontrado, se omite smoke."
  }

  Write-Ok "=== UP COMPLETO ==="
}

function Do-Down {
  Write-Info "=== DOWN (APP + DB) ==="
  if (Test-Path $devDown) {
    & $psExe -NoProfile -ExecutionPolicy Bypass -File $devDown `
      -ComposeProject $ComposeProject `
      -ComposeFile $composeFile `
      -Purge:$Purge | Out-Host
    return
  }

  # Fallback interno (si no existe dev_down.ps1)
  $pidFile = Join-Path $repoRoot ".runtime\pids\ai-bootstrap.pid"
  if (Test-Path $pidFile) {
    $pidRaw = (Get-Content $pidFile -ErrorAction SilentlyContinue | Select-Object -First 1)
    if ($pidRaw) {
      $procId = 0
      if ([int]::TryParse($pidRaw.Trim(), [ref]$procId)) {
        Write-Info ("Stopping app PID={0}" -f $procId)
        Stop-Process -Id $procId -Force -ErrorAction SilentlyContinue
      }
    }
    try { Remove-Item $pidFile -Force -ErrorAction SilentlyContinue } catch {}
  }

  Write-Info "docker compose down"
  $args = @("down","--remove-orphans")
  if ($Purge) { $args += "-v" }
  Compose-Cmd $args | Out-Host
  Write-Ok "DOWN completo."
}

function Do-Status {
  Write-Info "=== STATUS ==="
  Write-Info "Docker Compose:"
  Compose-Cmd @("ps") | Out-Host

  $pidFile = Join-Path $repoRoot ".runtime\pids\ai-bootstrap.pid"
  if (Test-Path $pidFile) {
    $pidRaw = (Get-Content $pidFile -ErrorAction SilentlyContinue | Select-Object -First 1)
    if ($pidRaw) {
      $procId = 0
      if ([int]::TryParse($pidRaw.Trim(), [ref]$procId)) {
        $p = Get-Process -Id $procId -ErrorAction SilentlyContinue
        if ($p) { Write-Ok ("App PID {0} RUNNING" -f $procId) }
        else    { Write-Warn ("App PID {0} NOT RUNNING" -f $procId) }
      }
    }
  } else {
    Write-Warn "No pidfile para app."
  }

  Write-Info ("Health: {0}/actuator/health" -f $BaseUrl)
  try {
    $out = & curl.exe --noproxy "*" -sS --max-time 3 ("{0}/actuator/health" -f $BaseUrl) 2>$null
    if ($out) { Write-Ok $out } else { Write-Warn "No respondió health." }
  } catch {
    Write-Warn "No pude consultar health."
  }
}

function Do-Logs {
  Write-Info "=== LOGS ==="
  $outLog = Join-Path $repoRoot ".runtime\logs\ai-bootstrap.out.log"
  $errLog = Join-Path $repoRoot ".runtime\logs\ai-bootstrap.err.log"

  Write-Info "Tail stdout:"
  if (Test-Path $outLog) { Get-Content $outLog -Tail 120 | Out-Host } else { Write-Warn ("No existe: {0}" -f $outLog) }

  Write-Info "Tail stderr:"
  if (Test-Path $errLog) { Get-Content $errLog -Tail 120 | Out-Host } else { Write-Warn ("No existe: {0}" -f $errLog) }

  $cid = Get-ComposeContainerId $DbService
  if ($cid) {
    Write-Info "Docker logs (postgres) tail:"
    & docker logs --tail 120 $cid | Out-Host
  } else {
    Write-Warn "No encuentro container postgres para logs."
  }
}

function Do-Smoke {
  if (-not (Test-Path $devSmoke)) { throw ("No existe dev_smoke.ps1: {0}" -f $devSmoke) }
  Write-Info "=== SMOKE ==="
  & $psExe -NoProfile -ExecutionPolicy Bypass -File $devSmoke -BaseUrl $BaseUrl -TimeoutSec 5 -Retries 2 | Out-Host
  Write-Ok "Smoke OK."
}

# --------- Dispatch ---------
switch ($Cmd) {
  "up"     { Do-Up }
  "down"   { Do-Down }
  "status" { Do-Status }
  "logs"   { Do-Logs }
  "smoke"  { Do-Smoke }
}
