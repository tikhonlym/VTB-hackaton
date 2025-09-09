@echo off
echo Testing fixed navigation crash issue
echo.

echo Building the app...
call gradlew assembleDebug
if %errorlevel% neq 0 (
    echo Build failed!
    pause
    exit /b 1
)

echo.
echo Build successful! Now testing the navigation fix:
echo.

echo Test scenario that was crashing:
echo 1. Launch app WITHOUT deep link (normal launch)
echo 2. App should show FallbackFragment with loader
echo 3. Wait for initial processing to complete
echo 4. Send a deep link while app is open
echo 5. App should show loader again and navigate safely
echo.

echo Commands to test:
echo.
echo Step 1 - Normal launch (no deep link):
echo adb shell am start -n com.preload.vtb_hackaton/.MainActivity
echo.
echo Step 2 - Send deep link while app is open:
echo adb shell am start -W -a android.intent.action.VIEW -d "https://vtbhackaton.app/interview/123" com.preload.vtb_hackaton
echo.

echo Expected behavior (FIXED):
echo - No more navigation crashes
echo - Loader shows when deep link is received
echo - Safe navigation to ChooseModeFragment
echo - Proper error handling if navigation fails
echo.

echo To install the app:
echo adb install app\build\outputs\apk\debug\app-debug.apk
echo.

echo The fix includes:
echo - Check current destination before navigation
echo - Use global navigation actions as fallback
echo - Proper error handling with try-catch
echo - Detailed logging for debugging
echo.

pause
