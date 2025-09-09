@echo off
echo ========================================
echo    ТЕСТИРОВАНИЕ ИСПРАВЛЕННОЙ НАВИГАЦИИ
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

echo 5. Тестируем исправленный флоу...
echo.
echo Тест: vtbhackaton://interview/123
echo Ожидаемое поведение:
echo 1. Открывается InterviewFragment
echo 2. Загружаются вопросы с сервера
echo 3. Переход на ChooseModeFragment
echo 4. ChooseModeFragment загружает вопросы из SharedPreferences
echo 5. Нажатие кнопки "Начать" -> возврат к InterviewFragment
echo 6. InterviewFragment начинает интервью
echo.
adb shell am start -W -a android.intent.action.VIEW -d "vtbhackaton://interview/123" com.preload.vtb_hackaton
echo.

echo 6. Проверяем логи...
echo Нажмите Ctrl+C для остановки
echo.
adb logcat | findstr /i "ChooseModeFragment\|InterviewFragment\|Loading from SharedPreferences\|Successfully loaded\|Cannot start interview\|ERROR\|FATAL"
