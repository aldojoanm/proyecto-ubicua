param(
  [string]$Profile = "dev",
  [int]$Port = 8091,
  [string]$BaseUrl = "",
  [int]$HealthTimeoutSec = 180,
  [switch]$RestartIfSameApp
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

# Health check robusto: usa curl.exe (NO Invoke-WebRequest)
function Get-HealthStatus([string]$url){
  try {
    $out = & curl.exe --noproxy "*" -sS --max-time 3 $url 2>$null
    if (-not $out) { return $null }
    if ($out -match '"status"\s*:\s*"UP"') { return "UP" }
    if ($out -match '"status"\s*:\s*"DOWN"') { return "DOWN" }
    return "UNKNOWN"
  } catch {
    return $null
  }
}

function Wait-Health([string]$url, [int]$timeoutSec){
  $deadline = (Get-Date).AddSeconds($timeoutSec)
  while ((Get-Date) -lt $deadline) {
    $s = Get-HealthStatus $url
    if ($s -eq "UP") { return $true }
    Start-Sleep -Milliseconds 700
  }
  return $false
}

$repoRoot = Get-RepoRoot
if ([string]::IsNullOrWhiteSpace($BaseUrl)) { $BaseUrl = "http://127.0.0.1:$Port" }
$BaseUrl = $BaseUrl.TrimEnd("/")
$healthUrl = "$BaseUrl/actuator/health"

$Name = "ai-service"
Write-Info "Dev up: profile=$Profile port=$Port base=$BaseUrl"
Write-Info "RepoRoot=$repoRoot"

# --- Port guard (enterprise-safe) ---
$pids = Get-PidsListening -p $Port
if ($pids.Count -gt 0) {
  if ($RestartIfSameApp) {
    $pidFile = Join-Path $repoRoot ".runtime\pids\ai-bootstrap.pid"
    if (Test-Path $pidFile) {
      $oldPid = (Get-Content $pidFile -ErrorAction SilentlyContinue | Select-Object -First 1)
      if ($oldPid -and ($pids -contains [int]$oldPid)) {
        Write-Warn ("${Name}: port $Port ocupado por PID {0} (segun pidfile). Lo apago y continuo..." -f $oldPid)
        try { Stop-Process -Id [int]$oldPid -Force -ErrorAction SilentlyContinue } catch {}
        Start-Sleep -Milliseconds 500
        $pids = Get-PidsListening -p $Port
      }
    }
  }

  if ($pids.Count -gt 0) {
    throw ("${Name}: El puerto $Port ya esta en uso. PID(s): {0}" -f ($pids -join ", "))
  }
}
Write-Ok ("${Name}: puerto $Port libre")

$mvnw = Join-Path $repoRoot "mvnw.cmd"
if (-not (Test-Path $mvnw)) { throw "No encuentro mvnw.cmd en $repoRoot" }

Write-Info "Building (ai-bootstrap + deps) with Maven (skipTests)..."
Push-Location $repoRoot
try {
  & $mvnw clean package -DskipTests | Out-Host
} finally { Pop-Location }

$jarDir = Join-Path $repoRoot "modules\ai-bootstrap\target"
$jar = Get-ChildItem $jarDir -Filter "ai-bootstrap-*.jar" -File -ErrorAction Stop |
       Sort-Object LastWriteTime -Descending | Select-Object -First 1
if (-not $jar) { throw "No encuentro jar en $jarDir" }

$rtDir = Join-Path $repoRoot ".runtime"
$pidDir = Join-Path $rtDir "pids"
$logDir = Join-Path $rtDir "logs"
New-Item -ItemType Directory -Force -Path $pidDir,$logDir | Out-Null

$pidFile = Join-Path $pidDir "ai-bootstrap.pid"
$outLog  = Join-Path $logDir "ai-bootstrap.out.log"
$errLog  = Join-Path $logDir "ai-bootstrap.err.log"

Write-Info ("Jar: {0}" -f $jar.FullName)
Write-Info "Starting ai-bootstrap (background)..."

$args = @(
  "-jar", $jar.FullName,
  "--spring.profiles.active=$Profile",
  "--server.port=$Port"
)

$p = Start-Process -FilePath "java" -ArgumentList $args -PassThru `
  -RedirectStandardOutput $outLog -RedirectStandardError $errLog -WindowStyle Hidden

Write-Ok ("Started java PID={0}" -f $p.Id)
Set-Content -Path $pidFile -Value $p.Id -Encoding ascii
Write-Ok ("PID saved: {0}" -f $pidFile)
Write-Info ("Logs: {0} / {1}" -f $outLog, $errLog)

Write-Info ("Waiting for health: {0} (timeout {1}s)" -f $healthUrl, $HealthTimeoutSec)
if (Wait-Health -url $healthUrl -timeoutSec $HealthTimeoutSec) {
  Write-Ok ("Health OK: {0} => UP" -f $healthUrl)
  exit 0
}

Write-Err "Health not ready within timeout."
Write-Err "Try manual:"
Write-Host ("  curl.exe --noproxy ""*"" {0}" -f $healthUrl)

Write-Err "Tail stderr:"
if (Test-Path $errLog) { Get-Content $errLog -Tail 80 | Out-Host }
Write-Err "Tail stdout:"
if (Test-Path $outLog) { Get-Content $outLog -Tail 80 | Out-Host }

try { Stop-Process -Id $p.Id -Force -ErrorAction SilentlyContinue } catch {}
throw "dev_up failed (health not UP)."
