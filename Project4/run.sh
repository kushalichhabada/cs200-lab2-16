#!/bin/sh
# Runs the compiled ChocAn system. Saved data and reports are created in this folder.
# Pass --reset to discard saved data:  ./run.sh --reset
java -cp out chocan.Main "$@"
