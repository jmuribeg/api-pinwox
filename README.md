# STS Maven API (Java)

API REST en Spring Boot lista para abrir en STS.

## Requisitos

- Java 17+
- Maven 3.9+

## Abrir en STS

1. File > Import > Maven > Existing Maven Projects
2. Selecciona la carpeta `sts-maven-api`
3. Ejecuta `StsMavenApiApplication` como Spring Boot App

## Ejecutar por terminal

```bash
mvn spring-boot:run
```

La API arranca en `http://localhost:8081`.

## Endpoints

- `GET /api/v1/health`
- `GET /api/v1/shows`
- `GET /api/v1/shows/{id}`
- `POST /api/v1/shows`

### Ejemplo POST

```json
{
  "name": "Mr. Robot",
  "genre": "Thriller"
}
```
