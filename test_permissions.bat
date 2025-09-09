@echo off
echo ========================================
echo    ТЕСТИРОВАНИЕ ПРОВЕРКИ РАЗРЕШЕНИЙ
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

echo 5. Тестируем проверку разрешений...
echo.
echo Тест 1: Запуск без разрешений
echo Ожидаемое поведение:
echo 1. FallbackFragment проверяет разрешения
echo 2. Запрашивает разрешения у пользователя
echo 3. Если разрешения не даны - показывает Toast и закрывает приложение
echo 4. Если разрешения даны - продолжает работу
echo.
adb shell am start -W -a android.intent.action.VIEW -d "vtbhackaton://interview/123" com.preload.vtb_hackaton
echo.

echo 6. Проверяем логи...
echo Нажмите Ctrl+C для остановки
echo.
adb logcat | findstr /i "FallbackFragment\|Checking required permissions\|Missing permissions\|All permissions granted\|Closing app due to missing permissions\|ERROR\|FATAL"
