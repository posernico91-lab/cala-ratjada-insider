# =============================================================================
# BUILD ALL 25 COUNTRY AABs (Android App Bundles) - Windows PowerShell
# =============================================================================
# Usage:
#   .\build-all-aabs.ps1              # Build ALL 25 flavors
#   .\build-all-aabs.ps1 -Flavor germany  # Build single flavor
# =============================================================================

param(
    [string]$Flavor = ""
)

$ErrorActionPreference = "Stop"

$Flavors = @(
    "germany", "usa", "brazil", "peru", "mexico", "uk", "albania", "georgia",
    "southafrica", "nigeria", "kenya", "egypt", "botswana", "capeverde",
    "morocco", "tanzania", "vietnam", "bhutan", "thailand", "tajikistan",
    "saudiarabia", "dubai", "japan", "australia", "europe"
)

$OutputDir = "build\aab-output"
if (-not (Test-Path $OutputDir)) { New-Item -ItemType Directory -Path $OutputDir -Force | Out-Null }

function Build-AAB {
    param([string]$FlavorName)

    $capitalized = $FlavorName.Substring(0,1).ToUpper() + $FlavorName.Substring(1)
    $task = "bundle${capitalized}Release"

    Write-Host "=============================================" -ForegroundColor Cyan
    Write-Host "  Building AAB: $FlavorName" -ForegroundColor Yellow
    Write-Host "  Task: $task" -ForegroundColor Gray
    Write-Host "=============================================" -ForegroundColor Cyan

    & .\gradlew.bat $task --no-daemon
    if ($LASTEXITCODE -ne 0) {
        Write-Host "  FEHLER bei $FlavorName!" -ForegroundColor Red
        return
    }

    $aabPath = "app\build\outputs\bundle\${FlavorName}Release\app-${FlavorName}-release.aab"
    if (Test-Path $aabPath) {
        Copy-Item $aabPath "$OutputDir\${FlavorName}-release.aab" -Force
        $size = (Get-Item "$OutputDir\${FlavorName}-release.aab").Length / 1MB
        Write-Host "  -> $OutputDir\${FlavorName}-release.aab ($([math]::Round($size, 1)) MB)" -ForegroundColor Green
    } else {
        Write-Host "  WARNUNG: AAB nicht gefunden: $aabPath" -ForegroundColor Yellow
    }
    Write-Host ""
}

# Main
if ($Flavor) {
    Write-Host "Baue einzelnen Flavor: $Flavor" -ForegroundColor Green
    Build-AAB -FlavorName $Flavor
} else {
    Write-Host "Baue alle 25 Länder-AABs..." -ForegroundColor Green
    Write-Host ""
    foreach ($f in $Flavors) {
        Build-AAB -FlavorName $f
    }
}

Write-Host "=============================================" -ForegroundColor Cyan
Write-Host "  BUILD KOMPLETT" -ForegroundColor Green
Write-Host "  AABs in: $OutputDir\" -ForegroundColor Green
Write-Host "=============================================" -ForegroundColor Cyan
Get-ChildItem $OutputDir -Filter "*.aab" | Format-Table Name, @{N="MB";E={[math]::Round($_.Length/1MB,1)}} -AutoSize
