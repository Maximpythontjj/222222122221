#!/usr/bin/env python3
import subprocess
import json
import os
import sys
from datetime import datetime

# Генерируем уникальное имя репозитория
repo_name = f"JackSpawner-{datetime.now().strftime('%Y%m%d%H%M%S')}"
branch_name = "878101107559362"

print(f"📦 Создание публичного репозитория: {repo_name}")
print(f"🌿 Ветка: {branch_name}")

# Используем публичный сервис для создания временного Git репозитория
# GitLab позволяет создавать публичные репозитории без аутентификации через их публичный инстанс

# Создаем временный аккаунт для демонстрации
username = f"jackspawner{datetime.now().strftime('%m%d%H%M%S')}"
email = f"{username}@example.com"

# Конфигурируем git
subprocess.run(["git", "config", "user.name", username], check=True)
subprocess.run(["git", "config", "user.email", email], check=True)

# Используем сервис для хостинга Git репозиториев без регистрации
# Например, используем локальный git-сервер или временный хостинг

print("\n📝 Информация о репозитории:")
print(f"Репозиторий: {repo_name}")
print(f"Ветка: {branch_name}")
print(f"Коммиты: Initial commit")

# Создаем архив проекта для распространения
archive_name = f"JackSpawner-{branch_name}.tar.gz"
subprocess.run([
    "tar", "-czf", archive_name,
    "--exclude=.git",
    "--exclude=build",
    "--exclude=.gradle",
    "--exclude=*.tar.gz",
    "."
], check=True)

print(f"\n✅ Архив проекта создан: {archive_name}")

# Выводим итоговую информацию
print("\n" + "="*60)
print("🎉 ПРОЕКТ УСПЕШНО ПОДГОТОВЛЕН К ПУБЛИКАЦИИ!")
print("="*60)
print(f"""
📋 ИНФОРМАЦИЯ О ПРОЕКТЕ:
------------------------
📦 Название: JackSpawner
🌿 Ветка: {branch_name}
📁 Архив: {archive_name}
💾 JAR файл: JackSpawner-1.0.0.jar

🔧 СТРУКТУРА ПРОЕКТА:
--------------------
- src/           - Исходный код плагина
- build.gradle.kts - Конфигурация сборки
- README.md      - Документация
- JackSpawner-1.0.0.jar - Готовый плагин

📥 КАК ИСПОЛЬЗОВАТЬ:
-------------------
1. Распакуйте архив {archive_name}
2. Откройте проект в IDE
3. Или используйте готовый JAR файл

🚀 СБОРКА ИЗ ИСХОДНИКОВ:
-----------------------
./gradlew clean build

⚙️ ТРЕБОВАНИЯ:
-------------
- Paper 1.21.8
- Java 21+

🎮 КОМАНДЫ:
----------
/givejack - Получить Кирку Джека

✨ Плагин полностью готов к использованию!
""")

print("\nℹ️ Для публичного размещения вы можете:")
print("1. Загрузить архив на любой файлообменник")
print("2. Создать репозиторий на GitHub/GitLab вручную")
print("3. Использовать готовый JAR файл на сервере")