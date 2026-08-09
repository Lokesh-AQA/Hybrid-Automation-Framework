@echo off

title Allure Temporary Report

cd /d %~dp0

echo.
echo ===========================================================
echo             ALLURE TEMPORARY REPORT
echo ===========================================================
echo.

REM ===========================================================
REM STEP 1 - FIND LATEST ALLURE EXECUTION
REM ===========================================================

echo [STEP 1] Finding latest Allure execution...
echo.

set "ALLURE_ROOT=%~dp0Allure-Reports"

if not exist "%ALLURE_ROOT%" (
    echo.
    echo #######################################################
    echo ERROR: Allure-Reports directory was not found.
    echo.
    echo Expected:
    echo %ALLURE_ROOT%
    echo #######################################################
    pause
    exit /b 1
)

REM ===========================================================
REM FIND LATEST DATE-TIME DIRECTORY
REM ===========================================================

set "LATEST_EXECUTION="

for /f "delims=" %%D in ('dir "%ALLURE_ROOT%" /b /ad /o-n 2^>nul') do (
    set "LATEST_EXECUTION=%%D"
    goto :LATEST_FOUND
)

:LATEST_FOUND

if "%LATEST_EXECUTION%"=="" (
    echo.
    echo #######################################################
    echo ERROR: No Allure execution directory was found.
    echo #######################################################
    pause
    exit /b 1
)

set "EXECUTION_DIR=%ALLURE_ROOT%\%LATEST_EXECUTION%"

set "RESULTS_DIR=%EXECUTION_DIR%\Allure-results"

echo Latest Execution:
echo %LATEST_EXECUTION%
echo.

echo Allure Results:
echo %RESULTS_DIR%
echo.

REM ===========================================================
REM VERIFY RESULTS DIRECTORY
REM ===========================================================

if not exist "%RESULTS_DIR%" (
    echo.
    echo #######################################################
    echo ERROR: Allure-results directory was not found.
    echo.
    echo Expected:
    echo %RESULTS_DIR%
    echo #######################################################
    pause
    exit /b 1
)

REM ===========================================================
REM STEP 2 - START TEMPORARY ALLURE REPORT
REM ===========================================================

echo [STEP 2] Starting Allure Temporary Report...
echo.

call allure serve "%RESULTS_DIR%"

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
echo.

pause