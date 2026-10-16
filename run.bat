@echo off
title TrailSafe - Smart Trekking & Tourist Safety System
echo ========================================================
echo  TrailSafe - Smart Trekking & Tourist Safety System
echo ========================================================
echo Compiling Java Backend...
if not exist "bin" mkdir "bin"
dir /s /b src\main\java\*.java > sources.txt
javac -encoding UTF-8 -d bin @sources.txt
del sources.txt
if %errorlevel% neq 0 (
    echo Compilation failed! Please check your JDK installation.
    pause
    exit /b %errorlevel%
)
echo Starting TrailSafe Server on port 8080...
start "" http://localhost:8080
java -cp bin com.trailsafe.server.TrailSafeServer 8080
pause
