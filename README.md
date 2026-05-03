# BudgetServer

Бэкенд-часть Personal Budget: сервис для ведения личного бюджета с аккаунтами
пользователей, JWT-авторизацией, категориями, доходами и расходами. Flutter
клиент из отдельного репозитория работает с этим сервисом через REST API.

Доска проекта: [YouGile](https://zavozik.yougile.com/board/4i11p9qwg9yu)

## Возможности

- Регистрация, вход, обновление токенов и получение текущего профиля.
- JWT access/refresh tokens.
- Обновление профиля и удаление аккаунта.
- CRUD для категорий доходов и расходов.
- CRUD для транзакций с фильтрами по типу, категории и диапазону дат.
- Health-check endpoints и базовые PWA-маршруты.
- Миграции схемы PostgreSQL через Flyway.

## Архитектура

Бэкенд реализован как Kotlin/Ktor modular monolith в каталоге `BudjetBackend`.

- `Application.kt` подключает Ktor-модули: HTTP, сериализацию, безопасность,
  обработку ошибок, роутинг и запуск базы данных.
- `main.kt` поддерживает два режима запуска: сервер и отдельный режим миграций
  через `APP_MODE=migrate`.
- `config/` содержит настройки приложения, CORS/default headers, сериализацию,
  JWT-проверку и обработку исключений.
- `routes/` является HTTP-слоем. Эндпоинты сгруппированы под `/api/auth`,
  `/api/users`, `/api/categories` и `/api/transactions`.
- `auth/` содержит регистрацию, вход, refresh flow, выпуск JWT и хэширование
  паролей.
- `database/` содержит таблицы Exposed, DTO и операции доступа к данным.
- `src/main/resources/db/migration/` содержит SQL-миграции Flyway.
- В корне репозитория лежит инфраструктура запуска: `docker-compose.yaml`,
  `.env.example` и `nginx/nginx.conf`.

## Стек

- Kotlin, Ktor, Netty
- Gradle Kotlin DSL, JVM 21
- PostgreSQL
- Exposed ORM
- Flyway
- JWT auth
- Docker Compose и Nginx

## Основные API

- `GET /health` и `GET /api/health` - состояние сервиса и базы данных.
- `POST /api/auth/register` - создание аккаунта.
- `POST /api/auth/login` - вход и получение токенов.
- `POST /api/auth/refresh` - обновление access/refresh token pair.
- `POST /api/auth/logout` - защищенный logout endpoint.
- `GET /api/auth/me` - профиль текущего пользователя.
- `PATCH /api/users/me` - обновление профиля.
- `DELETE /api/users/me` - удаление аккаунта.
- `GET|POST /api/categories` - список и создание категорий.
- `PATCH|DELETE /api/categories/{id}` - обновление и удаление категории.
- `GET|POST /api/transactions` - список и создание транзакций.
- `GET|PATCH|DELETE /api/transactions/{id}` - чтение, обновление и удаление
  транзакции.

## Локальный запуск

Скопируйте переменные окружения и поднимите полный стек:

```bash
cp .env.example .env
docker compose up --build
```

По умолчанию Nginx публикует API на `http://localhost:8080`.

Для запуска только Ktor-приложения поднимите PostgreSQL отдельно и выполните:

```bash
cd BudjetBackend
./gradlew run
```

Локальные значения по умолчанию описаны в
`BudjetBackend/src/main/resources/application.yaml`. Их можно переопределить
через переменные окружения:

- `DB_URL`
- `DB_USER`
- `DB_PASSWORD`
- `DB_DRIVER`
- `JWT_DOMAIN`
- `JWT_AUDIENCE`
- `JWT_REALM`
- `JWT_SECRET`

## Проверки

```bash
cd BudjetBackend
./gradlew test
```
