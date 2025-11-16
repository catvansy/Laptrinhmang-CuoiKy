@echo off
echo ========================================
echo    Testing MegaChat Connection
echo ========================================
echo.

echo Checking if application is running on port 8080...
netstat -ano | findstr :8080 >nul
if %errorlevel% == 0 (
    echo [OK] Port 8080 is in use - Application is running!
    echo.
    echo Testing connection to http://localhost:8080/megachat...
    echo.
    echo Opening browser...
    start http://localhost:8080/megachat
    echo.
    echo If browser shows 404 error:
    echo 1. Wait 30-60 seconds for application to fully start
    echo 2. Check console for "Started MegaChatApplication" message
    echo 3. Try again
) else (
    echo [ERROR] Port 8080 is NOT in use - Application is NOT running!
    echo.
    echo Please run start-megachat.bat first!
)

echo.
pause

