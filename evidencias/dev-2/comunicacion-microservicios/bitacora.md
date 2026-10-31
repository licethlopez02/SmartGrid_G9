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