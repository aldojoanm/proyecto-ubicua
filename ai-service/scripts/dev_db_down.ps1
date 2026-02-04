[CmdletBinding()]
param(
  [string]$ComposeProject = "ai-service",
  [string]$DbService = "postgres",
  [string]$ComposeFile = ""
)

$ErrorActionPreference = "Stop"
try { [Console]::OutputEncoding = [System.Text.Encoding]::UTF8 } catch {}
$OutputEncoding = [System.Text.Encoding]::UTF8

function Write-Info($m){ Write-Host "[INFO] $m" -ForegroundColor Cyan }
function Write-OK($m){ Write-Host "[OK]  $m" -ForegroundColor Green }
function Write-Warn($m){ Write-Host "[WARN] $m" -ForegroundColor Yellow }

$repoRoot = Split-Path -Parent $PSScriptRoot
if ([string]::IsNullOrWhiteSpace($ComposeFile)) {
  $ComposeFile = Join-Path $repoRoot "docker-compose.yml"
}

if (-not (Test-Path $ComposeFile)) {
  throw "No existe compose file: $ComposeFile"
}

Write-Info ("docker compose -p {0} -f `"{1}`" stop {2}" -f $ComposeProject, $ComposeFile, $DbService)
docker compose -p $ComposeProject -f $ComposeFile stop $DbService | Out-Host

Write-OK "DB detenido (sin borrar volumen)."
