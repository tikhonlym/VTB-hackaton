@echo off
echo ========================================
echo    ТЕСТИРОВАНИЕ SHAREDPREFERENCES
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

echo 5. Тестируем с подробными логами...
echo.
echo Тест: vtbhackaton://interview/123
echo Ожидаемые логи:
echo - InterviewFragment: Saving to SharedPreferences
echo - InterviewFragment: Verification - saved questions
echo - ChooseModeFragment: Loading from SharedPreferences
echo - ChooseModeFragment: Successfully loaded X questions
echo.
adb shell am start -W -a android.intent.action.VIEW -d "vtbhackaton://interview/123" com.preload.vtb_hackaton
echo.

echo 6. Проверяем логи...
echo Нажмите Ctrl+C для остановки
echo.
adb logcat | findstr /i "Saving to SharedPreferences\|Verification - saved\|Loading from SharedPreferences\|Successfully loaded\|Cannot start interview\|ERROR\|FATAL"
