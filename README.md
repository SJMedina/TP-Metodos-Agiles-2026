## Requisitos

- Docker
- Docker Compose

## Cómo ejecutarlo por primera vez o despues de realizar cambios en el codigo

Desde la raíz del proyecto: (C:\..\TP-Metodos-Agiles-2026\)

```bash
docker compose up --build
```

Eso levanta automáticamente:

- PostgreSQL en Docker
- Spring Boot en `http://localhost:8080`
- Angular en `http://localhost:4200`
- Salud del backend en `http://localhost:8080/api/health`

La base de datos se crea sola con el servicio `db` del `docker-compose.yml`.

## Comandos Docker útiles

### Levantar todo

```bash
docker compose up --build
```

### Levantar en segundo plano

```bash
docker compose up -d --build
```

### Ver logs

```bash
docker compose logs -f
```

### Bajar contenedores

```bash
docker compose down
```

### Bajar contenedores y eliminar volúmenes

```bash
docker compose down -v
```

### Detener temporalmente

```bash
docker compose stop
```

### Volver a iniciar lo que quedó detenido

```bash
docker compose start
```

### Ver estado de los servicios

```bash
docker compose ps
```

Para ejecutar por fuera de Docker, localmente:

### Backend

```powershell
.\mvnw.cmd spring-boot:run
```

### Frontend

```bash
cd frontend
npm install
npm start
```

## Puertos

- Frontend: `4200`
- Backend: `8080`
- PostgreSQL: `5432`
