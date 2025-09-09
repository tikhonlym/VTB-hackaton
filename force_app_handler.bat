@echo off
echo ========================================
echo    ПРИНУДИТЕЛЬНАЯ НАСТРОЙКА ОБРАБОТЧИКА
echo ========================================
echo.

echo 1. Проверяем подключенные устройства...
adb devices
echo.

echo 2. Удаляем старые настройки App Links...
adb shell pm set-app-links --package com.preload.vtb_hackaton 0 vtbhackaton://
adb shell pm set-app-links --package com.preload.vtb_hackaton 0 https://vtbhackaton.app
echo.

echo 3. Устанавливаем приложение как обработчик по умолчанию...
adb shell pm set-app-links --package com.preload.vtb_hackaton 1 vtbhackaton://
adb shell pm set-app-links --package com.preload.vtb_hackaton 1 https://vtbhackaton.app
echo.

echo 4. Принудительно устанавливаем как обработчик для всех связанных доменов...
adb shell pm set-app-links --package com.preload.vtb_hackaton 1 http://vtbhackaton.app
echo.

echo 5. Проверяем настройки App Links...
echo.
adb shell pm get-app-links com.preload.vtb_hackaton
echo.

echo 6. Очищаем кэш системы...
adb shell pm clear com.android.packageinstaller
echo.

echo 7. Перезапускаем системные сервисы...
adb shell am force-stop com.android.packageinstaller
echo.

echo 8. Тестируем запуск приложения...
echo.
echo Тест 1: vtbhackaton://interview/123
adb shell am start -W -a android.intent.action.VIEW -d "vtbhackaton://interview/123" com.preload.vtb_hackaton
echo.

echo Тест 2: https://vtbhackaton.app?id=456
adb shell am start -W -a android.intent.action.VIEW -d "https://vtbhackaton.app?id=456" com.preload.vtb_hackaton
echo.

echo 9. Проверяем какие приложения могут обрабатывать ссылки...
echo.
adb shell pm query-activities -a android.intent.action.VIEW -d "vtbhackaton://test"
echo.

echo 10. Проверяем логи...
echo Нажмите Ctrl+C для остановки
echo.
adb logcat | findstr /i "MainActivity\|Deep link\|vtbhackaton\|PackageManager\|AppLinks"

