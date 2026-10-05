package co.edu.uptc.smart_grid.medicion;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public record LecturaConsumo(
		BigDecimal valor,
		Instant timestamp,
		String medidorId) {

	private static final BigDecimal CAMBIO_ANOMALO = new BigDecimal("0.50");

	public LecturaConsumo {
		Objects.requireNonNull(valor, "El valor del consumo es obligatorio");
		Objects.requireNonNull(timestamp, "El timestamp de la lectura es obligatorio");
		Objects.requireNonNull(medidorId, "El id del medidor es obligatorio");
		if (medidorId.isBlank()) {
			throw new IllegalArgumentException("El id del medidor no puede estar vacío");
		}
		if (valor.signum() < 0) {
			throw new IllegalArgumentException("El consumo no puede ser negativo");
		}
	}

	public boolean sugiereSensorDefectuoso(BigDecimal valorReferencia) {
		Objects.requireNonNull(valorReferencia, "El valor de referencia es obligatorio");
		if (valorReferencia.signum() < 0) {
			throw new IllegalArgumentException("El valor de referencia no puede ser negativo");
		}
		if (valor.signum() == 0) {
			return true;
		}
		if (valorReferencia.signum() == 0) {
			return false;
		}

		BigDecimal variacionRelativa = valor.subtract(valorReferencia)
				.abs()
				.divide(valorReferencia, 8, java.math.RoundingMode.HALF_UP);
		return variacionRelativa.compareTo(CAMBIO_ANOMALO) > 0;
	}
}
