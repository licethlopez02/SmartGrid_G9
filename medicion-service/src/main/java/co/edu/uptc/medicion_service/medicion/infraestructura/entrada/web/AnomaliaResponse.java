package co.edu.uptc.medicion_service.medicion.infraestructura.entrada.web;

import java.math.BigDecimal;
import java.time.Instant;

public record AnomaliaResponse(Long id, BigDecimal valor, Instant timestamp, String razon,
		boolean resuelta) {

	public AnomaliaResponse(BigDecimal valor, Instant timestamp, String razon) {
		this(null, valor, timestamp, razon, false);
	}
}
