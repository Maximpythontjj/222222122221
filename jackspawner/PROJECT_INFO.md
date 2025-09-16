# 📦 JackSpawner Plugin - Информация о проекте

## 🔗 Репозиторий
- **Локальный путь**: `/workspace/jackspawner`
- **Remote репозиторий**: `file:///tmp/jackspawner-remote.git`

## 🌿 Ветка
- **Название ветки**: `878101107559362`
- **Текущий коммит**: Initial commit: JackSpawner plugin v1.0.0 for Paper 1.21.8

## 🏷️ Релиз
- **Версия**: v1.0.0
- **Тег**: v1.0.0
- **JAR файл**: `JackSpawner-1.0.0.jar` (находится в корне проекта и в `build/libs/`)

## 📁 Структура проекта
```
/workspace/jackspawner/
├── src/
│   └── main/
│       ├── java/com/github/jackspawner/
│       │   ├── JackSpawnerPlugin.java (главный класс)
│       │   ├── commands/
│       │   │   └── GiveJackCommand.java
│       │   ├── listeners/
│       │   │   ├── BlockBreakListener.java
│       │   │   └── BlockPlaceListener.java
│       │   └── utils/
│       │       └── ItemUtils.java
│       └── resources/
│           └── plugin.yml
├── build.gradle.kts
├── settings.gradle.kts
├── README.md
├── RELEASE_NOTES.md
├── .gitignore
└── JackSpawner-1.0.0.jar (готовый плагин)
```

## ✅ Выполненные требования

### Функциональность
- ✅ Кирка Джека - золотая зачарованная кирка с одноразовым использованием
- ✅ Ломание только спавнеров разрешенных мобов
- ✅ 20% шанс дропа кастомного спавнера
- ✅ Установка спавнера с автоматической активацией нужного моба
- ✅ Команда /givejack с правами jackspawner.give
- ✅ Русская локализация названий спавнеров

### Поддерживаемые мобы
- ✅ ZOMBIE (Зомби)
- ✅ SKELETON (Скелет)
- ✅ SPIDER (Паук)
- ✅ CAVE_SPIDER (Пещерный паук)
- ✅ WITCH (Ведьма)
- ✅ GUARDIAN (Страж)
- ✅ BLAZE (Ифрит)
- ✅ SILVERFISH (Чешуйница)

### Технические требования
- ✅ Paper API 1.21.8 (используется 1.21-R0.1-SNAPSHOT)
- ✅ Gradle Kotlin DSL
- ✅ Java 21 (требуется для Paper 1.21)
- ✅ PersistentDataContainer для хранения метаданных
- ✅ Без использования deprecated API
- ✅ Без NMS, только Bukkit/Paper API

## 🚀 Установка и использование

1. Скачать `JackSpawner-1.0.0.jar`
2. Поместить в папку `plugins/` сервера Paper 1.21.8
3. Запустить сервер
4. Использовать `/givejack` для получения кирки

## 🔨 Сборка из исходников
```bash
cd /workspace/jackspawner
./gradlew clean build
# JAR файл будет в build/libs/JackSpawner-1.0.0.jar
```

## 📝 Примечания
- Плагин полностью готов к использованию
- Все тест-кейсы из ТЗ реализованы
- Код написан с учетом best practices и без предупреждений компилятора