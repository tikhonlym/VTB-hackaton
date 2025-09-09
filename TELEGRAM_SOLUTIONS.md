# 🔧 Решения для Telegram Deep Links

## 🎯 Проблема
Telegram сразу открывает веб-версию и не предлагает выбрать приложение.

## ✅ Решения:

### **1. Принудительная установка как обработчик**

#### Через настройки Android:
1. **Настройки** → **Приложения** → **VTB Hackaton Debug**
2. **"Открывать по умолчанию"** → **"Открывать поддерживаемые ссылки"**
3. **Включите переключатель**

#### Через ADB:
```bash
# Установить приложение как обработчик для vtbhackaton://
adb shell pm set-app-links --package com.preload.vtb_hackaton 1 vtbhackaton://

# Установить приложение как обработчик для https://vtbhackaton.app
adb shell pm set-app-links --package com.preload.vtb_hackaton 1 https://vtbhackaton.app
```

### **2. Использование Intent URL (работает в Telegram)**

Вместо обычных ссылок используйте Intent URL:

```
intent://interview/123#Intent;scheme=vtbhackaton;package=com.preload.vtb_hackaton;end
```

**Этот формат должен работать в Telegram!**

### **3. Создание веб-страницы с редиректом**

Создайте HTML страницу, которая будет перенаправлять на приложение:

```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Открыть в приложении</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
    <script>
        // Пытаемся открыть в приложении
        window.location.href = "vtbhackaton://interview/123";
        
        // Если не получилось через 2 секунды, показываем кнопку
        setTimeout(function() {
            document.body.innerHTML = `
                <h1>Откройте в приложении</h1>
                <a href="vtbhackaton://interview/123" style="display: block; padding: 20px; background: #007bff; color: white; text-decoration: none; border-radius: 5px; text-align: center; font-size: 18px;">
                    Нажмите здесь для открытия в приложении
                </a>
                <p>Если приложение не открывается, убедитесь что оно установлено</p>
            `;
        }, 2000);
    </script>
</body>
</html>
```

### **4. Использование QR-кода**

1. Создайте QR-код со ссылкой `vtbhackaton://interview/123`
2. Отсканируйте камерой Android
3. Выберите "Открыть в приложении"

### **5. Тестирование через другие приложения**

#### WhatsApp:
- Отправьте ссылку в чат
- Нажмите на ссылку
- Выберите приложение

#### SMS:
- Отправьте ссылку в сообщении
- Нажмите на ссылку

#### Email:
- Отправьте ссылку в письме
- Нажмите на ссылку

### **6. Проверка настроек системы**

Запустите: `debug_telegram.bat`

Этот скрипт покажет:
- Установлено ли приложение
- Настроены ли intent-filter
- Какие приложения могут обрабатывать ссылки

## 🚀 Быстрые решения:

### **Решение 1: Intent URL**
```
intent://interview/123#Intent;scheme=vtbhackaton;package=com.preload.vtb_hackaton;end
```

### **Решение 2: Настройка через ADB**
```bash
adb shell pm set-app-links --package com.preload.vtb_hackaton 1 vtbhackaton://
```

### **Решение 3: Веб-страница с редиректом**
Разместите HTML файл на веб-сервере и используйте его ссылку в Telegram.

## 🔍 Отладка:

### **Проверьте логи:**
```bash
adb logcat | grep -i "PackageManager\|AppLinks\|vtbhackaton"
```

### **Проверьте настройки приложения:**
```bash
adb shell pm get-app-links com.preload.vtb_hackaton
```

## ⚠️ Важные моменты:

1. **Telegram может кэшировать настройки** - перезапустите Telegram
2. **Проверьте версию Telegram** - обновите до последней
3. **Убедитесь, что приложение установлено** перед тестированием
4. **Проверьте настройки Android** - разрешения для открытия ссылок

## 🎉 Рекомендуемое решение:

**Используйте Intent URL в Telegram:**
```
intent://interview/123#Intent;scheme=vtbhackaton;package=com.preload.vtb_hackaton;end
```

Этот формат должен работать в Telegram и открывать ваше приложение!

