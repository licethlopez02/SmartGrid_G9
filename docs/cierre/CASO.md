# Caso de éxito y límites de la solución

## Caso de éxito: una lectura anómala llega hasta la persona usuaria

El 15 de noviembre de 2026 se ejecutó el flujo de extremo a extremo con un
medidor registrado y una cuenta de usuario:

1. El medidor envió lecturas `1`, `1`, `2` y `-5` mediante el simulador.
2. Medición aceptó las lecturas válidas, calculó el consumo acumulado y detectó
   la lectura `2` como anomalía relativa al historial.
3. La anomalía se guardó junto con el evento en el outbox. El sondeo publicó
   el evento en Kafka.
4. Usuarios consumió `anomalias.detectadas`, resolvió el destinatario y envió
   la alerta por correo.
5. La lectura `-5` no se contabilizó como consumo y produjo la alerta de sensor
   defectuoso.
6. La anomalía quedó visible en la pantalla de alertas y pudo marcarse como
   resuelta.

El resultado esperado se observó en la aplicación: consumo válido de 4 kWh,
una anomalía pendiente con valor 2 antes de resolverla y dos tipos de alerta.
Este caso demuestra la separación entre consultas síncronas (HTTP/gRPC) y
hechos asíncronos (Outbox/Kafka), además de las reglas R1 y R3.

## Qué hizo posible el resultado

- Cada servicio conserva sus propios datos y contratos.
- El outbox evita la escritura dual entre la base de medición y Kafka.
- Usuarios-service encapsula el envío SMTP detrás de un puerto.
- La regla de período completo evita facturar con huecos de lecturas.
- Los endpoints están protegidos por JWT y roles en el gateway y los servicios.

## Límite observado: el correo no tiene recuperación

Durante la validación también se provocó un fallo de autenticación SMTP. La
anomalía continuó visible en la aplicación, pero el consumidor no reintentó el
envío; para repetir la evidencia fue necesario generar lecturas nuevas. Por
tanto, el caso es exitoso para la detección y persistencia de la anomalía, pero
no ofrece todavía una garantía de entrega del correo.

La solución actual entrega eventos al menos una vez y no implementa
deduplicación. Un reinicio o una reentrega puede producir un correo duplicado.
La mejora prioritaria es persistir el estado de entrega, reintentar con
backoff y usar el identificador del evento como clave idempotente.

## Conclusión

La arquitectura cumple el alcance académico y las historias HU-01 a HU-09
demostradas. Debe considerarse una solución de desarrollo, no un sistema listo
para producción, hasta cerrar la entrega confiable de alertas, la autenticación
gRPC y la gestión de secretos.
