@echo off
echo ========================================
echo    ТЕСТИРОВАНИЕ ВСЕХ ТИПОВ DEEP LINKS
echo ========================================
echo.

echo 1. Проверяем подключенные устройства...
adb devices
echo.

echo 2. Проверяем установленные приложения...
adb shell pm list packages | findstr vtb_hackaton
echo.

echo 3. Настраиваем приложение как обработчик...
adb shell pm set-app-links --package com.preload.vtb_hackaton 1 vtbhackaton://
echo.

echo 4. Проверяем настройки App Links...
echo.
adb shell pm get-app-links com.preload.vtb_hackaton
echo.

echo 5. Тестируем Custom Scheme ссылки...
echo.
echo Тест 1: vtbhackaton://interview/123
adb shell am start -W -a android.intent.action.VIEW -d "vtbhackaton://interview/123" com.preload.vtb_hackaton
echo.

echo Тест 2: vtbhackaton://interview/456
adb shell am start -W -a android.intent.action.VIEW -d "vtbhackaton://interview/456" com.preload.vtb_hackaton
echo.

echo Тест 3: vtbhackaton://profile/user789
adb shell am start -W -a android.intent.action.VIEW -d "vtbhackaton://profile/user789" com.preload.vtb_hackaton
echo.

echo 6. Тестируем HTTPS ссылки (если домен настроен)...
echo.
echo Тест 4: https://vtbhackaton.app?id=123
adb shell am start -W -a android.intent.action.VIEW -d "https://vtbhackaton.app?id=123" com.preload.vtb_hackaton
echo.

echo Тест 5: https://vtbhackaton.app/interview/456
adb shell am start -W -a android.intent.action.VIEW -d "https://vtbhackaton.app/interview/456" com.preload.vtb_hackaton
echo.

echo 7. Проверяем какие приложения могут обрабатывать ссылки...
echo.
echo Проверка vtbhackaton://
adb shell pm query-activities -a android.intent.action.VIEW -d "vtbhackaton://test"
echo.

echo Проверка https://vtbhackaton.app
adb shell pm query-activities -a android.intent.action.VIEW -d "https://vtbhackaton.app"
echo.

echo 8. Проверяем логи...
echo Нажмите Ctrl+C для остановки
echo.
adb logcat | findstr /i "MainActivity\|Deep link\|vtbhackaton\|PackageManager\|AppLinks"

