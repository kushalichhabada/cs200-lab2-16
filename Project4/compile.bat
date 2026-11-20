@echo off
REM Compiles the ChocAn system into the "out" folder. Requires a JDK (Java 11 or newer) on the PATH.
if not exist out mkdir out
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
if errorlevel 1 (
    del sources.txt
    echo Compilation FAILED.
    exit /b 1
)
del sources.txt
echo Compilation successful. Run the program with run.bat
