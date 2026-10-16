# ========================================================
# TrailSafe - Smart Trekking & Tourist Safety System
# PowerShell Launcher
# ========================================================

Write-Host "========================================================" -ForegroundColor Cyan
Write-Host " TrailSafe - Smart Trekking & Tourist Safety System" -ForegroundColor Green
Write-Host "========================================================" -ForegroundColor Cyan

if (-not (Test-Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

Write-Host "Compiling Java Backend..." -ForegroundColor Yellow
$javaFiles = (Get-ChildItem -Path "src/main/java" -Filter "*.java" -Recurse).FullName
javac -encoding UTF-8 -d bin $javaFiles

if ($LASTEXITCODE -ne 0) {
    Write-Host "Compilation failed!" -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host "Compilation successful!" -ForegroundColor Green
Write-Host "Starting TrailSafe Web Server on http://localhost:8080 ..." -ForegroundColor Cyan

Start-Process "http://localhost:8080"
java -cp bin com.trailsafe.server.TrailSafeServer 8080
