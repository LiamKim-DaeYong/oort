$ErrorActionPreference = "Stop"

$Root = Resolve-Path (Join-Path $PSScriptRoot "..")
Set-Location $Root

$requestBody = @{
    channel = "EMAIL"
    recipient = "user@example.com"
    title = "Order completed"
    content = "Your order has been completed."
} | ConvertTo-Json

$created = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/v1/notifications" -ContentType "application/json" -Body $requestBody

if ($created.status -ne "DISPATCHED") {
    throw "Expected a DISPATCHED notification but received $($created.status)."
}

$stored = Invoke-RestMethod -Method Get -Uri "http://localhost:8080/api/v1/notifications/$($created.id)"

if ($stored.id -ne $created.id -or $stored.status -ne "DISPATCHED") {
    throw "Stored notification does not match the created notification."
}

Write-Output "Notification Walking Skeleton smoke test passed: $($created.id)"
