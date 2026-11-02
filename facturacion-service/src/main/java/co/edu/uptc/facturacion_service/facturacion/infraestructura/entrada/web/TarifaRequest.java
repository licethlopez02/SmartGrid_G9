package co.edu.uptc.facturacion_service.facturacion.infraestructura.entrada.web;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TarifaRequest(BigDecimal valor, LocalDate vigenciaDesde, LocalDate vigenciaHasta) {}