@echo off
cd /d "%~dp0"

set "CLASSES_DIR=target\classes"
set "INPUT_DIR=test-inputs"
set "OUTPUT_DIR=test-outputs"

if not exist "%OUTPUT_DIR%" mkdir "%OUTPUT_DIR%"

echo Running all .in files...
echo.

for %%f in ("%INPUT_DIR%\*.in") do (
    echo Running %%~nxf...
    java -cp "%CLASSES_DIR%" software.project.Main "%%f" "%OUTPUT_DIR%\%%~nf.out"
)

echo.
echo Done.