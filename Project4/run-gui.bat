@echo off
REM Runs the compiled ChocAn system with the graphical interface (extra credit).
REM Same saved data as the console version. Pass --reset to discard saved data.
java -cp out chocan.Main --gui %*
