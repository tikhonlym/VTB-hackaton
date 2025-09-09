@echo off
echo ========================================
echo    ТЕСТИРОВАНИЕ DEEP LINKS (ИСПРАВЛЕННАЯ ВЕРСИЯ)
echo ========================================
echo.

echo 1. Проверяем подключенные устройства...
adb devices
echo.

echo 2. Устанавливаем debug версию приложения...
adb install -r app\build\outputs\apk\debug\app-debug.apk
echo.

echo 3. Запускаем приложение...
adb shell am start -n com.preload.vtb_hackaton/.MainActivity
echo.
echo Приложение запущено. Теперь тестируем диплинки когда приложение уже работает...
echo.

echo 4. Тестируем custom scheme ссылки (приложение уже запущено)...
echo.
echo Тест 1: vtbhackaton://interview/123
adb shell am start -W -a android.intent.action.VIEW -d "vtbhackaton://interview/123" com.preload.vtb_hackaton
echo.
echo Ждем 3 секунды...
timeout /t 3 /nobreak >nul
echo.
echo Тест 2: vtbhackaton://interview/abc123
adb shell am start -W -a android.intent.action.VIEW -d "vtbhackaton://interview/abc123" com.preload.vtb_hackaton
echo.
echo Ждем 3 секунды...
timeout /t 3 /nobreak >nul
echo.
echo Тест 3: vtbhackaton://interview/test-interview-456
adb shell am start -W -a android.intent.action.VIEW -d "vtbhackaton://interview/test-interview-456" com.preload.vtb_hackaton
echo.
echo Ждем 3 секунды...
timeout /t 3 /nobreak >nul
echo.

echo 5. Тестируем HTTPS ссылки...
echo.
echo Тест 4: https://vtbhackaton.app/interview/789
adb shell am start -W -a android.intent.action.VIEW -d "https://vtbhackaton.app/interview/789" com.preload.vtb_hackaton
echo.
echo Ждем 3 секунды...
timeout /t 3 /nobreak >nul
echo.
echo Тест 5: https://vtbhackaton.app?id=999
adb shell am start -W -a android.intent.action.VIEW -d "https://vtbhackaton.app?id=999" com.preload.vtb_hackaton
echo.

echo 6. Проверяем количество инстансов приложения...
echo.
echo Количество активных инстансов MainActivity:
adb shell dumpsys activity activities | findstr /i "MainActivity" | findstr /i "com.preload.vtb_hackaton"
echo.

echo 7. Запускаем мониторинг логов...
echo Нажмите Ctrl+C для остановки
echo.
adb logcat | findstr /i "MainActivity\|Deep link\|vtbhackaton\|onNewIntent"
