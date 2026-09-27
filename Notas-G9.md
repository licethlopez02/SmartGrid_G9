# NOTAS— G9 Monitoreo energético / medidores inteligentes (smart grid)

## 1. Lista de eventos de dominio (orden cronológico)

1. **Medidor registrado y asociado a un usuario** un operador de red da de alta el medidor y lo vincula a un usuario o predio (HU-01).
2. **Lectura de consumo recibida** el medidor inteligente envía continuamente sus lecturas (HU-02).
3. **Lectura negativa descartada** una lectura con valor negativo se descarta (regla de negocio, sección 3).
4. **Alerta de sensor defectuoso generada** consecuencia directa del descarte anterior (regla de negocio, sección 3).
5. **Consumo acumulado calculado** el sistema calcula el consumo acumulado por período (HU-03).
6. **Anomalía de consumo detectada** se marca cuando el consumo supera un umbral relativo al histórico del mismo medidor, cerca del momento en que ocurre (HU-04).
7. **Alerta de anomalía notificada al usuario** el usuario recibe la alerta para revisar su instalación (HU-05).
8. **Factura generada** se genera periódicamente según el consumo acumulado y la tarifa vigente, solo si el período de consumo está completo; una anomalía no resuelta se incluye en el reporte de facturación del período (HU-06, reglas de negocio).

*(Las consultas de historial de consumo, estado de facturas y consumo agregado por zona (HU-07, HU-08, HU-09) son lecturas/consultas, no producen eventos de dominio nuevos.)*

## 2. Eventos pivote

- **Anomalía de consumo detectada**: no lo puede resolver un solo objeto, necesita la lectura actual **y** el histórico del medidor para compararlos (HU-04). Además cambia el responsable: de "recepción/cálculo de consumo" pasa a "alerta al usuario".
- **Factura generada**: necesita información de varios orígenes, el consumo acumulado del período **y** la tarifa vigente (HU-06), además, cambia el responsable: de "medición" pasa a "cobro/facturación".

## 3. Bounded Contexts candidatos

- **Medición y anomalías**: se origina en el evento pivote *Anomalía de consumo detectada*. Agrupa el registro del medidor, la recepción/validación de lecturas (incluida la regla de descarte de negativos) y el cálculo del consumo acumulado, porque todo ese flujo comparte el mismo Lenguaje Ubicuo alrededor del Medidor y su histórico.
- **Facturación**: se origina en el evento pivote *Factura generada*. Agrupa la generación periódica de la factura a partir del consumo acumulado y la tarifa vigente, sin necesitar saber cómo se detectan las anomalías internamente (solo si hay una anomalía no resuelta, para incluirla en el reporte).

## 4. Asignación de subdominios

| Integrante | Subdominio candidato | Raíz del Agregado | Value Object candidato | Servicio de Dominio candidato |
|---|---|---|---|---|
| Dev 1 - Yaider Becerra | Medición y anomalías | Medidor | `LecturaConsumo` — descarta negativos, marca alerta de sensor defectuoso | `DeteccionAnomaliaService` — compara el consumo contra el histórico del medidor |
| Dev 2 - Liceth López| Facturación | Factura | `TarifaVigente` — positiva, con vigencia | `GeneracionFacturaService` — solo genera si el período de consumo está completo |

## 5. Enlace al repositorio de GitHub
https://github.com/licethlopez02/SmartGrid_G9.git