@echo off
echo Testing deep link loader flow when app is already open
echo.

echo Building the app...
call gradlew assembleDebug
if %errorlevel% neq 0 (
    echo Build failed!
    pause
    exit /b 1
)

echo.
echo Build successful! Now testing the deep link flow:
echo.

echo Test scenario:
echo 1. Install and launch the app
echo 2. App should show FallbackFragment with loader
echo 3. Wait for initial processing to complete
echo 4. Send a new deep link while app is open
echo 5. App should show loader again and make new API call
echo.

echo Deep link test commands:
echo.
echo Test with valid ID:
echo adb shell am start -W -a android.intent.action.VIEW -d "https://vtbhackaton.app/interview/123" com.preload.vtb_hackaton
echo.
echo Test with different ID:
echo adb shell am start -W -a android.intent.action.VIEW -d "https://vtbhackaton.app/interview/456" com.preload.vtb_hackaton
echo.
echo Test with custom scheme:
echo adb shell am start -W -a android.intent.action.VIEW -d "vtbhackaton://interview/789" com.preload.vtb_hackaton
echo.

echo Expected behavior:
echo - Loader should appear immediately when new deep link is received
echo - API call should be made with new interview ID
echo - Navigation should happen if API call succeeds
echo - Error message should appear if API call fails
echo.

echo To install the app:
echo adb install app\build\outputs\apk\debug\app-debug.apk
echo.

pause
