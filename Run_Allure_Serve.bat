@echo off

title Allure Temporary Report

cd /d %~dp0

echo.
echo ===========================================================
echo             ALLURE TEMPORARY REPORT
echo ===========================================================
echo.

echo [STEP 1] Starting Allure Temporary Report...
echo.

call allure serve allure-results

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo #######################################################
    echo ERROR: Failed to start Allure Temporary Report.
    echo Error Code : %ERRORLEVEL%
    echo #######################################################
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo ===========================================================
echo Temporary Report Closed.
echo ===========================================================

pause