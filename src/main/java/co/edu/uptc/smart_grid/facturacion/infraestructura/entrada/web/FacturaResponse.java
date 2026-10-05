package co.edu.uptc.smart_grid.facturacion.infraestructura.entrada.web;

import co.edu.uptc.smart_grid.facturacion.dominio.EstadoFactura;
import java.math.BigDecimal;
import java.time.LocalDate;

public record FacturaResponse(
    Long id, Long medidorId, LocalDate periodoInicio, LocalDate periodoFin,
    BigDecimal montoTotal, EstadoFactura estado
) {}