@echo off
echo ========================================
echo    ТЕСТИРОВАНИЕ НОВОГО ФЛОУ
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

echo 5. Тестируем новый флоу...
echo.
echo Тест: vtbhackaton://interview/123
echo Ожидаемый флоу:
echo 1. MainActivity обрабатывает deep link
echo 2. FallbackFragment (стартовый экран)
echo 3. FallbackFragment проверяет наличие ID
echo 4. Переход к InterviewFragment для загрузки вопросов
echo 5. InterviewFragment загружает вопросы с сервера
echo 6. Переход к ChooseModeFragment с данными
echo 7. ChooseModeFragment показывает кнопку "Начать"
echo 8. Нажатие "Начать" -> InterviewFragment (интервью)
echo 9. После завершения -> InterviewResultFragment
echo.
adb shell am start -W -a android.intent.action.VIEW -d "vtbhackaton://interview/123" com.preload.vtb_hackaton
echo.

echo 6. Проверяем логи...
echo Нажмите Ctrl+C для остановки
echo.
adb logcat | findstr /i "MainActivity\|FallbackFragment\|InterviewFragment\|ChooseModeFragment\|Checking for questions\|Found interview ID\|Loading from arguments\|Successfully loaded\|ERROR\|FATAL"
