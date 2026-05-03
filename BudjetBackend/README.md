# BudjetBackend

Ktor-модуль бэкенда Personal Budget. Основной README репозитория находится на
уровень выше: `../README.md`.

Доска проекта: [YouGile](https://zavozik.yougile.com/board/4i11p9qwg9yu)

## Что делает модуль

Модуль предоставляет REST API для авторизации, профиля пользователя, категорий
и транзакций личного бюджета. Данные хранятся в PostgreSQL, приватные маршруты
защищены JWT access token, а изменения схемы базы данных ведутся через Flyway.

## Архитектура

- `Application.kt` настраивает Ktor-плагины и модули приложения.
- `main.kt` запускает сервер или выполняет миграции при `APP_MODE=migrate`.
- `config/` содержит настройки приложения, HTTP, сериализацию, security и
  status pages.
- `routes/` содержит HTTP endpoints под `/api`.
- `auth/` содержит auth service, DTO токенов и хэширование паролей.
- `database/` содержит таблицы Exposed, DTO и операции доступа к данным.
- `resources/db/migration/` содержит SQL-миграции Flyway.

## Локальные команды

```bash
./gradlew run
./gradlew test
```

Для полного Docker Compose стека с PostgreSQL и Nginx используйте корень
репозитория:

```bash
cd ..
cp .env.example .env
docker compose up --build
```
