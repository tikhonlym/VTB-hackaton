@echo off
echo ========================================
echo    ТЕСТИРОВАНИЕ БЕЗ КНОПКИ "НАЗАД"
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

echo 4. Тестируем навигацию без кнопки "Назад"...
echo.
echo Тест: vtbhackaton://interview/123
echo Ожидаемое поведение:
echo 1. Открывается InterviewFragment
echo 2. Загружаются вопросы с сервера
echo 3. Переход на ChooseModeFragment
echo 4. Кнопка "Назад" НЕ РАБОТАЕТ
echo 5. Нажатие кнопки "Начать" -> возврат к InterviewFragment
echo 6. Кнопка "Назад" НЕ РАБОТАЕТ
echo 7. После завершения -> InterviewResultFragment
echo 8. Кнопка "Назад" НЕ РАБОТАЕТ
echo.
adb shell am start -W -a android.intent.action.VIEW -d "vtbhackaton://interview/123" com.preload.vtb_hackaton
echo.

echo 5. Проверяем логи...
echo Нажмите Ctrl+C для остановки
echo.
adb logcat | findstr /i "Back button pressed - blocked\|MainActivity\|Deep link\|vtbhackaton\|InterviewFragment\|ChooseModeFragment\|InterviewResultFragment\|ERROR\|FATAL"
