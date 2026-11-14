# Bitácora: asociación de medidores, anomalías y alertas

## Asociación con usuarios

El registro de un medidor acepta `usuarioId` y valida por gRPC que el usuario exista y esté activo. Si no se envía `propiedad`, se usa el valor de `ubicacion`. Un medidor mantiene un único `usuarioId`; no existe un endpoint para reasignarlo, por lo que no se agrega una restricción de unicidad por propiedad/estado. Esto permite registrar varios medidores en la misma ubicación y soporta HU-09 (consumo por zona).

La consulta `GET /api/medidores/mios` filtra los medidores por el `sub` del token autenticado, cubriendo HU-07. La eliminación de medidores permanece restringida al rol `OPERADOR`.

## Anomalías persistidas y resolución

Cuando una lectura supera el umbral de anomalía de consumo, Medición guarda una entidad en `anomalias` con su medidor, valor, fecha, razón y estado pendiente. Después publica el evento `anomalias.detectadas`; el payload incluye `destinatarioId` cuando el medidor tiene dueño, para que el consumidor de Usuarios envíe el correo al dueño. Si no hay dueño, el consumidor usa al operador.

La resolución se realiza mediante `POST /api/anomalias/{anomaliaId}/resolver` (operador) o la ruta anidada del medidor. Resolver cambia `resuelta` a `true` y persiste el cambio.

## Alertas de sensores defectuosos

La alerta `sensores.defectuosos` se publica únicamente para lecturas negativas, conforme a la regla 1. Las lecturas en cero o con una variación mayor al 50% no generan esta alerta. La lectura negativa no se guarda como consumo válido, pero el payload conserva el valor real recibido (incluido el signo), junto con `medidorId`, `usuarioId`/`destinatarioId` cuando corresponda, fecha y razón. El consumidor de Usuarios envía estas alertas al operador.

## Periodo completo

`periodoCompleto` devuelve verdadero si existe al menos una lectura, la primera cubre el inicio solicitado, la última cubre el fin solicitado y no hay separaciones superiores al límite configurado.

La separación máxima aceptable entre lecturas quedó configurada en 24 horas (`medicion.periodo.separacion-maxima=PT24H`). `periodoCompleto` valida también los bordes y cada separación consecutiva, por lo que un hueco mayor al límite impide generar la factura.

El consumo acumulado continúa calculándose bajo demanda a partir de las lecturas; no se persiste una entidad separada porque el requisito no exige una tabla propia.

Para registrar lecturas, la detección usa una consulta agregada de promedio y una carga del medidor sin historial; ya no reconstruye todas las lecturas en cada POST. También se eliminó la búsqueda duplicada de la rama de lecturas negativas. La regla R4 queda cubierta por la unicidad del identificador del medidor y por una prueba que rechaza asociar un segundo usuario al mismo medidor.

## Disponibilidad de Usuarios

La verificación gRPC de usuarios usa un plazo de 5 segundos. Si Usuarios está caído o vence el plazo, Medición responde `503 Service Unavailable` en lugar de convertir el fallo en un error 500.