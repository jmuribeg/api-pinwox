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
- `GET /api/v1/search?search_query=girls`
- `GET /api/v1/show/{show_id}`
- `POST /api/v1/comments`

### Caché de show en MongoDB

El endpoint `GET /api/v1/show/{show_id}` valida primero si el show existe en MongoDB:

- Si existe, retorna el objeto guardado en la colección `show_cache`.
- Si no existe, consulta TVMaze, guarda la respuesta en Mongo y luego la retorna.

### Guardar comentario

Body:

```json
{
  "show_id": 139,
  "comment": "Muy buena",
  "rating": 5
}
```

Respuesta:

```json
{
  "status": "saved"
}
```

### Endpoint TVMaze Search

Consumimos `http://api.tvmaze.com/search/shows?q=query` y retornamos un arreglo de shows con:

- `id`
- `name`
- `channel` (`network.name` o `webChannel.name`)
- `summary`
- `genres`

### Endpoint TVMaze Show

Consumimos `https://api.tvmaze.com/shows/{show_id}` y retornamos el objeto show completo.

- Recibe: `show_id`
- Retorna: objeto show completo

### Ejemplo POST

```json
{
  "name": "Mr. Robot",
  "genre": "Thriller"
}
```
