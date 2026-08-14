$ErrorActionPreference = "Stop"

$Root = Resolve-Path (Join-Path $PSScriptRoot "..")
Set-Location $Root

. "$PSScriptRoot\common.ps1"

Invoke-Checked { docker info }
Invoke-Checked { .\gradlew.bat :mock-server:bootJar }
Invoke-Checked { docker compose up -d --build postgres mock-server }
