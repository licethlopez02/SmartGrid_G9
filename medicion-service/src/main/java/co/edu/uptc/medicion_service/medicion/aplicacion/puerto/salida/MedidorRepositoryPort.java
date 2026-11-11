package co.edu.uptc.medicion_service.medicion.aplicacion.puerto.salida;

import java.util.Optional;
import java.util.List;

import co.edu.uptc.medicion_service.medicion.dominio.Medidor;
import co.edu.uptc.medicion_service.medicion.dominio.LecturaConsumo;

public interface MedidorRepositoryPort {

	Optional<Medidor> buscarPorId(Long medidorId);

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
