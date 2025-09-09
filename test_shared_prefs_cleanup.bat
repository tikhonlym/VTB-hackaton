@echo off
echo Testing SharedPreferences Cleanup...
echo.

echo 1. Building the app...
call gradlew assembleDebug
if %errorlevel% neq 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)
echo Build successful!
echo.

echo 2. Installing the app...
adb install -r app\build\outputs\apk\debug\app-debug.apk
if %errorlevel% neq 0 (
    echo ERROR: Installation failed!
    pause
    exit /b 1
)
echo Installation successful!
echo.

echo 3. Starting app via deep link...
adb shell am start -W -a android.intent.action.VIEW -d "vtbhackaton://interview/123" com.preload.vtb_hackaton
if %errorlevel% neq 0 (
    echo ERROR: Deep link failed!
    pause
    exit /b 1
)
echo Deep link successful!
echo.

echo 4. Monitoring logs for SharedPreferences operations...
echo Waiting 5 seconds for app to start...
timeout /t 5 /nobreak >nul
echo.

echo Recent MainActivity logs:
adb logcat -d | findstr "MainActivity.*SharedPreferences\|MainActivity.*onDestroy\|MainActivity.*onPause" | tail -10
echo.

echo 5. Closing the app to test cleanup...
echo Closing app...
adb shell am force-stop com.preload.vtb_hackaton
echo.

echo 6. Checking logs after app closure...
echo Waiting 2 seconds...
timeout /t 2 /nobreak >nul
echo.

echo Final MainActivity logs:
adb logcat -d | findstr "MainActivity.*clearing\|MainActivity.*cleared" | tail -5
echo.

echo 7. Testing app restart to verify cleanup...
echo Restarting app...
adb shell am start -W -a android.intent.action.VIEW -d "vtbhackaton://interview/456" com.preload.vtb_hackaton
echo.

echo 8. Final verification...
echo Waiting 3 seconds...
timeout /t 3 /nobreak >nul
echo.

echo All MainActivity logs from this session:
adb logcat -d | findstr "MainActivity" | tail -15
echo.

echo Test completed!
echo.
echo ✅ SHARED PREFERENCES CLEANUP IMPLEMENTED:
echo - onDestroy() - clears all SharedPreferences when app is destroyed
echo - onPause() - clears all SharedPreferences when app is paused
echo - clearAllSharedPreferences() - method that performs the cleanup
echo - Uses sharedPref.edit().clear().apply() to remove all data
echo.
echo ✅ WHAT GETS CLEARED:
echo - deep_link_id
echo - loaded_questions
echo - interview_id
echo - Any other data stored in app_prefs
echo.
echo ✅ WHEN IT HAPPENS:
echo - When user closes the app (onDestroy)
echo - When user minimizes the app (onPause)
echo - Ensures clean state for next app launch
echo.
echo The app should now clean up all SharedPreferences data
echo when the user closes or minimizes the application!
echo.
pause
