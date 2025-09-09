@echo off
echo ========================================
echo    ПРИНУДИТЕЛЬНАЯ НАСТРОЙКА DEBUG APP
echo ========================================
echo.

echo 1. Проверяем подключенные устройства...
adb devices
echo.

echo 2. Проверяем установленные приложения...
adb shell pm list packages | findstr vtb_hackaton
echo.

echo 3. Очищаем старые настройки App Links...
adb shell pm set-app-links --package com.preload.vtb_hackaton 0 vtbhackaton://
echo.

echo 4. Устанавливаем приложение как обработчик по умолчанию...
adb shell pm set-app-links --package com.preload.vtb_hackaton 1 vtbhackaton://
echo.

echo 5. Проверяем настройки App Links...
echo.
adb shell pm get-app-links com.preload.vtb_hackaton
echo.

echo 6. Проверяем какие приложения могут обрабатывать vtbhackaton://...
echo.
adb shell pm query-activities -a android.intent.action.VIEW -d "vtbhackaton://test"
echo.

echo 7. Тестируем запуск приложения...
echo.
echo Тест 1: vtbhackaton://interview/123
adb shell am start -W -a android.intent.action.VIEW -d "vtbhackaton://interview/123" com.preload.vtb_hackaton
echo.

echo Тест 2: vtbhackaton://interview/456
adb shell am start -W -a android.intent.action.VIEW -d "vtbhackaton://interview/456" com.preload.vtb_hackaton
echo.

echo Тест 3: vtbhackaton://interview/789
adb shell am start -W -a android.intent.action.VIEW -d "vtbhackaton://interview/789" com.preload.vtb_hackaton
echo.

echo 8. Проверяем логи...
echo Нажмите Ctrl+C для остановки
echo.
adb logcat | findstr /i "MainActivity\|Deep link\|vtbhackaton\|PackageManager\|AppLinks"

