@echo off
echo ========================================
echo    ТЕСТИРОВАНИЕ DEBUG DEEP LINKS
echo ========================================
echo.

echo 1. Проверяем установленные приложения...
adb shell pm list packages | findstr vtb_hackaton
echo.

echo 2. Устанавливаем приложение как обработчик для debug версии...
adb shell pm set-app-links --package com.preload.vtb_hackaton 1 vtbhackaton://
echo.

echo 3. Проверяем настройки App Links...
echo.
adb shell pm get-app-links com.preload.vtb_hackaton
echo.

echo 4. Тестируем custom scheme ссылки...
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

echo 5. Проверяем какие приложения могут обрабатывать vtbhackaton://...
echo.
adb shell pm query-activities -a android.intent.action.VIEW -d "vtbhackaton://test"
echo.

echo 6. Проверяем логи...
echo Нажмите Ctrl+C для остановки
echo.
adb logcat | findstr /i "MainActivity\|Deep link\|vtbhackaton\|PackageManager\|AppLinks"

