@echo off
echo ========================================
echo    ТЕСТИРОВАНИЕ СТРОКОВЫХ ID
echo ========================================
echo.

echo 1. Собираем debug версию...
.\gradlew.bat assembleDebug
echo.

echo 2. Устанавливаем обновленную версию...
adb install -r app\build\outputs\apk\debug\app-debug.apk
echo.

echo 3. Настраиваем приложение как обработчик...
adb shell pm set-app-links --package com.preload.vtb_hackaton 1 vtbhackaton://
echo.

echo 4. Очищаем SharedPreferences для чистого теста...
adb shell pm clear com.preload.vtb_hackaton
echo.

echo 5. Тестируем строковые ID...
echo.
echo Тест 1: vtbhackaton://interview/123 (числовой ID)
adb shell am start -W -a android.intent.action.VIEW -d "vtbhackaton://interview/123" com.preload.vtb_hackaton
echo.
echo Тест 2: vtbhackaton://interview/abc123 (буквенно-цифровой ID)
adb shell am start -W -a android.intent.action.VIEW -d "vtbhackaton://interview/abc123" com.preload.vtb_hackaton
echo.
echo Тест 3: vtbhackaton://interview/test-interview-456 (строковый ID с дефисами)
adb shell am start -W -a android.intent.action.VIEW -d "vtbhackaton://interview/test-interview-456" com.preload.vtb_hackaton
echo.

echo 6. Проверяем логи...
echo Нажмите Ctrl+C для остановки
echo.
adb logcat | findstr /i "FallbackFragment\|Making API call with ID\|Successfully loaded\|ERROR\|FATAL"
