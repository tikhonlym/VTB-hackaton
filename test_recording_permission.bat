@echo off
echo ========================================
echo    ТЕСТИРОВАНИЕ РАЗРЕШЕНИЯ НА ЗАПИСЬ
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
echo - "FallbackFragment: Android version: XX (API XX)"
echo - "FallbackFragment: Required permissions: [RECORD_AUDIO]"
echo - "FallbackFragment: Permission RECORD_AUDIO: DENIED"
echo - "FallbackFragment: Requesting permissions: [RECORD_AUDIO]"
echo - "FallbackFragment: ActivityCompat.requestPermissions called successfully"
echo - "FallbackFragment: 1 second after requestPermissions - checking if dialog appeared"
echo - "FallbackFragment: onRequestPermissionsResult called with requestCode: 1001"
echo.
adb logcat | findstr /i "FallbackFragment\|ActivityCompat\|onRequestPermissionsResult\|Android version\|RECORD_AUDIO"
