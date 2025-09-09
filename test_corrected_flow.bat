@echo off
echo ========================================
echo    ТЕСТИРОВАНИЕ ИСПРАВЛЕННОГО ФЛОУ
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
echo Ожидаемый флоу:
echo 1. MainActivity обрабатывает deep link
echo 2. FallbackFragment (стартовый экран)
echo 3. FallbackFragment -> ChooseModeFragment
echo 4. ChooseModeFragment загружает вопросы с сервера
echo 5. ChooseModeFragment -> InterviewFragment (кнопка "Начать")
echo 6. InterviewFragment начинает интервью
echo 7. InterviewFragment -> InterviewResultFragment (после завершения)
echo.
adb shell am start -W -a android.intent.action.VIEW -d "vtbhackaton://interview/123" com.preload.vtb_hackaton
echo.

echo 6. Проверяем логи...
echo Нажмите Ctrl+C для остановки
echo.
adb logcat | findstr /i "FallbackFragment\|ChooseModeFragment\|InterviewFragment\|Found interview ID\|Loading questions from server\|Successfully loaded\|Starting interview\|ERROR\|FATAL"
