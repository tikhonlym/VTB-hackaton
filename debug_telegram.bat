@echo off
echo ========================================
echo    ОТЛАДКА TELEGRAM DEEP LINKS
echo ========================================
echo.

echo 1. Проверяем установленные приложения...
adb shell pm list packages | findstr vtb_hackaton
echo.

echo 2. Проверяем intent-filter для vtbhackaton://...
echo.
adb shell dumpsys package com.preload.vtb_hackaton | findstr -A 20 -B 5 "intent-filter"
echo.

echo 3. Проверяем настройки App Links...
echo.
adb shell pm get-app-links com.preload.vtb_hackaton
echo.

echo 4. Проверяем какие приложения могут обрабатывать vtbhackaton://...
echo.
adb shell pm query-activities -a android.intent.action.VIEW -d "vtbhackaton://test"
echo.

echo 5. Проверяем какие приложения могут обрабатывать https://vtbhackaton.app...
echo.
adb shell pm query-activities -a android.intent.action.VIEW -d "https://vtbhackaton.app"
echo.

echo 6. Тестируем запуск приложения напрямую...
echo.
adb shell am start -W -a android.intent.action.VIEW -d "vtbhackaton://interview/123" com.preload.vtb_hackaton
echo.

echo 7. Проверяем логи...
echo Нажмите Ctrl+C для остановки
echo.
adb logcat | findstr /i "MainActivity\|Deep link\|vtbhackaton\|PackageManager\|AppLinks"

