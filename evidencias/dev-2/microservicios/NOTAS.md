## Paso 1 · El corte

| Paquete / clase actual (monolito) | Destino |
|---|---|
| `facturacion.*` (dominio, aplicacion, infraestructura) | facturacion-service |
| `medicion.*` | No se mueve: es de Dev 1 |
| `compartido` (NegocioException, ErrorResponse, GlobalExceptionHandler) | Se duplica en facturacion-service |
| `SmartGridApplication`, `application.yaml`, `docker-compose.yaml` | Se reemplazan por los propios del servicio |
| `bruno/SmartGrid_G9/Facturacion` | Se queda en el repo; se agrega un entorno para el puerto 8081 |

## Decisiones del servicio

| Aspecto | Decisión |
|---|---|
| Nombre | facturacion-service |
| Puerto | 8081 |
| Base de datos | PostgreSQL 16, base `facturacion`, puerto 5433 |
| Migraciones | Sin Flyway (el proyecto usa ddl-auto: update) |

**Sobre duplicar `compartido`:** no es un descuido. Un manejador de errores común
implicaría una librería compartida versionada aparte (shared kernel). Cada servicio
necesita su propio manejo de errores, y esta es la primera decisión de arquitectura
que forzó el corte.## Paso 1 · El corte

| Paquete / clase actual (monolito) | Destino |
|---|---|
| `facturacion.*` (dominio, aplicacion, infraestructura) | facturacion-service |
| `medicion.*` | No se mueve: es de Dev 1 |
| `compartido` (NegocioException, ErrorResponse, GlobalExceptionHandler) | Se duplica en facturacion-service |
| `SmartGridApplication`, `application.yaml`, `docker-compose.yaml` | Se reemplazan por los propios del servicio |
| `bruno/SmartGrid_G9/Facturacion` | Se queda en el repo; se agrega un entorno para el puerto 8081 |

## Decisiones del servicio

| Aspecto | Decisión |
|---|---|
| Nombre | facturacion-service |
| Puerto | 8081 |
| Base de datos | PostgreSQL 16, base `facturacion`, puerto 5433 |
| Migraciones | Sin Flyway (el proyecto usa ddl-auto: update) |

**Sobre duplicar `compartido`:** no es un descuido. Un manejador de errores común
implicaría una librería compartida versionada aparte (shared kernel). Cada servicio
necesita su propio manejo de errores, y esta es la primera decisión de arquitectura
que forzó el corte.