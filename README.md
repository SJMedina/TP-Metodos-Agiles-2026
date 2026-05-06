# TP-Metodos-Agiles-2026

## Requisitos

- Java 17 o superior
- Maven 3.8 o superior
- Docker
- Docker Compose

### Ejecución

Desde la raíz del proyecto, ejecutar:

```bash
docker compose up --build
```

### Otros comandos utiles

# Levantar en primer plano (útil para ver logs en la terminal)
docker compose up --build

# Iniciar contenedores parados
docker compose start

# Bajar y eliminar contenedores y red (preserva volúmenes)
docker compose down