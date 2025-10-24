# Script để clean và rebuild project
# Chạy script này để xóa cache và rebuild project

Write-Host "🧹 Cleaning project..." -ForegroundColor Yellow

# Xóa target folder
if (Test-Path "target") {
    Remove-Item -Recurse -Force "target"
    Write-Host "✅ Deleted target folder" -ForegroundColor Green
} else {
    Write-Host "ℹ️ Target folder not found" -ForegroundColor Blue
}

# Xóa .mvn folder nếu có
if (Test-Path ".mvn") {
    Remove-Item -Recurse -Force ".mvn"
    Write-Host "✅ Deleted .mvn folder" -ForegroundColor Green
}

# Xóa các file cache khác
$cacheFiles = @("*.class", "*.jar", "*.war")
foreach ($pattern in $cacheFiles) {
    Get-ChildItem -Path . -Recurse -Name $pattern -ErrorAction SilentlyContinue | ForEach-Object {
        Remove-Item $_ -Force -ErrorAction SilentlyContinue
    }
}

Write-Host "✅ Cleaned cache files" -ForegroundColor Green

Write-Host "🔨 Rebuilding project..." -ForegroundColor Yellow

# Nếu có Maven wrapper
if (Test-Path "mvnw.cmd") {
    Write-Host "Using Maven wrapper..." -ForegroundColor Blue
    .\mvnw.cmd clean compile
} else {
    Write-Host "❌ Maven wrapper not found. Please run manually:" -ForegroundColor Red
    Write-Host "   mvn clean compile" -ForegroundColor White
    Write-Host "   mvn spring-boot:run" -ForegroundColor White
}

Write-Host "🎉 Clean and rebuild completed!" -ForegroundColor Green
Write-Host "Now you can run: mvn spring-boot:run" -ForegroundColor Cyan


