@echo off
echo ========================================
echo    ТЕСТИРОВАНИЕ СИСТЕМНОЙ ПЛАШКИ РАЗРЕШЕНИЙ
echo ========================================
echo.

echo 1. Собираем debug версию...
.\gradlew.bat assembleDebug
echo.

echo 2. Устанавливаем обновленную версию...
adb install -r app\build\outputs\apk\debug\app-debug.apk
echo.

echo 3. Очищаем данные для чистого теста...
adb shell pm clear com.preload.vtb_hackaton
echo.

echo 4. Запускаем приложение с deep link...
echo Тест: vtbhackaton://interview/123
adb shell am start -W -a android.intent.action.VIEW -d "vtbhackaton://interview/123" com.preload.vtb_hackaton
echo.

echo 5. Проверяем логи разрешений...
echo Нажмите Ctrl+C для остановки
echo.
echo Ожидаемые логи:
echo - "Checking required permissions..."
echo - "Missing permissions: [...]"
echo - "Requesting permissions: [...]" (системная плашка)
echo - "Some permissions were denied"
echo - "First permission denial, requesting again with system dialog" (повторная системная плашка)
echo - "Permissions denied again, closing app" (если снова отказался)
echo.
adb logcat | findstr /i "FallbackFragment\|permissions\|denied\|granted\|system dialog"
