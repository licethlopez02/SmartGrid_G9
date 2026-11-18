# ATAM-lite: evaluación de la arquitectura de Smart Grid (Grupo 9)

Parte de los cinco atributos de calidad de la sección 1 de `ARQUITECTURA.md`. Los hechos salen de las secciones 2, 4 y 7 de ese documento, de la prueba de extremo a extremo del 2026-11-15 y de la revisión del código del 2026-11-17.

## 1. Árbol de utilidad

| # | Atributo | Escenario (estímulo, respuesta, medida) | Importancia | Dificultad |
|---|---|---|---|---|
| 1 | Confiabilidad de la facturación (RNF2, R2) | **Estímulo:** el día 1 a las 02:00 se factura el mes anterior y entre dos lecturas hay más de la separación máxima (`PT24H`). **Respuesta:** `periodoCompleto` devuelve `false`, se lanza `PeriodoNoCerradoException` y no se genera la factura. **Medida:** 0 facturas con período incompleto; cubierto por un test unitario. | A | M |
| 2 | Oportunidad de las alertas (RNF3, HU-04, HU-05) | **Estímulo:** llega una lectura con \|valor − promedio\| / promedio > 0,5. **Respuesta:** se guarda la anomalía y el evento `anomalias.detectadas` en la misma transacción, el outbox lo publica en Kafka y usuarios-service envía el correo. **Medida:** evento publicado en Kafka en un máximo de 2 s; en la prueba del 2026-11-15 el correo llegó al usuario (el tiempo exacto no se midió). | A | A |
| 3 | Rendimiento sostenido de lecturas (RNF1) | **Estímulo:** llegan lecturas de forma sostenida y el historial del medidor crece. **Respuesta:** el `POST` de lectura no carga el historial completo. **Medida:** el número de consultas por `POST` no crece con el historial; verificado por inspección del código (tres operaciones de lectura en la base, sin cargar la lista de lecturas); no se midió bajo carga. | M | M |
| 4 | Seguridad (control de acceso) | **Estímulo:** un usuario sin rol OPERADOR intenta eliminar un medidor o consultar la factura de otro usuario. **Respuesta:** el acceso se rechaza. **Medida:** `DELETE /api/medidores/{id}` solo lo acepta un OPERADOR; una factura solo la consulta un OPERADOR o el propietario del medidor. | M | M |
| 5 | Testabilidad | **Estímulo:** se cambia una regla de dominio. **Respuesta:** se prueba sin base de datos ni red. **Medida:** cada servicio tiene su suite; los `contextLoads` de facturacion y usuarios exigen Docker activo. | M | B |

Los escenarios 2 (A/A) y 1 (A/M) son los que se analizan a fondo.

## 2. Análisis del escenario 2: alertas oportunas (A/A)

| Tipo | Hallazgo | Decisión relacionada |
|---|---|---|
| No-riesgo | La anomalía y el evento se guardan en la misma transacción: si Kafka está caído, el evento no se pierde. | ADR-002 (Outbox) |
| No-riesgo | medicion no espera el envío del correo ni conoce los correos de los usuarios; un fallo de SMTP no la afecta. | ADR-005 |
| No-riesgo | La cadena completa se probó de extremo a extremo el 2026-11-15: lectura simulada, evento en Kafka, consumo en usuarios-service y correo recibido. | ADR-002, ADR-005 |
| Riesgo | Sin reintentos ni deduplicación: el listener captura el error de envío y da el mensaje por procesado, así que un fallo de SMTP no se reintenta y un evento reentregado puede producir un correo duplicado. En la prueba del 2026-11-15 el envío falló por credenciales inválidas (`Authentication failed`) y las alertas se verificaron enviando lecturas nuevas. La anomalía sigue visible en la pantalla Alertas. | ADR-005 |
| Riesgo | El comportamiento si usuarios-service está apagado depende del offset del grupo Kafka (`auto-offset-reset: latest` solo aplica si el grupo no tiene un offset confirmado); no se probó. | ADR-005 |
| Riesgo | usuarios-service depende de Kafka y de un proveedor de correo externo. | ADR-005 |
| Riesgo | El sensor defectuoso se notifica solo por correo; no se persiste. | ADR-005 |
| Sensibilidad | Intervalo del sondeo del outbox (2 s, lotes de hasta 50): fija la espera máxima antes de publicar. | ADR-002 |
| Sensibilidad | Umbral relativo de 0,5: define qué lecturas se consideran anomalía. | HU-04 |
| Compromiso | Outbox: no se pierden eventos, a cambio de hasta 2 s de espera y un proceso más que operar. | ADR-002 |
| Compromiso | Entrega al menos una vez: no se pierden alertas por reentrega, a cambio de posibles duplicados. | ADR-005 |

## 3. Análisis del escenario 1: confiabilidad de la facturación (A/M)

| Tipo | Hallazgo | Decisión relacionada |
|---|---|---|
| No-riesgo | La regla de período completo vive en el dominio y se prueba sin base de datos ni red. | Puertos y adaptadores |
| No-riesgo | La factura se genera sola el día 1 (cron `0 0 2 1 * *`, zona America/Bogota) para el mes anterior. | ADR-001 |
| No-riesgo | facturacion aplica un Circuit Breaker programático en sus dos clientes gRPC hacia medicion, así que no se queda esperando si medicion falla. | ADR-002 |
| Riesgo | Si medicion no responde, no se factura ese período: el Circuit Breaker protege a facturacion, pero no genera la factura. | ADR-002 |
| Riesgo | `periodoCompleto` y el consumo acumulado consultan el historial completo del medidor; con mucho volumen la facturación puede ser lenta. No se midió. | ADR-003 |
| Riesgo | La copia local de medidores en facturacion llega por eventos: un medidor recién registrado puede tardar unos segundos en aparecer (consistencia eventual). | ADR-003 |
| Riesgo | Sin una tarifa vigente no se puede generar ninguna factura; la tarifa la crea el operador. | ADR-003 |
| Sensibilidad | `medicion.periodo.separacion-maxima` (`PT24H`): muy baja, deja períodos sin facturar por huecos pequeños; muy alta, factura con datos incompletos. | R2 |
| Compromiso | Se prefiere no facturar a facturar con datos incompletos: más confiabilidad, menos disponibilidad de la factura. | RNF2 |

## 4. Los 3 mayores riesgos

| # | Riesgo | Mitigación | Decisión |
|---|---|---|---|
| 1 | Alertas por correo sin reintentos ni deduplicación | Si el proyecto continúa, agregar un control por identificador de evento y reintentos o una cola de errores. | Aceptar para esta entrega: la anomalía sigue visible en la pantalla Alertas. |
| 2 | gRPC sin autenticación y secreto JWT compartido entre servicios | Restringir los puertos gRPC a la red interna y agregar un interceptor que exija un token; rotar el secreto si se filtra. | Aceptar en el entorno local; mitigar antes de cualquier despliegue. |
| 3 | La facturación y el consumo consultan el historial completo del medidor | Acotar las consultas del período o calcular el total en la base de datos; medir antes de optimizar. | Aceptar mientras no haya volumen; revisar si el proyecto continúa. |