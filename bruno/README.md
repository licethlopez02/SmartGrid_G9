# Coleccion Bruno - SmartGrid_G9

## Cobertura disponible

La coleccion incluye requests para todos los endpoints REST actuales:

- **Usuarios:** registro, login, perfil, cambio de contrasena, listado, consulta
  por id, roles y activacion/desactivacion.
- **Medicion:** registro y eliminacion de medidores, medidores propios,
  lecturas, consumo, anomalias (consulta, pendientes y resolucion) y consumo
  por zona.
- **Facturacion:** generacion mensual y por periodo, consultas por id/medidor,
  mis facturas, pago, tarifas y validaciones de errores.

Los requests que necesitan autenticacion usan los tokens guardados por los
requests de login en el entorno seleccionado. Los casos con datos que pueden
chocar con ejecuciones anteriores usan las variables del entorno `Local`.

## Ejecutar para la sustentacion

1. Inicia Docker Desktop.
2. Desde la raiz del proyecto, ejecuta `docker compose up -d`.
3. Inicia Spring Boot con `mvn spring-boot:run`.
4. Abre la carpeta `bruno` en Bruno y selecciona el entorno `Local`.
5. Ejecuta primero los requests de **Usuarios** para obtener `tokenUsuario` y
   `tokenOperador`.
6. Ejecuta los requests de **Medicion** y luego los de **Facturacion**.
7. Si la generacion devuelve conflicto porque ya existe una factura con esos
   datos, cambia `medidorId` en `environments/Local.bru` a otro numero que no
   se haya usado y repite.

La tarifa de prueba es 650 y el consumo es 120.5 kWh, por lo que el total esperado es 78325.

## Limite de integracion entre subdominios

La coleccion cubre los endpoints REST, pero no puede probar una comunicacion
HTTP entre Medicion y Facturacion: Facturacion obtiene el consumo mediante su
puerto de aplicacion y no expone ese flujo como una solicitud REST. Para probar
esa integracion hace falta definir y documentar el contrato entre ambos
subdominios.
