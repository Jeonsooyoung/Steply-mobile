@echo off
setlocal
cd /d "%~dp0\.."

echo [Steply] Stopping Gradle daemons...
call gradlew.bat --stop

echo [Steply] Removing local build caches...
if exist .gradle rmdir /s /q .gradle
if exist app\build rmdir /s /q app\build
if exist build rmdir /s /q build

echo [Steply] Rebuilding debug APK...
call gradlew.bat :app:assembleDebug --no-daemon --stacktrace
endlocal
