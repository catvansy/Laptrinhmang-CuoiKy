@echo off
setlocal enabledelayedexpansion
echo ========================================
echo  MegaChat Server with Cloudflare Tunnel
echo ========================================
echo.

cd /d "%~dp0"

REM Check if cloudflared exists
if not exist "cloudflared.exe" (
    echo [ERROR] cloudflared.exe not found!
    echo.
    echo Download from: https://github.com/cloudflare/cloudflared/releases/latest
    echo File: cloudflared-windows-amd64.exe - Rename to: cloudflared.exe
    echo.
    pause
    exit /b 1
)

REM Stop existing processes on port 8080
echo Checking port 8080...
netstat -ano | findstr :8080 >nul
if %errorlevel% == 0 (
    echo Stopping existing process on port 8080...
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080 ^| findstr LISTENING') do (
        taskkill /PID %%a /F >nul 2>&1
    )
    timeout /t 2 /nobreak >nul
)

REM Start server in separate window (minimized)
echo Starting MegaChat Server...
if exist "start-megachat.ps1" (
    start "MegaChat Server" /MIN powershell -ExecutionPolicy Bypass -File "start-megachat.ps1"
) else (
    start "MegaChat Server" /MIN cmd /k "mvn spring-boot:run"
)

REM Quick wait - server will be ready when tunnel connects
echo Server starting... (will be ready in ~30 seconds)
timeout /t 5 /nobreak >nul

echo.
echo ========================================
echo  Starting Cloudflare Tunnel...
echo ========================================
echo.
echo [IMPORTANT] Look for the URL below:
echo.
echo It will appear in a box like this:
echo.
echo   Your quick Tunnel has been created! Visit it at:
echo   https://xxxx-xxxx-xxxx.trycloudflare.com
echo.
echo COPY THIS URL and share: [URL]/megachat
echo.
echo Example: https://abc-123.trycloudflare.com/megachat
echo.
echo Keep this window open! Press Ctrl+C to stop.
echo ========================================
echo.

REM Start Cloudflare tunnel
cloudflared tunnel --url http://127.0.0.1:8080

echo.
echo Tunnel closed.
pause
