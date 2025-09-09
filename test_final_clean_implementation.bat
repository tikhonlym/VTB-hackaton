@echo off
echo Testing Final Clean Implementation...
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

echo 3. Starting interview via deep link...
adb shell am start -W -a android.intent.action.VIEW -d "vtbhackaton://interview/123" com.preload.vtb_hackaton
if %errorlevel% neq 0 (
    echo ERROR: Deep link failed!
    pause
    exit /b 1
)
echo Deep link successful!
echo.

echo 4. Monitoring logs...
echo Waiting 10 seconds for app to start...
timeout /t 10 /nobreak >nul
echo.

echo Recent logs:
adb logcat -d | findstr "InterviewFragment\|VoiceInputActivity" | tail -15
echo.

echo 5. Final implementation summary:
echo.
echo ✅ CLEANED UP:
echo - Removed old AndroidSpeechRecognizer.kt
echo - Removed unused imports (Manifest, PackageManager, etc.)
echo - Removed permission handling code
echo - Removed old test files
echo - Simplified InterviewFragment
echo.
echo ✅ FINAL IMPLEMENTATION:
echo - Simple Google Voice Typing (GeeksforGeeks style)
echo - VoiceInputActivity with system RecognizerIntent
echo - Clean, minimal code
echo - No unnecessary complexity
echo.
echo Test completed!
pause
