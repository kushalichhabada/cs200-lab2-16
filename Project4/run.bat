@echo off
REM Runs the compiled ChocAn system. Saved data and reports are created in this folder.
REM Pass --reset to discard saved data:  run.bat --reset
java -cp out chocan.Main %*
