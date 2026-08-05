@echo off
title Allure Report Generator

cd /d %~dp0

echo.
echo ===========================================================
echo              ALLURE REPORT GENERATOR
echo ===========================================================
echo.

echo [STEP 1] Generating Allure Report...
echo.

call allure generate allure-results --clean -o allure-report

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo #######################################################
    echo ERROR: Failed to generate Allure Report.
    echo Error Code : %ERRORLEVEL%
    echo #######################################################
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo [SUCCESS] Report Generated Successfully.
echo.

echo [STEP 2] Opening Allure Report...
echo.

call allure open allure-report

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo #######################################################
    echo ERROR: Failed to open Allure Report.
    echo Error Code : %ERRORLEVEL%
    echo #######################################################
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo ===========================================================
echo        Allure Report Opened Successfully
echo ===========================================================

pause