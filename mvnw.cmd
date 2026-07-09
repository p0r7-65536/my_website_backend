@echo off
setlocal EnableDelayedExpansion

where mvn >nul 2>nul
if %ERRORLEVEL% == 0 (
    call mvn %*
    exit /b !ERRORLEVEL!
)

set "BASE_DIR=%~dp0"
set "WRAPPER_PROPS=%BASE_DIR%.mvn\wrapper\maven-wrapper.properties"

for /f "tokens=1,* delims==" %%A in ('findstr /b "distributionUrl=" "%WRAPPER_PROPS%"') do set "DIST_URL=%%B"
for %%F in ("%DIST_URL%") do set "DIST_ZIP=%%~nxF"
set "DIST_NAME=%DIST_ZIP:.zip=%"
set "MVN_HOME_NAME=%DIST_NAME:-bin=%"
set "DIST_DIR=%BASE_DIR%.mvn\wrapper\dists\%DIST_NAME%"
set "MVN_BIN=%DIST_DIR%\%MVN_HOME_NAME%\bin\mvn.cmd"

if not exist "%MVN_BIN%" (
    if not exist "%DIST_DIR%" mkdir "%DIST_DIR%"
    set "ZIP_FILE=%DIST_DIR%\%DIST_ZIP%"
    powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -Uri '%DIST_URL%' -OutFile '%ZIP_FILE%'"
    if not !ERRORLEVEL! == 0 exit /b !ERRORLEVEL!
    powershell -NoProfile -ExecutionPolicy Bypass -Command "Expand-Archive -Force -Path '%ZIP_FILE%' -DestinationPath '%DIST_DIR%'"
    if not !ERRORLEVEL! == 0 exit /b !ERRORLEVEL!
)

call "%MVN_BIN%" %*
exit /b !ERRORLEVEL!
