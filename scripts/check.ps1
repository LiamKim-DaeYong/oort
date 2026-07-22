$ErrorActionPreference = "Stop"

$Root = Resolve-Path (Join-Path $PSScriptRoot "..")
Set-Location $Root

. "$PSScriptRoot\common.ps1"

Invoke-Checked { .\gradlew.bat ktlintCheck test build }
