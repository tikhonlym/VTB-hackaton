@echo off
echo ========================================
echo    ТЕСТИРОВАНИЕ ИСПРАВЛЕННОГО ПРИЛОЖЕНИЯ
echo ========================================
echo.

echo 1. Собираем debug версию...
.\gradlew.bat assembleDebug
echo.

echo 2. Устанавливаем исправленную версию...
adb install -r app\build\outputs\apk\debug\app-debug.apk
echo.

echo 3. Настраиваем приложение как обработчик...
adb shell pm set-app-links --package com.preload.vtb_hackaton 1 vtbhackaton://
echo.

echo 4. Тестируем deep links...
echo.
echo Тест 1: vtbhackaton://interview/123
adb shell am start -W -a android.intent.action.VIEW -d "vtbhackaton://interview/123" com.preload.vtb_hackaton
echo.

echo Тест 2: vtbhackaton://interview/456
adb shell am start -W -a android.intent.action.VIEW -d "vtbhackaton://interview/456" com.preload.vtb_hackaton
echo.

echo 5. Проверяем логи на ошибки...
echo Нажмите Ctrl+C для остановки
echo.
adb logcat | findstr /i "MainActivity\|Deep link\|vtbhackaton\|QuestionTimer\|InterviewFragment\|ERROR\|FATAL"
