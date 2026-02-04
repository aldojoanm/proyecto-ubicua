[CmdletBinding()]
param(
  [string]$ComposeProject = "ai-service",
  [string]$ComposeFile = "",
  [switch]$Purge
)

$ErrorActionPreference = "Stop"

function Write-Info($m){ Write-Host ("[INFO] {0}" -f $m) -ForegroundColor Cyan }
function Write-Ok($m){   Write-Host ("[OK]  {0}" -f $m) -ForegroundColor Green }
function Write-Warn($m){ Write-Host ("[WARN] {0}" -f $m) -ForegroundColor Yellow }
function Write-Err($m){  Write-Host ("[ERR] {0}" -f $m) -ForegroundColor Red }

function Get-RepoRoot { Split-Path -Parent $PSScriptRoot }

function Stop-PidFile([string]$pidFile){
  if (-not (Test-Path $pidFile)) {
    Write-Info ("PID file no existe: {0}" -f $pidFile)
    return
  }

  $pidRaw = (Get-Content $pidFile -ErrorAction SilentlyContinue | Select-Object -First 1)
  if ([string]::IsNullOrWhiteSpace($pidRaw)) {
    Write-Warn ("PID file vacío: {0}" -f $pidFile)
    return
  }

  $procId = 0
  if (-not [int]::TryParse($pidRaw.Trim(), [ref]$procId)) {
    Write-Warn ("PID inválido en {0}: {1}" -f $pidFile, $pidRaw)
    return
  }

  $proc = Get-Process -Id $procId -ErrorAction SilentlyContinue
  if ($null -eq $proc) {
    Write-Warn ("Proceso PID {0} ya no existe." -f $procId)
  } else {
    Write-Info ("Deteniendo proceso PID {0}..." -f $procId)
    try {
      Stop-Process -Id $procId -Force -ErrorAction Stop
      Write-Ok ("Proceso {0} detenido." -f $procId)
    } catch {
      Write-Warn ("No pude detener PID {0}. {1}" -f $procId, $_.Exception.Message)
    }
  }

  try { Remove-Item $pidFile -Force -ErrorAction SilentlyContinue } catch {}
}

function Docker-Compose-Down([string]$project, [string]$file, [switch]$purge){
  $args = @("compose","-p",$project,"-f",$file,"down","--remove-orphans")
  if ($purge) { $args += @("-v") }

  Write-Info ("docker {0}" -f ($args -join " "))
  & docker @args | Out-Host
}

$repoRoot = Get-RepoRoot
if ([string]::IsNullOrWhiteSpace($ComposeFile)) {
  $ComposeFile = Join-Path $repoRoot "docker-compose.yml"
}
if (-not (Test-Path $ComposeFile)) {
  throw ("No encuentro docker-compose.yml en: {0}" -f $ComposeFile)
}

Write-Info "=== DOWN (APP + DB) ==="
Write-Info ("RepoRoot = {0}" -f $repoRoot)
Write-Info ("Compose  = {0}" -f $ComposeFile)
Write-Info ("Project  = {0}" -f $ComposeProject)
if ($Purge) { Write-Warn "Purge=ON (docker compose down -v) => borrará volúmenes (pgdata)." }

# 1) Baja la app (si fue levantada por dev_up)
$pidFile = Join-Path $repoRoot ".runtime\pids\ai-bootstrap.pid"
Stop-PidFile -pidFile $pidFile

# 2) Baja docker compose (DB)
try {
  Docker-Compose-Down -project $ComposeProject -file $ComposeFile -purge:$Purge
  Write-Ok "docker compose down OK"
} catch {
  Write-Warn ("docker compose down falló: {0}" -f $_.Exception.Message)
}

Write-Info "Estado final:"
try {
  & docker compose -p $ComposeProject -f $ComposeFile ps | Out-Host
} catch {
  Write-Warn "No pude ejecutar docker compose ps (ignorable)."
}

Write-Ok "DOWN completo."
