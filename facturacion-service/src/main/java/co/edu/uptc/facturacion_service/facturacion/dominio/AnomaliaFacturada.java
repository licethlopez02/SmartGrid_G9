package co.edu.uptc.facturacion_service.facturacion.dominio;

import jakarta.persistence.Embeddable;

import java.math.BigDecimal;
import java.time.Instant;

@Embeddable
public record AnomaliaFacturada(Long anomaliaId, BigDecimal valor, Instant momento, String razon) {}