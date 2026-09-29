package co.edu.uptc.smart_grid.facturacion.infraestructura.entrada.web;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FacturaRequest(
    Long medidorId, LocalDate periodoInicio, LocalDate periodoFin,
    BigDecimal consumoAcumuladoKwh, boolean periodoCompleto,
    BigDecimal tarifaValor, LocalDate tarifaVigenciaDesde, LocalDate tarifaVigenciaHasta
) {}