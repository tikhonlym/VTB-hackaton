@echo off
echo ========================================
echo    ТЕСТИРОВАНИЕ ФЛОУ ТОЛЬКО В СЕССИИ
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

echo 5. Тестируем флоу только в сессии...
echo.
echo Тест: vtbhackaton://interview/123
echo Ожидаемое поведение:
echo 1. FallbackFragment -> InterviewFragment -> ChooseModeFragment -> InterviewFragment -> InterviewResultFragment
echo 2. InterviewResultFragment показывается ТОЛЬКО ОДИН РАЗ
echo 3. После завершения интервью все данные сессии очищаются
echo 4. Кнопка "Назад" заблокирована на всех экранах
echo 5. Нет сохранения данных между сессиями
echo.
adb shell am start -W -a android.intent.action.VIEW -d "vtbhackaton://interview/123" com.preload.vtb_hackaton
echo.

echo 6. Проверяем логи...
echo Нажмите Ctrl+C для остановки
echo.
adb logcat | findstr /i "Session data cleared\|InterviewResultFragment displayed\|Back button pressed - blocked\|ERROR\|FATAL"
