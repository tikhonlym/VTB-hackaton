# 🔗 Полное руководство по Deep Links в Android

## 🎯 Как работает система Deep Links

### **Принцип работы:**
1. **Пользователь кликает на ссылку** в любом приложении (Telegram, браузер, SMS)
2. **Android анализирует ссылку** и ищет приложения, которые могут её обработать
3. **Система показывает диалог выбора** или сразу открывает приложение
4. **Приложение получает ссылку** и обрабатывает её

## 📱 Настройка приложения для ожидания ссылок

### **1. AndroidManifest.xml - Intent Filters**

Ваше приложение уже настроено! Вот что у вас есть:

```xml
<!-- Custom scheme (работает сразу) -->
<intent-filter>
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    <data android:scheme="vtbhackaton" />
</intent-filter>

<!-- HTTPS ссылки (нужен домен) -->
<intent-filter android:autoVerify="true">
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    <data android:scheme="https"
          android:host="vtbhackaton.app" />
</intent-filter>
```

### **2. MainActivity - Обработка ссылок**

Ваш код уже готов! Вот что происходит:

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // Обработка deep link при запуске приложения
    handleDeepLink(intent)
}

override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    // Обработка deep link когда приложение уже запущено
    handleDeepLink(intent)
}
```

## 🔗 Типы ссылок, которые может обрабатывать ваше приложение

### **1. Custom Scheme (работает сразу)**
```
vtbhackaton://interview/123
vtbhackaton://interview/456
vtbhackaton://profile/user789
```

### **2. HTTPS ссылки (нужен домен)**
```
https://vtbhackaton.app/interview/123
https://vtbhackaton.app?id=456
https://vtbhackaton.app/profile/user789
```

### **3. Intent URL (для мессенджеров)**
```
intent://interview/123#Intent;scheme=vtbhackaton;package=com.preload.vtb_hackaton;end
```

## 🧪 Как тестировать Deep Links

### **1. Через ADB (командная строка)**
```bash
# Тест custom scheme
adb shell am start -W -a android.intent.action.VIEW -d "vtbhackaton://interview/123" com.preload.vtb_hackaton

# Тест HTTPS
adb shell am start -W -a android.intent.action.VIEW -d "https://vtbhackaton.app?id=123" com.preload.vtb_hackaton
```

### **2. Через браузер**
1. Откройте браузер на устройстве
2. Введите ссылку: `vtbhackaton://interview/123`
3. Нажмите Enter
4. Выберите ваше приложение

### **3. Через мессенджеры**
1. Отправьте ссылку в Telegram/WhatsApp
2. Нажмите на ссылку
3. Выберите ваше приложение

### **4. Через QR-код**
1. Создайте QR-код со ссылкой
2. Отсканируйте камерой
3. Выберите ваше приложение

## ⚙️ Настройка приложения как обработчика по умолчанию

### **Через настройки Android:**
1. **Настройки** → **Приложения** → **VTB Hackaton**
2. **"Открывать по умолчанию"** → **"Открывать поддерживаемые ссылки"**
3. **Включите переключатель**

### **Через ADB:**
```bash
# Установить как обработчик для custom scheme
adb shell pm set-app-links --package com.preload.vtb_hackaton 1 vtbhackaton://

# Установить как обработчик для HTTPS
adb shell pm set-app-links --package com.preload.vtb_hackaton 1 https://vtbhackaton.app
```

## 🎯 Практические примеры

### **Пример 1: Ссылка на интервью**
```
vtbhackaton://interview/123
```
**Результат:** Приложение откроется и покажет интервью с ID 123

### **Пример 2: Ссылка на профиль**
```
vtbhackaton://profile/user456
```
**Результат:** Приложение откроется и покажет профиль пользователя 456

### **Пример 3: Ссылка с параметрами**
```
vtbhackaton://interview/789?mode=test&timer=30
```
**Результат:** Приложение откроется с интервью 789 в тестовом режиме на 30 секунд

## 🔍 Отладка Deep Links

### **Проверка настроек:**
```bash
# Проверить App Links
adb shell pm get-app-links com.preload.vtb_hackaton

# Проверить обработчики
adb shell pm query-activities -a android.intent.action.VIEW -d "vtbhackaton://test"
```

### **Проверка логов:**
```bash
adb logcat | grep -i "MainActivity\|Deep link\|vtbhackaton"
```

## 🚀 Быстрый старт

### **1. Убедитесь, что приложение установлено**
```bash
adb shell pm list packages | grep vtb_hackaton
```

### **2. Настройте как обработчик**
```bash
adb shell pm set-app-links --package com.preload.vtb_hackaton 1 vtbhackaton://
```

### **3. Протестируйте**
```bash
adb shell am start -W -a android.intent.action.VIEW -d "vtbhackaton://interview/123" com.preload.vtb_hackaton
```

### **4. Проверьте результат**
- Приложение должно открыться
- Должен появиться Toast с ID: "Deep Link ID: 123"
- ID сохранится в SharedPreferences

## 🎉 Готово!

Ваше приложение уже настроено для обработки deep links! Просто:

1. **Установите приложение** на устройство
2. **Настройте как обработчик** (через ADB или настройки)
3. **Отправьте ссылку** в любое приложение
4. **Нажмите на ссылку** - ваше приложение откроется!

**Ваше приложение будет "ждать" кликов по ссылкам автоматически!** 🎯

