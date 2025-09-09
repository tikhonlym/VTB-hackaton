#!/bin/bash

echo "========================================"
echo "    ТЕСТИРОВАНИЕ DEEP LINKS"
echo "========================================"
echo

echo "1. Проверяем подключенные устройства..."
adb devices
echo

echo "2. Устанавливаем debug версию приложения..."
adb install -r app/build/outputs/apk/debug/app-debug.apk
echo

echo "3. Тестируем custom scheme ссылки..."
echo
echo "Тест 1: vtbhackaton://interview/123"
adb shell am start -W -a android.intent.action.VIEW -d "vtbhackaton://interview/123" com.preload.vtb_hackaton
echo
echo "Тест 2: vtbhackaton://interview/456"
adb shell am start -W -a android.intent.action.VIEW -d "vtbhackaton://interview/456" com.preload.vtb_hackaton
echo
echo "Тест 3: vtbhackaton://interview/789"
adb shell am start -W -a android.intent.action.VIEW -d "vtbhackaton://interview/789" com.preload.vtb_hackaton
echo

echo "4. Запускаем мониторинг логов..."
echo "Нажмите Ctrl+C для остановки"
echo
adb logcat | grep -i "MainActivity\|Deep link\|vtbhackaton"
