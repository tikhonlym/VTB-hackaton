@echo off
echo ========================================
echo    ТЕСТИРОВАНИЕ НАВИГАЦИИ ЧЕРЕЗ АРГУМЕНТЫ
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

echo 5. Тестируем навигацию через аргументы...
echo.
echo Тест: vtbhackaton://interview/123
echo Ожидаемое поведение:
echo 1. InterviewFragment загружает вопросы с сервера
echo 2. Переход к ChooseModeFragment с данными в аргументах
echo 3. ChooseModeFragment получает данные из аргументов
echo 4. Нажатие "Начать" -> передача данных обратно в InterviewFragment
echo 5. InterviewFragment начинает интервью
echo.
adb shell am start -W -a android.intent.action.VIEW -d "vtbhackaton://interview/123" com.preload.vtb_hackaton
echo.

echo 6. Проверяем логи...
echo Нажмите Ctrl+C для остановки
echo.
adb logcat | findstr /i "Preparing to navigate\|Questions data prepared\|Loading from arguments\|Successfully loaded\|Passing data to InterviewFragment\|Cannot start interview\|ERROR\|FATAL"
