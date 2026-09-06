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
echo       Allure Permanent Report
echo ==========================================
echo.
echo Latest Execution: %LATEST%
echo.

if exist "Allure-Reports\%LATEST%\Allure-Report" (
    echo Existing Allure Report found.
    echo Opening existing report...
    echo.
    allure open "Allure-Reports\%LATEST%\Allure-Report"
    pause
    exit /b 0
)

echo Generating Permanent Allure Report...
echo.

allure generate "Allure-Reports\%LATEST%\Allure-Results" -o "Allure-Reports\%LATEST%\Allure-Report"

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: Allure Report generation failed.
    pause
    exit /b 1
)

echo.
echo Permanent Report Generated Successfully.
echo.
echo Opening Allure Report...
echo.

allure open "Allure-Reports\%LATEST%\Allure-Report"

pause

REM Explanation
REM ============================================================
REM Line_1 = @echo off
REM @echo off = Hides BAT commands while executing.
REM Keeps the Command Prompt output clean.
REM ============================================================


REM Line_2 = setlocal
REM setlocal = Creates a local environment for BAT variables.
REM Variables created here will not affect the parent CMD session.


REM Line_3 = cd /d "%~dp0"
REM cd = Change Directory.
REM /d = Allows changing both drive and directory.
REM %~dp0 = Drive + directory path where this BAT file exists.
REM This makes the BAT file work from the project root.
REM Example:
REM D:\Projects\July30\Hybrid-Automation-Framework\


REM Line_4 = for /f "delims=" %%F in ('dir "Allure-Reports" /ad /b /o-d') do (
REM for /f = Processes the text output of another command.
REM "delims=" = Keeps the complete folder name as one value.
REM %%F = Temporary variable used by the FOR loop.
REM dir "Allure-Reports" = Lists the Allure-Reports folder.
REM /ad = Shows directories/folders only.
REM /b = Shows only folder names.
REM /o-d = Sorts folders by date in descending order.
REM Newest folder appears first.


REM Line_5 = set "LATEST=%%F"
REM set = Creates or assigns a variable.
REM LATEST = Variable used to store the latest execution folder.
REM %%F = Current folder returned by the FOR loop.
REM Example:
REM LATEST=2026-09-06_18-22-59


REM Line_6 = goto :FOUND
REM goto = Jumps to another location in the BAT file.
REM :FOUND = Destination label.
REM Stops the FOR loop after the first folder.
REM Since folders are sorted newest first,
REM the first folder is the latest execution.


REM Line_7 = )
REM Closes the FOR loop.


REM Line_8 = :FOUND
REM :FOUND = Label.
REM Execution continues from this location after GOTO.


REM Line_9 = if not defined LATEST (
REM if = Performs a condition check.
REM not defined = Checks whether the variable does not exist.
REM LATEST = Variable containing the latest execution folder.
REM If no folder was found, LATEST will not be defined.


REM Line_10 = echo ERROR: No Allure execution folder found.
REM echo = Displays text in the Command Prompt.
REM Displays an error message when no execution folder exists.


REM Line_11 = echo.
REM Displays a blank line.


REM Line_12 = pause
REM pause = Pauses the BAT execution.
REM Allows the user to read the error message.


REM Line_13 = exit /b 1
REM exit = Stops execution of the BAT file.
REM /b = Exits only the current BAT script.
REM 1 = Returns an error/failure status.


REM Line_14 = )
REM Closes the IF block.


REM Line_15 = echo ==========================================
REM Displays a separator line.


REM Line_16 = echo       Allure Permanent Report
REM Displays the title of the BAT execution.


REM Line_17 = echo ==========================================
REM Displays another separator line.


REM Line_18 = echo.
REM Displays a blank line.


REM Line_19 = echo Latest Execution: %LATEST%
REM Displays the latest execution folder.
REM %LATEST% is replaced with the actual folder name.
REM Example:
REM Latest Execution: 2026-09-06_18-22-59


REM Line_20 = echo.
REM Displays a blank line.


REM Line_21 = if exist "Allure-Reports\%LATEST%\Allure-Report" (
REM if exist = Checks whether a file or folder exists.
REM Checks whether the permanent Allure-Report folder
REM already exists for the latest execution.
REM Example:
REM Allure-Reports\2026-09-06_18-22-59\Allure-Report


REM Line_22 = echo Existing Allure Report found.
REM Displays a message when the report already exists.


REM Line_23 = echo Opening existing report...
REM Displays a message indicating that the existing report
REM will be opened instead of generating it again.


REM Line_24 = echo.
REM Displays a blank line.


REM Line_25 = allure open "Allure-Reports\%LATEST%\Allure-Report"
REM allure = Calls the Allure Command Line Tool.
REM open = Opens an already generated Allure HTML report.
REM The path points to the existing permanent report.
REM Example:
REM allure open "Allure-Reports\2026-09-06_18-22-59\Allure-Report"


REM Line_26 = pause
REM Keeps the Command Prompt window open.


REM Line_27 = exit /b 0
REM Stops the BAT file successfully.
REM /b = Exits only the BAT script.
REM 0 = Success status.


REM Line_28 = )
REM Closes the IF EXIST block.


REM Line_29 = echo Generating Permanent Allure Report...
REM Displays a message that the permanent report
REM is going to be generated.


REM Line_30 = echo.
REM Displays a blank line.


REM Line_31 = allure generate "Allure-Reports\%LATEST%\Allure-Results" -o "Allure-Reports\%LATEST%\Allure-Report"
REM allure = Calls the Allure Command Line Tool.
REM generate = Generates a permanent Allure HTML report.
REM First path = Source Allure-Results folder.
REM -o = Specifies the output directory.
REM Second path = Destination Allure-Report folder.
REM Example:
REM allure generate "Allure-Reports\2026-09-06_18-22-59\Allure-Results" -o "Allure-Reports\2026-09-06_18-22-59\Allure-Report"
REM This creates the permanent HTML report.


REM Line_32 = if %ERRORLEVEL% NEQ 0 (
REM ERRORLEVEL = Contains the return code of the previous command.
REM NEQ = Not Equal To.
REM 0 normally means success.
REM Therefore, this checks whether Allure generation failed.


REM Line_33 = echo.
REM Displays a blank line.


REM Line_34 = echo ERROR: Allure Report generation failed.
REM Displays an error message if report generation failed.


REM Line_35 = pause
REM Keeps the Command Prompt open so the error can be read.


REM Line_36 = exit /b 1
REM Stops the BAT file with an error status.


REM Line_37 = )
REM Closes the ERRORLEVEL IF block.


REM Line_38 = echo.
REM Displays a blank line.


REM Line_39 = echo Permanent Report Generated Successfully.
REM Confirms that the permanent Allure report was generated.


REM Line_40 = echo.
REM Displays a blank line.


REM Line_41 = echo Opening Allure Report...
REM Displays a message before opening the report.


REM Line_42 = echo.
REM Displays a blank line.


REM Line_43 = allure open "Allure-Reports\%LATEST%\Allure-Report"
REM allure = Calls the Allure Command Line Tool.
REM open = Opens the generated permanent Allure report.
REM Uses the latest execution's Allure-Report folder.


REM Line_44 = pause
REM Keeps the Command Prompt window open.
REM Press any key to close it.