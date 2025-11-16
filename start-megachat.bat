@echo off
echo ========================================
echo    MegaChat - Starting Application
echo ========================================
echo.

cd /d "%~dp0"

echo Checking if port 8080 is already in use...
netstat -ano | findstr :8080 >nul
if %errorlevel% == 0 (
    echo Port 8080 is already in use. Stopping existing process...
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080 ^| findstr LISTENING') do (
        echo Stopping process %%a...
        taskkill /PID %%a /F >nul 2>&1
    )
    timeout /t 2 /nobreak >nul
)

echo.
echo Starting MegaChat application...
echo Please wait...
echo.

call mvn spring-boot:run

pause

