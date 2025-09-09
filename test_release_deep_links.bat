@echo off
echo ========================================
echo    ТЕСТИРОВАНИЕ RELEASE DEEP LINKS
echo ========================================
echo.

echo 1. Собираем release версию...
.\gradlew.bat assembleRelease
echo.

echo 2. Устанавливаем release версию...
adb install -r app\build\outputs\apk\release\app-release.apk
echo.

echo 3. Проверяем настройки App Links для release...
echo.
adb shell pm get-app-links com.preload.vtb_hackaton
echo.

echo 4. Устанавливаем приложение как обработчик по умолчанию...
adb shell pm set-app-links --package com.preload.vtb_hackaton 1 vtbhackaton://
adb shell pm set-app-links --package com.preload.vtb_hackaton 1 https://vtbhackaton.app
echo.

echo 5. Тестируем deep links в release сборке...
echo.
echo Тест 1: vtbhackaton://interview/123
adb shell am start -W -a android.intent.action.VIEW -d "vtbhackaton://interview/123" com.preload.vtb_hackaton
echo.

echo Тест 2: https://vtbhackaton.app?id=456
adb shell am start -W -a android.intent.action.VIEW -d "https://vtbhackaton.app?id=456" com.preload.vtb_hackaton
echo.

echo Тест 3: https://vtbhackaton.app/interview/789
adb shell am start -W -a android.intent.action.VIEW -d "https://vtbhackaton.app/interview/789" com.preload.vtb_hackaton
echo.

echo 6. Проверяем логи...
echo Нажмите Ctrl+C для остановки
echo.
adb logcat | findstr /i "MainActivity\|Deep link\|vtbhackaton\|PackageManager\|AppLinks"

