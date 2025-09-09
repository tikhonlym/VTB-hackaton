@echo off
echo ========================================
echo    ТЕСТИРОВАНИЕ ГОЛОСОВОГО ВВОДА
echo ========================================
echo.

echo 1. Собираем debug версию...
.\gradlew.bat assembleDebug
echo.

echo 2. Устанавливаем обновленную версию...
adb install -r app\build\outputs\apk\debug\app-debug.apk
echo.

echo 3. Очищаем данные для чистого теста...
adb shell pm clear com.preload.vtb_hackaton
echo.

echo 4. Запускаем приложение с deep link...
echo Тест: vtbhackaton://interview/123
adb shell am start -W -a android.intent.action.VIEW -d "vtbhackaton://interview/123" com.preload.vtb_hackaton
echo.

echo 5. Проверяем логи голосового ввода...
echo Нажмите Ctrl+C для остановки
echo.
echo Ожидаемые логи:
echo - "FallbackFragment: All permissions granted, proceeding with app flow"
echo - "InterviewFragment: Starting interview with X questions"
echo - "InterviewFragment: Starting voice input for question X"
echo - "AndroidSpeechRecognizer: Ready for speech input"
echo - "AndroidSpeechRecognizer: Voice input recognized: '...'"
echo - "InterviewFragment: Voice answer for question X: '...'"
echo - "okhttp.OkHttpClient: {"answers":[{"answer":"...","id":X}]}"
echo.
echo ПРИМЕЧАНИЕ: Нажмите кнопку "await" для голосового ввода!
echo.
adb logcat | findstr /i "Voice input\|AndroidSpeechRecognizer\|Voice answer\|okhttp.OkHttpClient"
