# Coleccion Bruno - SmartGrid_G9

## Cobertura disponible

La API HTTP actual expone Facturacion en `/api/facturas`. La coleccion prueba:

- Generar una factura y verificar el total (`consumo x tarifa`).
- Consultar una factura por id.
- Listar facturas por medidor y validar un listado vacio.
- Rechazar periodos duplicados e incompletos.
- Rechazar tarifas vencidas o invalidas.
- Manejar una consulta de factura inexistente.

## Ejecutar para la sustentacion

1. Inicia Docker Desktop.
2. Desde la raiz del proyecto, ejecuta `docker compose up -d`.
3. Inicia Spring Boot con `mvn spring-boot:run`.
4. Abre `bruno/SmartGrid_G9` en Bruno y selecciona el entorno `Local`.
5. Ejecuta primero `01 - Generar factura` y despues la coleccion completa.
6. Si la generacion devuelve conflicto porque ya existe una factura con esos datos, cambia `medidorId` en `environments/Local.bru` a otro numero que no se haya usado y repite.

La tarifa de prueba es 650 y el consumo es 120.5 kWh, por lo que el total esperado es 78325.

## Limite de integracion entre subdominios

La coleccion cubre todos los endpoints REST existentes, pero no puede probar una
comunicacion HTTP entre Medicion y Facturacion: Medicion no tiene controlador
REST y Facturacion recibe `consumoAcumuladoKwh` directamente en la solicitud.
Ademas, el identificador de medidor es `String` en Medicion y `Long` en
Facturacion. Por tanto, actualmente no hay una integracion ejecutable entre
ambos subdominios; para probarla hace falta definir e implementar ese contrato.
