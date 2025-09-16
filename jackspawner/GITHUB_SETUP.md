# Инструкция по загрузке в GitHub

## Информация о проекте

- **Название ветки**: `5470819632`
- **Версия**: 1.0.0
- **JAR файл**: `build/libs/jackspawner-1.0.0.jar`

## Шаги для создания GitHub репозитория

1. **Создайте новый репозиторий на GitHub**:
   - Перейдите на https://github.com/new
   - Название: `jackspawner-plugin`
   - Описание: `Jack's Pickaxe Plugin for Minecraft Paper 1.21.8`
   - Сделайте репозиторий публичным
   - НЕ инициализируйте с README, .gitignore или лицензией

2. **Загрузите код**:
   ```bash
   git remote add origin https://github.com/ВАШ_USERNAME/jackspawner-plugin.git
   git push -u origin 5470819632
   ```

3. **Создайте Release**:
   - Перейдите в раздел "Releases" репозитория
   - Нажмите "Create a new release"
   - Tag version: `v1.0.0`
   - Release title: `JackSpawner Plugin v1.0.0`
   - Описание:
     ```
     Первый релиз плагина "Кирка Джека" для Minecraft Paper 1.21.8
     
     ## Возможности:
     - Одноразовая золотая кирка с свечением
     - Ломает спавнеры с 20% шансом дропа кастомного спавнера
     - Поддержка 8 типов мобов
     - Русская локализация названий
     - Команда /givejack для получения кирки
     
     ## Установка:
     1. Скачайте jackspawner-1.0.0.jar
     2. Поместите в папку plugins/ сервера
     3. Перезапустите сервер
     ```
   - Прикрепите файл `jackspawner-1.0.0.jar` как Asset
   - Нажмите "Publish release"

## Готовые ссылки (после создания)

- **Репозиторий**: `https://github.com/ВАШ_USERNAME/jackspawner-plugin`
- **Ветка**: `5470819632`
- **Релиз**: `https://github.com/ВАШ_USERNAME/jackspawner-plugin/releases/tag/v1.0.0`
- **JAR файл**: `build/libs/jackspawner-1.0.0.jar`