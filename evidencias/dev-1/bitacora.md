## Bitacora

| Pregunta | Tu respuesta |
|---|---|
| ¿Cuál es la raíz del Agregado Medición? | `Medidor` — es el objeto que identifica al medidor y controla el registro de sus lecturas. |
| ¿Qué vive dentro del límite? | `id`, `ubicacion` y la colección de `LecturaConsumo` perteneciente al medidor. El agregado expone copias inmutables del historial y controla cómo se agregan nuevas lecturas. |
| ¿Qué representa `LecturaConsumo`? | Es un objeto de dominio inmutable con `valor`, `timestamp` y `medidorId`. Valida que el consumo no sea negativo, que exista el medidor y que el identificador no esté vacío. También puede indicar si una lectura presenta una variación anómala frente a un valor de referencia. |
| ¿Qué reglas protege `Medidor`? | No permite identificadores ni ubicaciones vacías, no acepta lecturas nulas o de otro medidor y exige que las lecturas se registren en orden cronológico. |
| ¿Dónde se detectan las anomalías que comparan varias lecturas? | En `DeteccionAnomaliaService`, un servicio de dominio que calcula el promedio histórico y compara la nueva lectura con un umbral de variación. Devuelve un `ResultadoDeteccion` con la decisión y una razón explícita. |
| ¿Por qué `ResultadoDeteccion` es separado de `Medidor`? | Porque representa el resultado de una operación de análisis, no un dato que deba almacenarse dentro del agregado. Además, obliga a explicar tanto si existe una anomalía como el motivo de la decisión. |
| ¿Cómo se crea un `Medidor` válido? | Mediante `MedidorFactory`, que ofrece una creación normal y otra con historial. La variante con historial reutiliza `registrarLectura`, por lo que conserva todas las reglas del agregado. |
| ¿Qué pasaría si se modificara directamente la lista de lecturas? | Se podrían introducir lecturas de otro medidor, valores inválidos o registros fuera de orden. Por eso `Medidor` mantiene la lista privada y solo expone copias no modificables. |

## Evidencias dev-1

| Paso | Evidencia en el código | Fecha |
|---|---|---|
| 1 · Lenguaje ubicuo de Medición | `Medidor`, `LecturaConsumo`, `historialLecturas`, `registrarLectura` y `DeteccionAnomaliaService` expresan los conceptos del dominio. | 22-09-2026 |
| 2 · Agregado `Medidor` | `Medidor` funciona como raíz y protege la identidad, ubicación e historial de lecturas. | 22-09-2026 |
| 3 · Reglas de las lecturas | `LecturaConsumo` valida valores no negativos, identificador y timestamp; `Medidor` valida pertenencia y orden cronológico. | 22-09-2026 |
| 4 · Servicio de dominio | `DeteccionAnomaliaService` compara una nueva lectura con el promedio histórico y genera `ResultadoDeteccion`. | 22-09-2026 |
| 5 · Factory (`MedidorFactory`) | Centraliza la creación del agregado y la carga inicial del historial sin saltarse sus invariantes. | 22-09-2026 |
| 6 · Commit de la funcionalidad | La funcionalidad de medición aparece registrada en el commit `036ad56` (`feat: add medicion domain and tests`). | 22-09-2026 |

> Los tiempos estimados y reales no están registrados en los archivos disponibles del proyecto.