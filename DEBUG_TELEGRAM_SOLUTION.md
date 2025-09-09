# 🚀 РЕШЕНИЕ ДЛЯ DEBUG ВЕРСИИ

## 🎯 Проблема
У вас установлена debug версия (`1.0-debug`), но ссылки из Telegram не работают.

## ✅ РЕШЕНИЯ:

### **1. 🔧 Настройка приложения как обработчика**

```bash
# Запустите этот скрипт
force_debug_app_handler.bat
```

### **2. 📱 Тестирование в Telegram**

#### **Вариант A: Custom Scheme**
```
vtbhackaton://interview/123
```

#### **Вариант B: Intent URL**
```
intent://interview/123#Intent;scheme=vtbhackaton;package=com.preload.vtb_hackaton;end
```

### **3. 🌐 Веб-страница с редиректом**

1. Откройте `debug_telegram_links.html` в браузере
2. Скопируйте URL страницы
3. Отправьте в Telegram
4. Нажмите на ссылку

## 🧪 ПОШАГОВОЕ ТЕСТИРОВАНИЕ:

### **Шаг 1: Настройка**
```bash
force_debug_app_handler.bat
```

### **Шаг 2: Перезапуск Telegram**
- Закройте Telegram полностью
- Откройте заново

### **Шаг 3: Тестирование**

#### **В Telegram отправьте:**
```
vtbhackaton://interview/123
```

#### **Если не работает, попробуйте Intent URL:**
```
intent://interview/123#Intent;scheme=vtbhackaton;package=com.preload.vtb_hackaton;end
```

## 🔍 ОТЛАДКА:

### **Проверьте настройки:**
```bash
adb shell pm get-app-links com.preload.vtb_hackaton
```

### **Проверьте обработчики:**
```bash
adb shell pm query-activities -a android.intent.action.VIEW -d "vtbhackaton://test"
```

### **Проверьте логи:**
```bash
adb logcat | grep -i "MainActivity\|Deep link\|vtbhackaton"
```

## 🎯 АЛЬТЕРНАТИВНЫЕ РЕШЕНИЯ:

### **1. Используйте другие мессенджеры:**
- **WhatsApp** - обычно лучше работает с deep links
- **Viber** - поддерживает custom schemes
- **SMS** - отправьте ссылку в сообщении

### **2. Создайте QR-код:**
1. Создайте QR-код со ссылкой `vtbhackaton://interview/123`
2. Отсканируйте камерой Android
3. Выберите "Открыть в приложении"

### **3. Используйте браузер:**
1. Откройте ссылку в Chrome
2. Chrome должен предложить открыть в приложении

## ⚠️ ВАЖНЫЕ МОМЕНТЫ:

1. **Перезапустите Telegram** после настройки приложения
2. **Убедитесь, что приложение установлено** перед тестированием
3. **Проверьте версию Telegram** - обновите до последней
4. **Попробуйте все варианты** - один из них должен сработать

## 🎉 РЕКОМЕНДУЕМЫЙ ПОРЯДОК:

1. **Запустите `force_debug_app_handler.bat`**
2. **Перезапустите Telegram**
3. **Попробуйте Custom Scheme: `vtbhackaton://interview/123`**
4. **Если не работает - попробуйте Intent URL**
5. **Если не работает - используйте веб-страницу**

## 🚀 ОЖИДАЕМЫЙ РЕЗУЛЬТАТ:

После выполнения всех шагов:
- Telegram должен предложить выбрать приложение
- Или сразу открыть ваше приложение
- Появится Toast с ID: "Deep Link ID: 123"
- ID сохранится в SharedPreferences

**Один из этих методов должен решить проблему с Telegram!** 🎯

