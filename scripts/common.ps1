function Invoke-Checked {
    param(
        [Parameter(Mandatory = $true)]
        [ScriptBlock] $Command
    )

    & $Command

    if ($LASTEXITCODE -ne 0) {
        exit $LASTEXITCODE
    }
}
