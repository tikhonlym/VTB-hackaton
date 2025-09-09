# 🔧 Исправление проблемы с Telegram

## 🎯 Проблема
Telegram не подхватывает ваше приложение и открывает веб-интерфейс вместо мобильного приложения.

## ✅ Что исправлено:

### 1. **Добавлены дополнительные intent-filter**
- Добавлен `android:host="*"` для лучшей совместимости
- Улучшены настройки Activity

### 2. **Проверка настроек приложения**
Запустите: `check_app_settings.bat`

## 🚀 Решения для Telegram:

### **Решение 1: Принудительная установка как обработчик**

1. **Откройте настройки Android:**
   - Настройки → Приложения → VTB Hackaton Debug
   - "Открывать по умолчанию" → "Открывать поддерживаемые ссылки"
   - Включите переключатель

2. **Или через ADB:**
   ```bash
   adb shell pm set-app-links --package com.preload.vtb_hackaton.debug 1 vtbhackaton://
   ```

### **Решение 2: Использование HTTPS ссылок**

Вместо `vtbhackaton://interview/123` используйте:
```
https://vtbhackaton.app/interview/123
```

Но для этого нужно:
1. Зарегистрировать домен `vtbhackaton.app`
2. Разместить файл `/.well-known/assetlinks.json`

### **Решение 3: Альтернативные форматы ссылок**

Попробуйте эти варианты в Telegram:

```
# Вариант 1: С явным указанием приложения
intent://interview/123#Intent;scheme=vtbhackaton;package=com.preload.vtb_hackaton.debug;end

# Вариант 2: Через market://
market://details?id=com.preload.vtb_hackaton.debug

# Вариант 3: Через HTTP с редиректом
http://vtbhackaton.app/interview/123
```

## 🧪 Тестирование:

### **1. Проверьте настройки приложения:**
```bash
check_app_settings.bat
```

### **2. Тест через другие приложения:**
- **WhatsApp:** Отправьте ссылку в чат
- **SMS:** Отправьте ссылку в сообщении
- **Email:** Отправьте ссылку в письме
- **Браузер:** Откройте ссылку в Chrome

### **3. Тест через QR-код:**
1. Создайте QR-код со ссылкой `vtbhackaton://interview/123`
2. Отсканируйте камерой
3. Выберите "Открыть в приложении"

## 🔍 Отладка:

### **Проверьте логи:**
```bash
adb logcat | grep -i "PackageManager\|AppLinks\|vtbhackaton"
```

### **Проверьте настройки системы:**
```bash
# Проверка App Links
adb shell pm get-app-links com.preload.vtb_hackaton.debug

# Проверка intent-filter
adb shell dumpsys package com.preload.vtb_hackaton.debug | grep -A 20 "intent-filter"
```

## 🎯 Альтернативные решения:

### **1. Используйте Intent URL (работает в Telegram):**
```
intent://interview/123#Intent;scheme=vtbhackaton;package=com.preload.vtb_hackaton.debug;end
```

### **2. Создайте веб-страницу с редиректом:**
```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Открыть в приложении</title>
</head>
<body>
    <script>
        // Пытаемся открыть в приложении
        window.location.href = "vtbhackaton://interview/123";
        
        // Если не получилось, показываем кнопку
        setTimeout(function() {
            document.body.innerHTML = '<h1>Откройте в приложении</h1><a href="vtbhackaton://interview/123">Нажмите здесь</a>';
        }, 1000);
    </script>
</body>
</html>
```

### **3. Используйте Universal Links (iOS) / App Links (Android):**
Настройте домен с файлом `assetlinks.json`

## ⚠️ Важные моменты:

1. **Telegram может кэшировать настройки** - перезапустите Telegram
2. **Проверьте версию Telegram** - обновите до последней
3. **Убедитесь, что приложение установлено** перед тестированием
4. **Проверьте настройки Android** - разрешения для открытия ссылок

## 🎉 Быстрое решение:

**Используйте Intent URL в Telegram:**
```
intent://interview/123#Intent;scheme=vtbhackaton;package=com.preload.vtb_hackaton.debug;end
```

Этот формат должен работать в Telegram и открывать ваше приложение!

