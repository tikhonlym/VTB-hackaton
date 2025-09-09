@echo off
echo Testing Loading Dialog Implementation...
echo.

echo 1. Building the app...
call gradlew assembleDebug
if %errorlevel% neq 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)
echo Build successful!
echo.

echo 2. Installing the app...
adb install -r app\build\outputs\apk\debug\app-debug.apk
if %errorlevel% neq 0 (
    echo ERROR: Installation failed!
    pause
    exit /b 1
)
echo Installation successful!
echo.

echo 3. Starting interview via deep link...
adb shell am start -W -a android.intent.action.VIEW -d "vtbhackaton://interview/123" com.preload.vtb_hackaton
if %errorlevel% neq 0 (
    echo ERROR: Deep link failed!
    pause
    exit /b 1
)
echo Deep link successful!
echo.

echo 4. Monitoring logs for loading dialog...
echo Waiting 10 seconds for app to start...
timeout /t 10 /nobreak >nul
echo.

echo Recent logs:
adb logcat -d | findstr "InterviewFragment\|LoadingDialog" | tail -15
echo.

echo 5. Loading Dialog Implementation Summary:
echo.
echo ✅ CREATED:
echo - LoadingDialog.kt - класс для управления лоадером
echo - loading_dialog.xml - layout с круглым лоадером
echo - circular_progress.xml - анимированный круглый прогресс
echo - loading_animation.xml - анимация вращения
echo - rounded_background_white.xml - белый фон с закругленными углами
echo.
echo ✅ FEATURES:
echo - Красивый круглый лоадер с анимацией
echo - Полноэкранный overlay с полупрозрачным фоном
echo - Настраиваемые сообщения и подзаголовки
echo - Автоматическое скрытие после завершения операции
echo - Блокировка взаимодействия с UI во время загрузки
echo.
echo ✅ INTEGRATED IN:
echo - loadQuestions() - показывается при загрузке вопросов
echo - submitAnswers() - показывается при отправке ответов
echo - onDestroy() - автоматическое скрытие при закрытии
echo.
echo ✅ LOADING STATES:
echo 1. "Загрузка вопросов" - "Подготовка интервью..."
echo 2. "Отправка ответов" - "Сохранение результатов интервью..."
echo.
echo Test completed!
echo.
echo The loading dialog should appear:
echo - When starting interview (loading questions)
echo - When submitting answers (sending to server)
echo - With beautiful circular animation
echo - With customizable messages
echo.
pause
