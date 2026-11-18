# Retrospectiva individual - Liceth López

## Autoevaluación (1 = no lo logro, 4 = lo hago con autonomía)

RA1 Explico principios, patrones y estilos arquitectónicos .......... 3
Evidencia: la tabla de estilos por componente y los ADR de `ARQUITECTURA.md` (secciones 2 y 4): gateway, microservicios con puertos y adaptadores, gRPC para consultas y Outbox con Kafka para eventos.

RA2 Analizo ventajas, limitaciones y contextos de los estilos ........ 3
Evidencia: `docs/cierre/ATAM-LITE.md` (escenarios, riesgos, sensibilidades y compromisos), `docs/cierre/MATRIZ.md` (revisada con Dev 1) y la sección 6 de `ARQUITECTURA.md` (por qué no service mesh ni data mesh).

RA3 Diseño arquitecturas con modelos, diagramas y estándares ........ 3
Evidencia: el diagrama de contenedores C4 y `ARQUITECTURA.md` (Lección 12), donde cada afirmación se contrastó con el código.

RA5 Implemento arquitecturas modernas (microservicios, CQRS, DDD, hexagonal) ... 3
Evidencia: facturacion-service con DDD (commit `68b763c`: value object, servicio de dominio, límite del agregado y factory) y arquitectura hexagonal (commits `18c66b9`, `e2d8826`, `d0f979b`, `c023d56`, `7caf85f` y `9127984`); usuarios-service con JWT y servidor gRPC (Pull Request #8, `41fdc5b`); comunicación entre los tres servicios con gRPC, Eureka, gateway y Kafka con Outbox. No implementé CQRS: el ADR-004 quedó como propuesta.

## Tres aprendizajes que me llevo

1. Un diseño solo está probado cuando se ejecuta de punta a punta. Al validar las alertas por correo aparecieron fallos que ningún documento mostraba: un error de sintaxis en un `application.yaml` que impedía arrancar usuarios-service, el orden de arranque de los servicios y que la base de datos de medición no se levanta sola.
2. El Outbox evita perder eventos, pero no garantiza que el correo llegue. Cuando el envío falló por credenciales inválidas, el servicio no lo reintentó (lee solo mensajes nuevos y no reintenta), y la alerta solo se pudo comprobar enviando lecturas nuevas. Por eso es el riesgo número uno del ATAM.
3. La mejor arquitectura depende del contexto. En la matriz de decisión, para un equipo de dos personas, el monolito modular puntuó más alto que nuestros tres servicios. Los mantuvimos por el objetivo del curso y por la separación de dominios, y asumimos el costo de operar más piezas.

## Una decisión de arquitectura que tomaría distinto hoy, y por qué

Las alertas por correo. Hoy el consumidor de usuarios-service lee solo los mensajes nuevos y no reintenta, así que una alerta se pierde si el servicio estaba apagado o si falla el envío. Hoy diseñaría desde el principio un control por identificador de evento y reintentos, para poder recuperar esas alertas sin generar lecturas nuevas.

## Lo que me falta por aprender y cómo lo haría

- Docker y sistemas distribuidos: me falta soltura para encontrar por mi cuenta dónde falla una cadena entre servicios (Kafka, Eureka, bases de datos). Lo haría repitiendo la prueba de extremo a extremo y leyendo el log de cada servicio hasta ubicar el fallo sin ayuda.
- Resiliencia y seguridad entre servicios: aplicar Circuit Breaker, idempotencia en los consumidores y autenticación en gRPC, que quedaron como riesgos y deuda técnica.
- Automatizar los guardarraíles de arquitectura con una prueba ArchUnit en un CI, que hoy se verifican a mano.

## Mi aporte al proyecto de grupo (con evidencia de commits)

En el historial de Git de `SmartGrid_G9`, `.mailmap` unifica mis nombres de autor bajo `Liceth López` (141 commits) y los de Dev 1 bajo `Yaider` (28 commits). Los nombres anteriores eran variantes de identidad, no integrantes adicionales. En el repositorio `Smart-Grid-Frontend` todos los commits son míos: 22 como `Liceth Lopez` y 5 como `licethlopez02`. Para listar los míos por carpeta: `git log --oneline --author="Liceth López" -- <carpeta>`.

- **Inicio del proyecto y documentos de equipo:** `8c53494` (inicialización con Spring Boot) y `dcae586` (`NOTAS-G9.md`).
- **facturacion-service:** dominio con DDD y puertos y adaptadores (`Factura`, `TarifaVigente`, `GeneracionFacturaService`), con los commits `68b763c`, `18c66b9`, `e2d8826`, `d0f979b`, `c023d56`, `7caf85f` y `9127984`. Además, tarifa propia, facturación mensual programada, copia local de medidores por eventos y tests: `git log --oneline --author="Liceth López" -- facturacion-service`.
- **usuarios-service:** autenticación y autorización con JWT, roles, operador inicial y servidor gRPC `ExisteUsuarioActivo`: Pull Request #8 (`41fdc5b`) y `git log --oneline --author="Liceth López" -- usuarios-service`.
- **Integración de Pull Requests:** commits de merge de los Pull Requests #5, #6, #8 y #9 (`1d9ed73`, `87f27d4`, `41fdc5b` y `18d978e`).
- **Frontend (27 commits):** integración con el gateway y script simulador del medidor, en el repositorio `Smart-Grid-Frontend`.
- **Documentación y verificación (5 commits con el nombre `Liceth Lopez` en `docs`, `ARQUITECTURA.md` y `README.md`):** `ARQUITECTURA.md` (Lección 12), README de arranque y prueba completa, validación de extremo a extremo del 2026-11-15 y los documentos de `docs/cierre/` que lideré (ATAM-lite, gobernanza y resumen ejecutivo): `git log --oneline --author="Liceth Lopez" -- docs ARQUITECTURA.md README.md`.