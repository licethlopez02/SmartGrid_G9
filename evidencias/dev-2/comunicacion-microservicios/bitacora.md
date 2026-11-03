# Bitácora — Comunicación entre microservicios (Medición y Facturación)

**Proyecto:** Smart Grid G9 · **Rama:** `comunicacion-microservicios`
**Autora:** Liceth López · **Fechas:** 29 y 30 de octubre de 2026
**Servicios:** medicion-service, facturacion-service
**Taller base:** Lección 6 de Ingeniería de Software II (rica-api), adaptado a Smart Grid.

## Tiempos por paso

El tiempo registrado es el intervalo entre commits, con la hora de inicio de la sesión (20:05 del 29 de octubre) como referencia para el primero. Incluye depuración.

| Paso | Tema | Estimado en el taller | Registrado | Commit | Fecha y hora del commit |
|---|---|---|---|---|---|
| 0 + 1 + 2A | Preparación, plan y servidor gRPC en medicion | 15 + 15 + ~25 min | ~39 min | `16ef68b` | 29-oct 20:44 |
| 2B | Cliente gRPC en facturacion | ~25 min | ~47 min | `bcfdbaa` | 29-oct 21:31 |
| 3 | Circuit Breaker (Resilience4j) | ~30 min | ~24 min | `8bc044b` | 29-oct 21:56 |
| 4 | Eureka: servidor y registro de los 3 servicios | ~40 min | ~136 min | `b08eceb`, `a3ad108` | 30-oct 00:12 |
| 5 | API Gateway | ~35 min | ~9 min | `828e189` | 30-oct 00:21 |
| 6A + 6B | Kafka (`infra/`) y Transactional Outbox en medicion | ~45 min | ~50 min | `481faec`, `2b6b6b4` | 30-oct 10:47 |
| 6C | Consumidor de `medidores.registrados` en facturacion | ~30 min | ~16 min | `e2ed9d7` | 30-oct 11:04 |
| 7A | Saga: parte de medicion | ~35 min | ~10 min | `913ab5f` | 30-oct 11:14 |
| 7B | Saga: parte de facturacion | ~25 min | ~9 min | `ec67215` | 30-oct 11:23 |
| 8 | Verificación de punta a punta | ~20 min | ~30 min | — | — |
| 9 | Push | ~10 min | ~8 min | — | — |


## Commits de la comunicación

| Hash | Mensaje |
|---|---|
| `16ef68b` | feat(medicion): servidor gRPC ExisteMedidor en el puerto 9090 |
| `bcfdbaa` | feat(facturacion): cliente gRPC para verificar el medidor antes de generar la factura |
| `8bc044b` | feat(facturacion): Circuit Breaker Resilience4j hacia medicion y respuesta 503 |
| `b08eceb` | feat(infra): eureka-server en el puerto 8761 |
| `a3ad108` | feat: registro de medicion, facturacion y usuarios en Eureka |
| `828e189` | feat(gateway): API Gateway en el puerto 8000 con rutas por nombre logico |
| `481faec` | feat(infra): Kafka en modo KRaft con docker compose |
| `2b6b6b4` | feat(medicion): Transactional Outbox para el evento medidores.registrados |
| `e2ed9d7` | feat(facturacion): consumidor del evento medidores.registrados |
| `913ab5f` | feat(medicion): inicio y cierre de la Saga de eliminacion de medidor |
| `ec67215` | feat(facturacion): marcar facturas huerfanas al eliminarse un medidor (Saga) |


## Evidencias

Las capturas están en `evidencias/dev-2/comunicacion-microservicios/capturas`.

| Archivo | Qué muestra |
|---|---|
| `00-docker-y-servicios.png` | Docker Desktop y los tres servicios con `Started` |
| `2a-grpcurl.png` | `grpcurl list` y las respuestas de `ExisteMedidor` |
| `2b-factura-201-404.png` | Factura 201 (medidor existente) y 404 (inexistente) |
| `3-circuit-breaker.png` | `CLOSED_TO_OPEN`, `HALF_OPEN_TO_CLOSED` y el 503 |
| `4-eureka.png` | Eureka con los tres servicios en UP |
| `5-gateway.png` | Misma respuesta por el 8000 y por el 8081, y login por el Gateway |
| `6b-outbox-pendiente.png` | Fila de `outbox_event` con `publicado = f` |
| `6c-kafka-apagado.png` | WARN con Kafka apagado, evento publicado después, `publicado = t` |
| `6d-evento-recibido.png` | Log de facturación con `Evento recibido [medidores.registrados]` |
| `7-saga.png` | 202, los tres logs de la Saga y el `SELECT` sin el medidor |
| `8-punta-a-punta.png` | Las cinco terminales, Eureka con cuatro componentes UP y la Saga |

## Estado final

| Pieza | Estado |
|---|---|
| gRPC | Hecho |
| Circuit Breaker | Hecho |
| Eureka | Hecho |
| API Gateway | Hecho |
| Kafka + Transactional Outbox | Hecho |
| Saga | Hecho |
| Verificación de punta a punta (Paso 8) | Hecho |


---

# Parte 2 — Facturación: requisitos pendientes

**Rama:** `comunicacion-microservicios` (misma rama de la parte 1) · **Fecha:** 2 de noviembre de 2026
**Alcance:** servidor gRPC de usuarios, validación de JWT en facturación, medidores locales por eventos, tarifa como entidad propia, factura del período con datos de medición, facturación mensual, pago con fecha, "mis facturas" y tests.

## Tiempos por paso

El tiempo registrado es el intervalo entre commits consecutivos. Incluye depuración.

| Paso | Tema | Estimado en el taller | Registrado | Commit | Hora del commit |
|---|---|---|---|---|---|
| 0 + 1 | Preparación y servidor gRPC de usuarios (incluye el ajuste de seguridad gRPC en medición) | 10 + 25 min | [completa: el inicio de la sesión no tiene registro] | `9257263` (medicion), `d3416dc` (usuarios) | 2-nov 15:40 y 15:45 |
| 2 | Validación de JWT en facturación | ~20 min | ~13 min | `4bbdfb6` | 2-nov 15:58 |
| 3 | Medidores locales por eventos | ~25 min | ~22 min | `b5c5328` | 2-nov 16:20 |
| 4 | Tarifa como entidad propia y ruta en el Gateway | ~25 min | ~32 min | `ac1c112`, `ce530a0` | 2-nov 16:51 |
| 5 | Factura del período con datos de medición | ~50 min | ~38 min | `5d126e7` | 2-nov 17:30 |
| 6 | Facturación mensual | ~20 min | ~15 min | `b365400` | 2-nov 17:45 |
| 7 | Pago con fecha, "mis facturas" y acceso por rol | ~25 min | ~23 min | `f7dbcbc` | 2-nov 18:08 |
| 8 | Tests | ~20 min | ~15 min | `7f33679` | 2-nov 18:22 |
| 9 | Verificación de punta a punta | ~30 min | ~1 h 40 min (aprox., hasta las 20:03; incluye el reinicio de medición) | — | — |
| 10 | Notas, bitácora  | ~10 min | 20 min | — | — |

## Commits de la parte 2

| Hash | Mensaje |
|---|---|
| `9257263` | fix(medicion): permitir las llamadas gRPC entre servicios sin autenticacion |
| `d3416dc` | feat(usuarios): servidor gRPC ExisteUsuarioActivo en el puerto 9091 |
| `4bbdfb6` | feat(facturacion): validacion de JWT con el secreto compartido de usuarios |
| `b5c5328` | feat(facturacion): copia local de medidores a partir de los eventos de medicion |
| `ac1c112` | feat(facturacion): tarifa como entidad propia con vigencias sin superposicion |
| `ce530a0` | feat(gateway): ruta /api/tarifas hacia facturacion |
| `5d126e7` | feat(facturacion): factura del periodo con consumo, periodo completo y anomalias desde medicion |
| `b365400` | feat(facturacion): facturacion mensual programada para los medidores activos |
| `f7dbcbc` | feat(facturacion): pago con fecha, mis facturas y acceso por rol o propietario |
| `7f33679` | test(facturacion): factura del periodo, tarifas sin superposicion y fecha de pago |


## Evidencias

Las capturas están en `evidencias/dev-2/comunicacion-microservicios/capturas/`.

| Archivo | Qué muestra |
|---|---|
| `f1-grpcurl-usuarios.png` | `grpcurl list` y `ExisteUsuarioActivo` |
| `f2-jwt-401-200.png` | Facturación con 401 sin token y 200 con token |
| `f3-medidores-locales.png` | Log del evento y tabla `medidores_locales` |
| `f4-tarifas.png` | Tarifa 201, superposición 409 y usuario normal 403 |
| `f5-compilado.png` | Compilación del Paso 5 |
| `f7-arranque.png` | Arranque con el controlador final |
| `f8-tests.png` | Resultado de `mvnw test` |
| `f9-punta-a-punta.png` | Factura con anomalías, 409, 403 y pago |

## Estado final

| Pieza | Estado |
|---|---|
| Servidor gRPC de usuarios | Hecho |
| JWT en facturación | Hecho |
| Medidores locales por eventos | Hecho |
| Tarifa como entidad propia | Hecho |
| Factura con consumo, período completo y anomalías desde medición | Hecho |
| Facturación mensual programada y manual | Hecho |
| Pago con fecha, "mis facturas" y acceso por rol | Hecho |
| Tests | Hecho |
| Verificación de punta a punta (Paso 9) | Hecho el 2-nov, tras reiniciar medicion-service |