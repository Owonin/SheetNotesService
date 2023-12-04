# Сервис нотных записей

Данный репозиторий содержит beckend часть учебного проекта обработки музыкальных нотаций, написанный на Java.

## Описание

Веб-приложение распознавания музыки реализует функционал хранения нотаций в png и midi форматах, а также позволяющий распознавать фотографии или скриншоты нотных записей в формате midi.

## Структура проекта

- **authService**: Сервис авторизации.
- **fileService**: Сервис хранения файлов.
- **notesService**: Сервис хранения нотаций.
- **recognition**: Сервис распознавания.
  - **Mozart**: Сисема распознавания нот [Mozart](https://github.com/aashrafh/Mozart)
  - `recognition_service.py`: Сервис генерации midi файлов.
- **docs**: Сгенерированния спецификация OpenApi.
  - `auth_service_api-docs.json`: Документация API сервиса авторизации
  - `file_service_api-docs.json`: Документация API файл сервиса
  - `notes_service_api-docks.json`: Документация API сервиса хранения нотаций
- `README.md`

## Список использованных технологий и библиотек

- Maven
- Lombok
- Spring Boot
- Spring Web
- Spring Security
- Spring Data JPA
- PosgreSQL
- Redis
- Apache Kafka
- OpenAPI