@echo off
where gradle >nul 2>nul
if %errorlevel%==0 (
  gradle %*
  exit /b %errorlevel%
)
echo Gradle is not installed and gradle-wrapper.jar is not bundled in this generated workspace.
echo Install Gradle 8.8 or import build.gradle in IntelliJ with JDK 17, then run: gradle build
exit /b 1
