[CmdletBinding()]
param(
  [string]$BaseUrl = "http://127.0.0.1:8091",
  [int]$TimeoutSec = 5,
  [int]$Retries = 2
)

$ErrorActionPreference = "Stop"
try { [Console]::OutputEncoding = [System.Text.Encoding]::UTF8 } catch {}
$OutputEncoding = [System.Text.Encoding]::UTF8

function Write-Info($m){ Write-Host ("[INFO] {0}" -f $m) -ForegroundColor Cyan }
function Write-Ok($m){   Write-Host ("[OK]  {0}" -f $m) -ForegroundColor Green }
function Write-Warn($m){ Write-Host ("[WARN] {0}" -f $m) -ForegroundColor Yellow }
function Write-Err($m){  Write-Host ("[ERR] {0}" -f $m) -ForegroundColor Red }

function Test-TcpPort([string]$targetHost,[int]$targetPort,[int]$ms=800){
  try {
    $client = New-Object System.Net.Sockets.TcpClient
    $iar = $client.BeginConnect($targetHost,$targetPort,$null,$null)
    if (-not $iar.AsyncWaitHandle.WaitOne($ms,$false)) { $client.Close(); return $false }
    $client.EndConnect($iar)
    $client.Close()
    return $true
  } catch { return $false }
}

function Get-Health([string]$url,[int]$timeoutSec){
  try {
    $out = & curl.exe --noproxy "*" -sS --max-time $timeoutSec $url 2>$null
    if (-not $out) { return $null }
    if ($out -match '"status"\s*:\s*"UP"') { return "UP" }
    if ($out -match '"status"\s*:\s*"DOWN"') { return "DOWN" }
    return "UNKNOWN"
  } catch { return $null }
}

$BaseUrl = $BaseUrl.TrimEnd("/")
$healthUrl = ("{0}/actuator/health" -f $BaseUrl)

Write-Info "=== Smoke: Health ==="
Write-Info ("Trying: {0}" -f $healthUrl)

$uri = [Uri]$BaseUrl
$targetHost = $uri.Host
$targetPort = $uri.Port

if (-not (Test-TcpPort -targetHost $targetHost -targetPort $targetPort -ms 900)) {
  Write-Warn ("Port not reachable: {0}:{1} (TCP connect failed)" -f $targetHost, $targetPort)
}

$last = $null
for ($i=0; $i -le $Retries; $i++) {
  $last = Get-Health -url $healthUrl -timeoutSec $TimeoutSec
  if ($last -eq "UP") {
    Write-Ok ("Health OK (UP) => {0}" -f $healthUrl)
    exit 0
  }
  Start-Sleep -Milliseconds 700
}

Write-Err ("Health failed. Last={0}" -f $last)
Write-Info "Diagnóstico rápido:"
Write-Host ("  1) netstat -ano | findstr :{0}" -f $targetPort)
Write-Host ("  2) curl.exe --noproxy ""*"" {0}" -f $healthUrl)
throw "Health not OK."
