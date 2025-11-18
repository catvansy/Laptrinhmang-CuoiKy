@echo off
setlocal enabledelayedexpansion
echo ========================================
echo  MegaChat Server Startup
echo ========================================
echo.

cd /d "%~dp0"

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

REM Stop any Java processes related to this project
echo Checking for Java processes...
for /f "tokens=2" %%a in ('jps -l ^| findstr megachat') do (
    echo Stopping Java process %%a...
    taskkill /PID %%a /F >nul 2>&1
)

timeout /t 2 /nobreak >nul

REM Start server
echo Starting MegaChat Server...
echo.
echo Server will be available at: http://localhost:8080/megachat
echo.
echo Press Ctrl+C to stop the server.
echo ========================================
echo.

mvn spring-boot:run

pause

