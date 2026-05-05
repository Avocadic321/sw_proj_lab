@echo off
REM run_all_tests.cmd - Runs all test cases (Windows)

setlocal enabledelayedexpansion

set "PROJECT_DIR=%~dp0"
set "MAIN_CLASS=software.project.Main"
set "CLASSES_DIR=%PROJECT_DIR%target\classes"
set "INPUT_DIR=%PROJECT_DIR%test-inputs"
set "OUTPUT_DIR=%PROJECT_DIR%test-outputs"

set TOTAL=0
set SKIPPED=0

set NAME_WIDTH=45

if not exist "%OUTPUT_DIR%" mkdir "%OUTPUT_DIR%"

echo Running Tests
echo.

for %%f in ("%INPUT_DIR%\*") do (
    set "filename=%%~nxf"
    set "base=%%~nf"

    REM Skip directories
    if not exist "%%f\" (

        REM Skip files that don't end with .in
        echo !filename! | findstr /i "\.in$" >nul
        if errorlevel 1 (
            echo [SKIP] Ignoring !filename! (not a .in file^)
            set /a SKIPPED+=1
        ) else (
            REM Skip files that don't start with "test"
            echo !base! | findstr /i "^test" >nul
            if errorlevel 1 (
                echo [SKIP] Ignoring !filename! (does not start with 'test'^)
                set /a SKIPPED+=1
            ) else (
                set /a TOTAL+=1
                set "output_file=%OUTPUT_DIR%\!base!.out"

                REM Pad the number
                set "num=0!TOTAL!"
                set "num=!num:~-2!"

                REM Pad the name
                set "name=!base!                              "
                set "name=!name:~0,%NAME_WIDTH%!"

                <nul set /p =[!num!] Running !name!

                java -cp "%CLASSES_DIR%" %MAIN_CLASS% "%%f" "!output_file!" 2>nul

                echo DONE
            )
        )
    )
)

echo.
if %SKIPPED% gtr 0 (
    echo %SKIPPED% file(s^) skipped.
)
echo All %TOTAL% tests completed successfully.

endlocal