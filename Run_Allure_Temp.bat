@echo off
setlocal

cd /d "%~dp0"

for /f "delims=" %%F in ('dir "Allure-Reports" /ad /b /o-d') do (
    set "LATEST=%%F"
    goto :FOUND
)

:FOUND

if not defined LATEST (
    echo ERROR: No Allure execution folder found.
    echo.
    pause
    exit /b 1
)

echo ==========================================
echo       Allure Temporary Report
echo ==========================================
echo.
echo Latest Execution: %LATEST%
echo.
echo Allure Results:
echo Allure-Reports\%LATEST%\Allure-Results
echo.
echo Starting Allure Temporary Report...
echo.

allure serve "Allure-Reports\%LATEST%\Allure-Results"

echo.
echo ==========================================
echo       Allure Report Closed
echo ==========================================
pause


REM Explanation
REM ============================================================
REM Line_1 = @echo off
REM @echo off = Hides BAT commands while executing.
REM It keeps the Command Prompt output clean.
REM ============================================================


REM Line_2 = setlocal
REM setlocal = Creates a local environment for variables.
REM Variables created in this BAT file won't affect
REM the parent CMD session.


REM Line_3 = cd /d "%~dp0"
REM cd = Change Directory.
REM /d = Allows changing the drive as well as the directory.
REM %~dp0 = Drive + directory path where this BAT file is located.
REM This makes the BAT file work from the project root.
REM Example:
REM D:\Projects\July30\Hybrid-Automation-Framework\


REM Line_4 = for /f "delims=" %%F in ('dir "Allure-Reports" /ad /b /o-d') do (
REM for /f = Processes text returned by another command.
REM "delims=" = Keeps the complete folder name as one value.
REM %%F = Temporary variable used by the FOR loop.
REM dir "Allure-Reports" = Lists contents of Allure-Reports.
REM /ad = Shows directories/folders only.
REM /b = Shows only folder names.
REM /o-d = Sorts folders by date in descending order.
REM Newest folder appears first.
REM The FOR loop processes the folders one by one.


REM Line_5 = set "LATEST=%%F"
REM set = Creates or assigns a variable.
REM LATEST = Name of our variable.
REM %%F = Current folder name returned by the FOR loop.
REM Stores the latest execution folder in LATEST.
REM Example:
REM LATEST=2026-09-06_18-22-59


REM Line_6 = goto :FOUND
REM goto = Jumps to another location in the BAT file.
REM :FOUND = Label representing the destination.
REM The loop stops after finding the first folder.
REM Because /o-d sorts newest first,
REM the first folder is the latest execution.


REM Line_7 = )
REM Closes the FOR loop block.


REM Line_8 = :FOUND
REM :FOUND = Label.
REM The GOTO command jumps execution to this location.
REM Processing continues from this point.


REM Line_9 = if not defined LATEST (
REM if = Performs a condition check.
REM not defined = Checks whether the variable does NOT exist.
REM LATEST = Variable containing the latest execution folder.
REM If LATEST does not exist, no execution folder was found.


REM Line_10 = echo ERROR: No Allure execution folder found.
REM echo = Displays text in the Command Prompt.
REM Displays an error message when no execution folder exists.


REM Line_11 = echo.
REM echo. = Prints a blank line.
REM Used to make Command Prompt output easier to read.


REM Line_12 = pause
REM pause = Pauses the BAT execution.
REM Allows the user to read the error message
REM before the Command Prompt window closes.


REM Line_13 = exit /b 1
REM exit = Stops execution.
REM /b = Exits only the current BAT file.
REM 1 = Returns an error/failure status.


REM Line_14 = )
REM Closes the IF block.


REM Line_15 = echo ==========================================
REM echo = Displays the separator line in Command Prompt.
REM Used only for better output formatting.


REM Line_16 = echo       Allure Temporary Report
REM Displays the title "Allure Temporary Report".
REM This does not affect Allure functionality.


REM Line_17 = echo ==========================================
REM Displays another separator line.


REM Line_18 = echo.
REM Prints a blank line.


REM Line_19 = echo Latest Execution: %LATEST%
REM Displays the latest execution folder.
REM %LATEST% = Value stored in the LATEST variable.
REM Example:
REM Latest Execution: 2026-09-06_18-22-59


REM Line_20 = echo.
REM Prints a blank line.


REM Line_21 = echo Allure Results:
REM Displays the "Allure Results" heading.


REM Line_22 = echo Allure-Reports\%LATEST%\Allure-Results
REM Displays the complete path of the Allure result folder.
REM %LATEST% is replaced with the actual latest folder name.
REM Example:
REM Allure-Reports\2026-09-06_18-22-59\Allure-Results


REM Line_23 = echo.
REM Prints a blank line.


REM Line_24 = echo Starting Allure Temporary Report...
REM Displays a message before starting the Allure server.


REM Line_25 = echo.
REM Prints a blank line.


REM Line_26 = allure serve "Allure-Reports\%LATEST%\Allure-Results"
REM allure = Calls the Allure Command Line Tool.
REM serve = Creates and serves a temporary Allure report.
REM The specified folder contains the raw Allure result files.
REM %LATEST% dynamically points to the latest execution.
REM Example:
REM allure serve "Allure-Reports\2026-09-06_18-22-59\Allure-Results"
REM Allure starts a local web server.
REM The report opens in the browser.
REM The report remains available while the Allure server is running.


REM Line_27 = echo.
REM Prints a blank line after the Allure server stops.


REM Line_28 = echo ==========================================
REM Displays a separator line.


REM Line_29 = echo       Allure Report Closed
REM Displays a message indicating that the Allure server has stopped.


REM Line_30 = echo ==========================================
REM Displays another separator line.


REM Line_31 = pause
REM Pauses the Command Prompt.
REM Allows the user to read the final message.
REM Press any key to close the window.