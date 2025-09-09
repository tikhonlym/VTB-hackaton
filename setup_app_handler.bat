@echo off
echo ========================================
echo    НАСТРОЙКА ПРИЛОЖЕНИЯ КАК ОБРАБОТЧИКА
echo ========================================
echo.

echo 1. Проверяем подключенные устройства...
adb devices
echo.

echo 2. Устанавливаем приложение как обработчик для vtbhackaton://...
adb shell pm set-app-links --package com.preload.vtb_hackaton 1 vtbhackaton://
echo.

echo 3. Устанавливаем приложение как обработчик для https://vtbhackaton.app...
adb shell pm set-app-links --package com.preload.vtb_hackaton 1 https://vtbhackaton.app
echo.

echo 4. Проверяем настройки App Links...
echo.
adb shell pm get-app-links com.preload.vtb_hackaton
echo.

echo 5. Тестируем запуск приложения...
echo.
echo Тест 1: vtbhackaton://interview/123
adb shell am start -W -a android.intent.action.VIEW -d "vtbhackaton://interview/123" com.preload.vtb_hackaton
echo.

echo Тест 2: https://vtbhackaton.app?id=456
adb shell am start -W -a android.intent.action.VIEW -d "https://vtbhackaton.app?id=456" com.preload.vtb_hackaton
echo.

echo 6. Проверяем логи...
echo Нажмите Ctrl+C для остановки
echo.
adb logcat | findstr /i "MainActivity\|Deep link\|vtbhackaton\|PackageManager\|AppLinks"

