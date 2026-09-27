# Implementación arquitectura hexagonal subdominio facturación
| Pregunta | Respuesta |
| :--- | :--- |
| `FacturaRepository extends JpaRepository` ¿puerto primario o secundario? | Secundario: quien inicia la llamada es el núcleo (`FacturaFactory`), no algo externo. |
| No existe hoy un adaptador primario para Facturación (no hay controlador). Según la guía (sección 6), ¿qué falta? | Falta un puerto primario explícito (`FacturaUseCase`) y un adaptador que lo consuma desde HTTP. |
| `GeneracionFacturaService` y `FacturaFactory` son clases concretas sin interfaz ¿núcleo o adaptador? | Núcleo: no importan nada de infraestructura salvo las anotaciones de Spring (`@Service` / `@Component`), que la guía tolera. |
| `TarifaVigente` y `Factura` ¿a qué paquete hexagonal pertenecen? | Al núcleo, subpaquete `dominio` son Entidad y Value Object puros. |