package co.edu.uptc.facturacion_service.facturacion.infraestructura.entrada.web;

import co.edu.uptc.facturacion_service.facturacion.dominio.EstadoFactura;
import java.math.BigDecimal;
import java.time.LocalDate;

public record FacturaResponse(
    Long id, Long medidorId, LocalDate periodoInicio, LocalDate periodoFin,
    BigDecimal montoTotal, EstadoFactura estado,
    BigDecimal consumoAcumuladoKwh, BigDecimal tarifaValor,
    LocalDate tarifaVigenciaDesde, LocalDate tarifaVigenciaHasta,
    LocalDate fechaGeneracion, LocalDate fechaVencimiento
) {

}