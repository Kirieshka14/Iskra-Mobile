# Iskra-Mobile

Мобильный клиент IskraAi для Android и iOS. Работает напрямую с сервером API без WebView-оболочки.

## Установка готовой сборки

Скачайте установочный файл из раздела **Releases**:

- **Android**:
  1. Скачайте файл `.apk` (`Iskra-Android-v0.8.0.apk`) на устройство.
  2. Откройте файл через проводник или браузер.
  3. Если система запросит разрешение на установку из неизвестных источников, подтвердите его и завершите установку.
- **iOS**:
  1. Скачайте файл `.ipa` из раздела Releases.
  2. Установите его на устройство с помощью AltStore, Sideloadly или через Xcode.

## Настройка (`iskra.config.json`)

Параметры клиента задаются в файле `iskra.config.json`:

```json
{
  "clientName": "Iskra Mobile Client",
  "clientVersion": "0.8.0",
  "targetPlatform": "android",
  "apiUrl": "https://bacand-iskra.onrender.com",
  "clientKey": "",
  "theme": {
    "accent": "#f43f5e",
    "background": "#0d0f12",
    "surface": "#161920"
  }
}
```

- `apiUrl` — базовый URL сервера API.
- `clientKey` — опциональный ключ клиента.
- `theme` — основные цвета оформления интерфейса.

## Сборка из исходников

### Требования
- **JDK 17** или выше.
- **Android SDK** (для сборки под Android).
- **Xcode** на macOS (для сборки под iOS).

### Сборка Android APK
```bash
./gradlew :composeApp:assembleDebug
```
Готовый `.apk` файл будет находиться по пути `composeApp/build/outputs/apk/debug/composeApp-debug.apk`.

### Сборка для iOS
Соберите фреймворк общего модуля и откройте проект `iosApp` в Xcode:
```bash
./gradlew :composeApp:linkReleaseFrameworkIosArm64
```
