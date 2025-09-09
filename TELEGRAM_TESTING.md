# 📱 Тестирование Deep Links в Telegram

## 🎯 Проблема с Debug сборками

**Да, проблема может быть в debug сборках!** Android по-разному обрабатывает deep links в debug и release версиях.

## 🔧 Что исправлено:

### 1. **Debug сборка теперь имеет отдельный package name:**
- Debug: `com.preload.vtb_hackaton.debug`
- Release: `com.preload.vtb_hackaton`

### 2. **Улучшено логирование** для отладки
### 3. **Созданы скрипты** для автоматического тестирования

## 🚀 Как тестировать:

### **Шаг 1: Соберите debug версию**
```bash
./gradlew assembleDebug
```

### **Шаг 2: Установите приложение**
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### **Шаг 3: Тестируйте через ADB (быстрый способ)**
```bash
# Windows
test_deep_links.bat

# Linux/Mac
./test_deep_links.sh
```

### **Шаг 4: Тестируйте в Telegram**

1. **Отправьте ссылку в Telegram:**
   ```
   vtbhackaton://interview/123
   ```

2. **Нажмите на ссылку в Telegram**

3. **Выберите "Открыть в приложении"** (если появится выбор)

4. **Проверьте Toast** - должен появиться "Deep Link ID: 123"

## 🐛 Отладка:

### **Проверьте логи:**
```bash
adb logcat | grep -i "MainActivity\|Deep link\|vtbhackaton"
```

### **Ожидаемые логи:**
```
D/MainActivity: MainActivity onCreate called
D/MainActivity: Intent: Intent { act=android.intent.action.VIEW dat=vtbhackaton://interview/123 }
D/MainActivity: Intent action: android.intent.action.VIEW
D/MainActivity: Intent data: vtbhackaton://interview/123
D/MainActivity: handleDeepLink called with action: android.intent.action.VIEW, data: vtbhackaton://interview/123
D/MainActivity: Deep link received: vtbhackaton://interview/123
D/MainActivity: Scheme: vtbhackaton, Host: null, Path: /interview/123
D/MainActivity: Extracted ID: 123
```

## 🔍 Возможные проблемы:

### **1. Telegram не предлагает открыть в приложении:**
- Убедитесь, что приложение установлено
- Проверьте, что package name правильный: `com.preload.vtb_hackaton.debug`

### **2. Приложение не открывается:**
- Проверьте логи на ошибки
- Убедитесь, что intent-filter правильно настроен

### **3. ID не извлекается:**
- Проверьте формат ссылки: `vtbhackaton://interview/123`
- Убедитесь, что путь содержит `/interview/`

## 📋 Чек-лист для тестирования:

- [ ] Приложение собрано в debug режиме
- [ ] Приложение установлено на устройство
- [ ] ADB подключен и работает
- [ ] Логи показывают обработку deep link
- [ ] Toast показывает правильный ID
- [ ] Ссылка работает из Telegram

## 🎯 Альтернативные способы тестирования:

### **1. Через браузер:**
Откройте `test_links.html` в браузере на устройстве

### **2. Через другие приложения:**
- WhatsApp
- SMS
- Email

### **3. Через QR-код:**
Создайте QR-код со ссылкой `vtbhackaton://interview/123`

## ⚠️ Важно для продакшена:

Для release версии нужно будет:
1. Убрать `.debug` суффикс из package name
2. Подписать приложение релизным ключом
3. Настроить домен для HTTPS ссылок

