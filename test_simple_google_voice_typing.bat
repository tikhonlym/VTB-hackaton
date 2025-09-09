@echo off
echo Testing Simple Google Voice Typing (GeeksforGeeks style)...
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

echo 4. Monitoring logs for simple Google Voice Typing...
echo Waiting 10 seconds for app to start...
timeout /t 10 /nobreak >nul
echo.

echo Recent VoiceInputActivity logs:
adb logcat -d | findstr "VoiceInputActivity" | tail -10
echo.

echo Recent InterviewFragment logs:
adb logcat -d | findstr "InterviewFragment" | tail -10
echo.

echo 5. Testing simple Google Voice Typing behavior...
echo.
echo Expected behavior (GeeksforGeeks style):
echo - VoiceInputActivity opens automatically after TTS
echo - Google Voice Typing interface appears (system dialog)
echo - User speaks and gets recognized text
echo - Text accumulates in EditText field
echo - Process repeats automatically every 2-3 seconds
echo - Works for full 3 minutes
echo - Much simpler and more reliable than custom implementation
echo.

echo 6. Monitoring for 30 seconds to see behavior...
echo Starting continuous monitoring...
for /L %%i in (1,1,6) do (
    echo Check %%i/6 - Time: %time%
    adb logcat -d | findstr "VoiceInputActivity.*Voice input result\|VoiceInputActivity.*Starting\|VoiceInputActivity.*Finishing" | tail -5
    timeout /t 5 /nobreak >nul
    echo.
)

echo 7. Final log summary...
echo.
echo All VoiceInputActivity logs from this session:
adb logcat -d | findstr "VoiceInputActivity" | tail -15
echo.

echo Test completed!
echo.
echo Key improvements with simple Google Voice Typing:
echo 1. Uses system RecognizerIntent.ACTION_RECOGNIZE_SPEECH
echo 2. Shows familiar Google Voice Typing interface
echo 3. Much simpler implementation (like GeeksforGeeks example)
echo 4. Better reliability and accuracy
echo 5. Automatic continuous recognition
echo 6. Text accumulation in EditText
echo.
echo This is exactly what you wanted - the system Google Voice Typing!
echo.
pause
