# Notas — Comunicación entre microservicios (Smart Grid G9)

## Resumen

Se implementaron las seis piezas de la Lección 6 sobre los tres microservicios: gRPC, Circuit Breaker, Eureka, API Gateway, Kafka con Transactional Outbox y Saga.
La relación que justifica la comunicación es **facturación → medición**: una factura referencia un medidor por `medidorId`, igual que en el taller una publicación referencia a un investigador.

## Puertos

| Componente | Puerto |
|---|---|
| gateway | 8000 |
| medicion-service | 8080 (HTTP), 9090 (gRPC) |
| facturacion-service | 8081 |
| usuarios-service | 8083 |
| eureka-server | 8761 |
| Kafka | 9092 |

## Qué se comunica y cómo

| Necesidad | Patrón | Dónde |
|---|---|---|
| Confirmar que el medidor existe antes de generar una factura | gRPC síncrono (`MedidorGrpc/ExisteMedidor`) | facturacion → medicion |
| No caer en cascada si medicion está caído | Circuit Breaker (ventana 10, mínimo 5 llamadas, umbral 50 %, 10 s abierto, plazo de 5 s por llamada) | facturacion |
| Sin direcciones fijas para HTTP | Eureka (`lb://nombre-del-servicio`) | los tres servicios |
| Un único punto de entrada | Gateway en el 8000 | `/api/medidores/**`, `/api/facturas/**`, `/api/auth/**`, `/api/usuarios/**` |
| Notificar un medidor registrado | Domain Event con Transactional Outbox | medicion → facturacion |
| Eliminar un medidor y marcar sus facturas | Saga por coreografía | medicion ↔ facturacion |

## Tópicos de Kafka

| Tópico | Productor | Consumidor |
|---|---|---|
| `medidores.registrados` | medicion (outbox) | facturacion (solo registra en el log) |
| `medidores.en-eliminacion` | medicion (outbox) | facturacion |
| `facturas.marcadas-huerfanas` | facturacion (KafkaTemplate directo) | medicion |

## Flujo de la Saga

1. `DELETE /api/medidores/{id}` responde **202**: el medidor pasa a `EN_ELIMINACION` y se guarda el evento en el outbox.
2. facturacion recibe `medidores.en-eliminacion`, marca las facturas del medidor como huérfanas y publica `facturas.marcadas-huerfanas`.
3. medicion recibe la confirmación y elimina físicamente el medidor.

## Orden de arranque

1. Docker Desktop abierto.
2. Kafka: `cd infra` y `docker compose up -d`.
3. eureka-server.
4. medicion-service, facturacion-service y usuarios-service (cada uno levanta su base con Docker Compose).
5. gateway, y esperar de 30 a 60 s a que todos aparezcan UP en `http://localhost:8761`.

## Decisiones de arquitectura

- **Kafka en `infra/docker-compose.yaml`** y no en el compose de un servicio, porque los servicios usan `spring-boot-docker-compose`, que levanta el compose de su propia carpeta.
- **Sin Flyway.** Los servicios usan `ddl-auto: update`, así que la tabla `outbox_event` sale de la entidad. Las columnas nuevas (`estado` en medidores, `huerfana` en facturas) llevan valor por defecto en la base para no romper las filas existentes.
- **`RegistrarMedidorApplicationService` dejó de ser `final`** y el caso de uso nuevo tampoco lo es: `@Transactional` necesita un proxy y una clase `final` no se puede proxificar. La transacción es lo que hace atómico el guardado del medidor y del evento.
- **`MedidorRepositoryPort.eliminar` es un método `default`**, igual que `listar()`, para que `FakeMedidorRepository` en los tests siga compilando.
- **El contrato `.proto` se copia en cada servicio** (con su propio `java_package`) para que ninguno dependa del código del otro.
- **Dos respuestas distintas:** 404 si el medidor no existe y 503 si no se pudo averiguar si existe (circuito abierto o medicion caído).
- **Usuarios no participa en gRPC, Kafka ni Saga:** en el código actual `Medidor` no tiene propietario, así que no hay relación medidor–usuario sobre la cual comunicar.
- **El Gateway reenvía el header `Authorization`** y es usuarios-service quien valida el JWT, como antes.

## Limitaciones conocidas

- El canal gRPC de facturación apunta a `static://localhost:9090`. Eureka resuelve las llamadas HTTP balanceadas; integrarlo con el balanceo de canales gRPC queda fuera del alcance.
- La confirmación `facturas.marcadas-huerfanas` se envía sin outbox. Si facturación cae entre guardar las facturas y enviar la confirmación, la Saga queda detenida.
- No hay compensación por timeout: si la Saga se atasca, el medidor queda en `EN_ELIMINACION` indefinidamente. Es el reto opcional del taller.
- `medidores.registrados` solo se registra en el log de facturación; todavía no dispara ninguna acción de negocio.
- Con un grupo de consumidores nuevo y `auto-offset-reset: earliest` se reprocesan eventos viejos al reiniciar. No afecta el resultado.


---

# Parte 2 — Facturación completa los requisitos de su subdominio

## Resumen

Facturación pasó de recibir consumo, período completo y tarifa por el cuerpo de la petición a derivarlos: el consumo y el período completo llegan de medicion-service por gRPC, la tarifa es una entidad propia, y las anomalías no resueltas se guardan en la factura. Además se protegen los endpoints con el JWT de usuarios.

## Cambios por servicio

| Servicio | Cambio |
|---|---|
| usuarios-service | Servidor gRPC `UsuarioGrpc/ExisteUsuarioActivo` en el puerto 9091, que usa medición al registrar un medidor |
| medicion-service | `GrpcSecurityConfig` con `permitAll`, para que el servidor gRPC del 9090 acepte llamadas entre servicios |
| facturacion-service | JWT, medidores locales, tarifa, factura del período, facturación mensual, pago con fecha, "mis facturas" |
| gateway | Ruta `/api/tarifas/**` hacia facturación |

## Contrato de `POST /api/facturas`

El cuerpo ahora pide solo `medidorId`, `periodoInicio` y `periodoFin`. Antes pedía también el consumo, `periodoCompleto` y los datos de la tarifa. `FacturaResponse` agrega `fechaPago` y `anomaliasNoResueltas`, además del detalle de consumo, tarifa y vencimiento de la parte anterior.

## Endpoints de facturación

| Método y ruta | Acceso |
|---|---|
| `POST /api/facturas` | OPERADOR |
| `POST /api/facturas/mensual?mes=AAAA-MM` | OPERADOR |
| `GET /api/facturas/mias` | Cualquier usuario autenticado |
| `GET /api/facturas/{id}` | OPERADOR o dueño del medidor |
| `GET /api/facturas?medidorId=` | OPERADOR o dueño del medidor |
| `POST /api/facturas/{id}/pago` | OPERADOR |
| `POST /api/tarifas` | OPERADOR |
| `GET /api/tarifas` | Cualquier usuario autenticado |

## Reglas implementadas

- **Período cerrado:** solo se factura si `periodoFin` es anterior a hoy.
- **Factura única:** una por medidor y período.
- **Período completo:** se deriva de medición. Si está incompleto, no se factura.
- **Tarifa:** una sola tarifa vigente global. Se elige la que cubre `periodoFin`, la factura guarda una copia, y las vigencias no pueden superponerse.
- **Vencimiento:** `fechaVencimiento` es la fecha de generación más 15 días. `VENCIDA` se calcula al consultar y no se guarda.
- **Pago:** registrarlo guarda `fechaPago`. Una factura ya pagada responde 409.
- **Anomalías:** las no resueltas del período se copian a la factura al generarla (regla 3).
- **Mensual:** tarea programada el día 1 a las 02:00 (America/Bogota) para el mes anterior, más el endpoint manual. El resumen devuelve `generadas`, `duplicadas` y `rechazadas`.

## Comunicación

- **Consumo y anomalías:** gRPC hacia medición (`ObtenerConsumo`, `AnomaliasNoResueltas`). El adaptador tiene su propio Circuit Breaker, y un `INVALID_ARGUMENT` no cuenta como falla del circuito.
- **Fechas hacia medición:** `desde` es 00:00:00Z de `periodoInicio` y `hasta` es 23:59:59Z de `periodoFin`, en UTC.
- **Medidores locales:** la tabla `medidores_locales` se alimenta de `medidores.registrados` (con `usuarioId` si viene) y se desactiva con `medidores.en-eliminacion`. Con ella, facturación sabe de quién es cada medidor sin consultar a medición.
- **Seguridad:** JWT HS256 con el secreto compartido `jwt.secret`. Se usan los claims `sub` (id del usuario) y `roles`.
