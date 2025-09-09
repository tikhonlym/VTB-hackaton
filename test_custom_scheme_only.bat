@echo off
echo ========================================
echo    ТЕСТИРОВАНИЕ ТОЛЬКО CUSTOM SCHEME
echo ========================================
echo.

echo 1. Собираем release версию...
.\gradlew.bat assembleRelease
echo.

echo 2. Устанавливаем release версию...
adb install -r app\build\outputs\apk\release\app-release.apk
echo.

echo 3. Устанавливаем приложение как обработчик для custom scheme...
adb shell pm set-app-links --package com.preload.vtb_hackaton 1 vtbhackaton://
echo.

echo 4. Проверяем настройки App Links...
echo.
adb shell pm get-app-links com.preload.vtb_hackaton
echo.

echo 5. Тестируем ТОЛЬКО custom scheme ссылки...
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

echo 6. Проверяем какие приложения могут обрабатывать vtbhackaton://...
echo.
adb shell pm query-activities -a android.intent.action.VIEW -d "vtbhackaton://test"
echo.

echo 7. Проверяем логи...
echo Нажмите Ctrl+C для остановки
echo.
adb logcat | findstr /i "MainActivity\|Deep link\|vtbhackaton\|PackageManager\|AppLinks"

