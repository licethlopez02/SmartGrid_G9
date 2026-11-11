package co.edu.uptc.medicion_service.medicion.dominio;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * Measurement reported by a meter at a point in time.
 */
public record LecturaConsumo(
		BigDecimal valor,
		Instant timestamp,
		Long medidorId) {

	public LecturaConsumo {
		Objects.requireNonNull(valor, "El valor del consumo es obligatorio");
		Objects.requireNonNull(timestamp, "El timestamp de la lectura es obligatorio");
		Objects.requireNonNull(medidorId, "El id del medidor es obligatorio");
		if (valor.signum() < 0) {
			throw new IllegalArgumentException("El consumo no puede ser negativo");
		}
	}

}
