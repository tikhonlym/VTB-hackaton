@echo off
echo Testing loader functionality on FallbackFragment
echo.

echo Building the app...
call gradlew assembleDebug
if %errorlevel% neq 0 (
    echo Build failed!
    pause
    exit /b 1
)

echo.
echo Build successful! The loader should now:
echo 1. Show immediately when FallbackFragment loads
echo 2. Stay visible while checking permissions
echo 3. Stay visible while making API calls
echo 4. Hide when:
echo    - Permissions are denied
echo    - No interview ID is found
echo    - API call fails
echo    - Navigation to ChooseModeFragment starts
echo.

echo To test the loader:
echo 1. Install the app: adb install app\build\outputs\apk\debug\app-debug.apk
echo 2. Launch the app
echo 3. Observe the loading spinner and text at the center of the screen
echo 4. The loader should appear immediately and stay until navigation or error
echo.

echo Test scenarios:
echo - With valid deep link: Loader shows until API response
echo - Without deep link: Loader shows until error message appears
echo - Permission denied: Loader hides when permissions are denied
echo.

pause
