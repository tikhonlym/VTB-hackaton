@echo off
echo ========================================
echo    ТЕСТИРОВАНИЕ ПРОВЕРКИ РАЗРЕШЕНИЙ ПЕРВОЙ
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

echo 5. Тестируем правильную последовательность...
echo.
echo Тест: vtbhackaton://interview/123
echo Ожидаемая последовательность:
echo 1. FallbackFragment запускается
echo 2. СНАЧАЛА проверяет разрешения
echo 3. ТОЛЬКО ПОТОМ загружает вопросы с сервера
echo 4. Если разрешений нет - закрывает приложение
echo 5. Если разрешения есть - продолжает работу
echo.
adb shell am start -W -a android.intent.action.VIEW -d "vtbhackaton://interview/123" com.preload.vtb_hackaton
echo.

echo 6. Проверяем логи...
echo Нажмите Ctrl+C для остановки
echo.
adb logcat | findstr /i "FallbackFragment\|Checking required permissions\|All permissions granted\|Starting to load questions\|Making API call.*permissions already granted\|ERROR\|FATAL"
