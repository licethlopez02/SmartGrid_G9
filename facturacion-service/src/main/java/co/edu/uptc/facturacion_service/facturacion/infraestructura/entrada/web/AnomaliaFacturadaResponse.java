package co.edu.uptc.facturacion_service.facturacion.infraestructura.entrada.web;

import java.math.BigDecimal;
import java.time.Instant;

public record AnomaliaFacturadaResponse(Long anomaliaId, BigDecimal valor, Instant momento, String razon) {
    
}