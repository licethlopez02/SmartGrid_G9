package co.edu.uptc.smart_grid.medicion;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Domain service containing rules that compare more than one reading.
 */
public final class DeteccionAnomaliaService {

	private static final BigDecimal UMBRAL_POR_DEFECTO = new BigDecimal("0.50");
	private final BigDecimal umbralVariacion;

	public DeteccionAnomaliaService() {
		this(UMBRAL_POR_DEFECTO);
	}

	public DeteccionAnomaliaService(BigDecimal umbralVariacion) {
		Objects.requireNonNull(umbralVariacion, "El umbral es obligatorio");
		if (umbralVariacion.signum() < 0) {
			throw new IllegalArgumentException("El umbral no puede ser negativo");
		}
		this.umbralVariacion = umbralVariacion;
	}

	public ResultadoDeteccion detectar(Medidor medidor, LecturaConsumo nuevaLectura) {
		Objects.requireNonNull(medidor, "El medidor es obligatorio");
		Objects.requireNonNull(nuevaLectura, "La nueva lectura es obligatoria");
		if (!medidor.id().equals(nuevaLectura.medidorId())) {
			throw new IllegalArgumentException("La lectura pertenece a otro medidor");
		}

		var historial = medidor.historialLecturas();
		if (historial.isEmpty()) {
			return new ResultadoDeteccion(false, "No hay histórico suficiente para comparar");
		}

		BigDecimal promedio = historial.stream()
				.map(LecturaConsumo::valor)
				.reduce(BigDecimal.ZERO, BigDecimal::add)
				.divide(BigDecimal.valueOf(historial.size()), 8, RoundingMode.HALF_UP);

		if (promedio.signum() == 0) {
			boolean anomalia = nuevaLectura.valor().signum() > 0;
			return new ResultadoDeteccion(anomalia,
					anomalia ? "La lectura se desvía de un histórico en cero" : "La lectura coincide con el histórico");
		}

		BigDecimal variacion = nuevaLectura.valor().subtract(promedio)
				.abs()
				.divide(promedio, 8, RoundingMode.HALF_UP);
		boolean anomalia = variacion.compareTo(umbralVariacion) > 0;
		String razon = anomalia
				? "La lectura se desvía más del " + umbralVariacion.multiply(BigDecimal.valueOf(100)).stripTrailingZeros().toPlainString()
						+ "% del promedio histórico"
				: "La lectura está dentro del rango esperado";
		return new ResultadoDeteccion(anomalia, razon);
	}
}
