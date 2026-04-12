# Проектная работа яндекс практикум, спринт 9.

Микросервисное приложение «Банк» с использованием Spring Boot, интеграций Spring Cloud и паттернов микросервисной архитектуры.
- аутентификацию пользователя через Keycloak (Authorization Code Flow);
- проброс пользовательского JWT через API Gateway (Token Relay);
- проверку прав пользователя и сервисов на основе ролей realm_access;
- вызовы между микросервисами через Client Credentials Flow;
- разделение ответственности между **UI, Gateway, Service Discovery, Consul Config и микросервисов**.

---

## Архитектурные принципы

Проект реализует паттерны **Microservice Architecture, API Gateway, Service Discovery, Externalized Configuration**:

- Фронт (Front UI) — это веб-приложение с клиентским HTML-интерфейсом;
- Микросервис аккаунтов (Accounts) - хранит информацию о зарегистрированных аккаунтах и их счетах;
- Микросервис обналичивания денег (Cash) - осуществляет пополнение счёта или снятие денег со счёта;
- Сервис перевода денег между счетами (Transfer) - осуществляет перевод денег между счетами разных пользователей;
- Сервис уведомлений (Notifications) - отправляет уведомления о выполненном действии: переводе денег, пополнении счёта, снятии денег со счёта и т. д.;
- Единая база данных с разными схемами для каждого микросервиса;
- Используют единый стек технологий (Spring Boot + PostgreSQL);
- Используется единый .env файл.

---

## Что внутри

- **Spring Security** сервер авторизации OAuth2 на Keycloack, Consul (Gateway, Discovery, Config)
- 
- **Микросервисы** `Front UI`, `Accounts`, `Cash`, `Transfer` и `Notifications`

- **REST-контроллеры** `MainController`, `AccountController`, `CashController` , `TransferController` и `NotificationController`

- **DAO-слой**: `Account` репозиторий `JpaRepository`

- **База данных PostgreSQL** с использованием **Liquibase** с единой базой данных, но разделенной схемой для каждого сервиса

- **Тесты**:
    - пакет `service` — unit тесты сервиса и тест Redis Cache сервиса
    - пакет `controller` и `repository` — интеграционные и WebMvc тесты контроллера и репозитория
    - реализованы контракт тесты

---

## Запуск приложения

- **Создать в корне проекта файл .env:** с указанием `DB_HOST, POSTGRES_DB, POSTGRES_USER, POSTGRES_PASSWORD, POSTGRES_PORT, ACCOUNT_DB_SCHEMA, CASH_DB_SCHEMA, TRANSFER_DB_SCHEMA, NOTIFICATION_DB_SCHEMA, CONSUL_PORT, KEYCLOAK_PORT, KEYCLOAK_ADMIN, KEYCLOAK_ADMIN_PASSWORD, KEYCLOAK_URL, FRONT_PORT, ACCOUNT_PORT, CASH_PORT, TRANSFER_PORT, NOTIFICATION_PORT, GATEWAY_PORT=8086`

- **Запуск:** `docker-compose up --build`

- **Приложение будет доступно по адресу:** `http://localhost:8081`