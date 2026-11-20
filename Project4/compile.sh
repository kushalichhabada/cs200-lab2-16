#!/bin/sh
# Compiles the ChocAn system into the "out" folder. Requires a JDK (Java 11 or newer) on the PATH.
mkdir -p out
find src -name "*.java" > sources.txt
if javac -d out @sources.txt; then
    rm -f sources.txt
    echo "Compilation successful. Run the program with ./run.sh"
else
    rm -f sources.txt
    echo "Compilation FAILED."
    exit 1
fi
