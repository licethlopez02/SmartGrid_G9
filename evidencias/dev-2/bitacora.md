## Bitacora

| Pregunta | Tu respuesta |
|---|---|
| ¿Cuál es la raíz del Agregado Facturación? | `Factura` — es la única clase con `@Entity` en el paquete `facturacion`. |
| ¿Qué vive dentro del límite? | `id`, `medidorId` (referencia por id, nunca el objeto `Medidor` completo), `periodoInicio`, `periodoFin`, `consumoAcumuladoKwh`, `tarifaAplicada` (Value Object `TarifaVigente`), `montoTotal`, `estado`, `fechaGeneracion`. |
| ¿Por qué `Medidor`/`Lectura` NO están dentro de este límite? | Pertenecen al Agregado `Medidor`, subdominio de Dev 1 (Medición y anomalías). Meterlos aquí violaría "Agregados pequeños" y acoplaría dos subdominios que deben poder desplegarse de forma independiente — por eso `medidorId` es un `Long`. |
| ¿Qué pasaría si alguien agrega `List<Lectura> lecturas` directo en `Factura`? | Rompería el límite: un cambio en las lecturas de Medición forzaría recargar y bloquear el Agregado `Factura`; dos transacciones podrían pisarse datos de otro subdominio; y Medición dejaría de poder evolucionar sola. |

## evidencias dev-2

| Paso | Tiempo estimado | Tiempo real | Fecha |
|---|---|---|---|
| 1 · Lenguaje Ubicuo | ~20 min | 15 min | 16-09-2026 |
| 2 · Value Object (`TarifaVigente`) | ~45 min | 50 min | 16-09-2026 |
| 3 · Servicio de Dominio (`GeneracionFacturaService`) | ~35 min | 30 min | 20-09-2026 |
| 4 · Límite del Agregado | ~15 min | 15 min | 20-09-2026 |
| 5 · Factory (`FacturaFactory`) | ~35 min | 40 min | 20-09-2026 |
| 6 · Commit y push | ~5 min | 5 min | 20-09-2026 |