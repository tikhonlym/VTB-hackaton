@echo off
echo ========================================
echo    ПРОВЕРКА НАСТРОЕК ПРИЛОЖЕНИЯ
echo ========================================
echo.

echo 1. Проверяем установленные приложения...
echo Поиск приложений с vtb_hackaton...
adb shell pm list packages | findstr vtb_hackaton
echo.

echo 2. Проверяем intent-filter для vtbhackaton://...
echo.
adb shell dumpsys package com.preload.vtb_hackaton | findstr -A 10 -B 5 "vtbhackaton"
echo.

echo 3. Проверяем настройки App Links...
echo.
adb shell pm get-app-links com.preload.vtb_hackaton
echo.

echo 4. Проверяем разрешения для открытия ссылок...
echo.
adb shell pm query-activities -a android.intent.action.VIEW -d "vtbhackaton://test"
echo.

echo 5. Тестируем запуск приложения...
echo.
echo Запускаем приложение через deep link...
adb shell am start -W -a android.intent.action.VIEW -d "vtbhackaton://interview/123" com.preload.vtb_hackaton
echo.

echo 6. Проверяем логи...
echo Нажмите Ctrl+C для остановки
echo.
adb logcat | findstr /i "MainActivity\|Deep link\|vtbhackaton\|PackageManager"
