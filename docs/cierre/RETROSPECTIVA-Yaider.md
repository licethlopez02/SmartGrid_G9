# Retrospectiva individual - Yaider (Dev 1)

## Qué salió bien

- La separación de medición, usuarios y facturación permitió trabajar por
  dominio y probar las reglas de medición sin depender de la base de datos.
- El flujo completo de lecturas, anomalías y alertas quedó verificable con
  Docker, Kafka, gRPC, Eureka y el gateway.
- La documentación de arquitectura y las evidencias ayudaron a encontrar
  problemas de integración que no aparecían en los tests unitarios.

## Qué no salió bien

- La puesta en marcha requiere varias terminales, servicios y bases, lo que
  aumenta la posibilidad de errores de configuración.
- Un fallo de SMTP no se reintenta y obliga a generar una lectura nueva para
  volver a comprobar el correo.
- La comunicación gRPC entre servicios todavía no tiene autenticación propia.

## Tres aprendizajes

1. Una regla de dominio es más confiable cuando tiene una prueba aislada y
   también una evidencia de extremo a extremo.
2. El patrón Outbox evita perder el evento entre la base y Kafka, pero no
   garantiza que todos los consumidores completen su trabajo.
3. Separar servicios mejora los límites de los dominios, pero introduce
   consistencia eventual, observabilidad y operación adicional.

## Qué cambiaría

Diseñaría desde el inicio la entrega de alertas como un flujo idempotente:
cada evento tendría un identificador único, estado de entrega y reintentos con
backoff. Así se podría recuperar un fallo SMTP sin pedir al medidor que
reenvíe la lectura y sin duplicar correos.

## Mi aporte

Mi trabajo se concentró en `medicion-service`: asociación de medidores con
usuarios, recepción de lecturas, detección y resolución de anomalías, consumo
por período, validación de períodos completos y comunicación con usuarios por
gRPC. También participé en la integración de eventos con Kafka, las pruebas de
la API en Bruno, la validación de extremo a extremo y la documentación de
evidencias.

Como evidencia quedan `evidencias/dev-1/bitacora.md`, las capturas de
`evidencias/dev-1/` y los commits de medición e integración en el historial del
repositorio.

## Próximo paso técnico

Priorizaría reintentos e idempotencia de alertas; después añadiría
autenticación gRPC y mediría el rendimiento de las consultas de consumo antes
de introducir CQRS u otra infraestructura.
