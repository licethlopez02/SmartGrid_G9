package co.edu.uptc.medicion_service.medicion.aplicacion.puerto.salida;

import java.util.Optional;
import java.util.List;

import co.edu.uptc.medicion_service.medicion.dominio.Medidor;
import co.edu.uptc.medicion_service.medicion.dominio.LecturaConsumo;
import java.math.BigDecimal;

public interface MedidorRepositoryPort {

	Optional<Medidor> buscarPorId(Long medidorId);

	default Optional<Medidor> buscarPorIdSinHistorial(Long medidorId) {
		return buscarPorId(medidorId);
	}

	default Optional<BigDecimal> promedioLecturas(Long medidorId) {
		return buscarPorId(medidorId).flatMap(medidor -> {
			List<LecturaConsumo> lecturas = medidor.historialLecturas();
			if (lecturas.isEmpty()) {
				return Optional.empty();
			}
			return Optional.of(lecturas.stream().map(LecturaConsumo::valor)
					.reduce(BigDecimal.ZERO, BigDecimal::add)
					.divide(BigDecimal.valueOf(lecturas.size()), 8, java.math.RoundingMode.HALF_UP));
		});
	}

	default List<Medidor> listar() {
		return List.of();
	}

	Medidor guardar(Medidor medidor);

	/**
	 * Persists only the newly received reading. Implementations should not
	 * rewrite the meter's complete history for every sample.
	 */
	default void guardarLectura(LecturaConsumo lectura) {
		// In-memory adapters persist the aggregate in guardar(Medidor).
	}

	default void eliminar(Long medidorId) {
		throw new UnsupportedOperationException();
	}
}
