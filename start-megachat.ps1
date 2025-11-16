# MegaChat Startup Script
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   MegaChat - Starting Application" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Get script directory
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $scriptDir

# Check if port 8080 is in use
Write-Host "Checking if port 8080 is already in use..." -ForegroundColor Yellow
$portCheck = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue

if ($portCheck) {
    Write-Host "Port 8080 is already in use. Stopping existing process..." -ForegroundColor Yellow
    $processes = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue | Select-Object -ExpandProperty OwningProcess -Unique
    foreach ($pid in $processes) {
        Write-Host "Stopping process $pid..." -ForegroundColor Yellow
        Stop-Process -Id $pid -Force -ErrorAction SilentlyContinue
    }
    Start-Sleep -Seconds 2
}

Write-Host ""
Write-Host "Starting MegaChat application..." -ForegroundColor Green
Write-Host "Please wait..." -ForegroundColor Green
Write-Host ""

# Start the application
& mvn spring-boot:run

Write-Host ""
Write-Host "Application stopped. Press any key to exit..." -ForegroundColor Yellow
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

