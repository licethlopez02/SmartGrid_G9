# Matriz de decisión ponderada

## Decisión

Para el alcance de Smart Grid se compararon tres alternativas para organizar el
backend:

1. **Monolito modular:** un despliegue y una base, con módulos separados por
   dominio.
2. **Tres microservicios con comunicación síncrona:** usuarios, medición y
   facturación separados, con llamadas HTTP/gRPC.
3. **Tres microservicios con eventos:** la misma separación, más Kafka y
   Transactional Outbox para propagar hechos de negocio.

La alternativa elegida fue la tercera. No es la de menor costo operativo, pero
es la que mejor satisface el objetivo de la etapa: comunicación entre
microservicios, alertas desacopladas y bases de datos propiedad de cada
servicio.

## Criterios y pesos

Los pesos reflejan los atributos de calidad priorizados en
`ARQUITECTURA.md`: confiabilidad de facturación y oportunidad de alertas son
los más importantes. La escala es de 1 (muy desfavorable) a 5 (muy favorable).

| Criterio | Peso |
|---|---:|
| Confiabilidad de facturación | 25 |
| Oportunidad y desacoplamiento de alertas | 20 |
| Testabilidad y separación de dominios | 15 |
| Seguridad y control de acceso | 15 |
| Rendimiento sostenido de lecturas | 10 |
| Costo operativo para un equipo de dos personas | 15 |
| **Total** | **100** |

## Evaluación

| Alternativa | Facturación (25) | Alertas (20) | Testabilidad (15) | Seguridad (15) | Rendimiento (10) | Operación (15) | **Puntaje / 5** |
|---|---:|---:|---:|---:|---:|---:|---:|
| Monolito modular | 5 | 2 | 4 | 3 | 3 | 5 | **3,75** |
| Microservicios síncronos | 4 | 3 | 4 | 4 | 4 | 2 | **3,55** |
| Microservicios + eventos (elegida) | 4 | 5 | 4 | 4 | 4 | 2 | **3,95** |

El puntaje se calcula como `Σ (peso × calificación) / 100`. La diferencia
entre las dos últimas alternativas está principalmente en las alertas: con
Outbox y Kafka, medición no espera a SMTP ni conoce los datos de contacto de
usuarios. El costo es mayor complejidad operativa y consistencia eventual.

## Sensibilidad y decisión final

Si el costo operativo sube de 15 a 25 puntos, el monolito modular pasa a ser
la alternativa preferida. Esto es razonable para un producto pequeño sin el
requisito académico de comunicación entre servicios. Para este proyecto, el
objetivo de la etapa y la necesidad de no perder eventos justifican conservar
la alternativa elegida.

La decisión queda condicionada a estas mitigaciones antes de producción:

- reintentos e idempotencia para los consumidores de alertas;
- autenticación de los canales gRPC y rotación de secretos;
- consultas acotadas por período o agregaciones en base de datos si crece el
  volumen de lecturas.
