package co.edu.uptc.medicion_service.medicion.aplicacion.puerto.salida;

import java.util.Optional;
import java.util.List;

import co.edu.uptc.medicion_service.medicion.dominio.Medidor;

public interface MedidorRepositoryPort {

	Optional<Medidor> buscarPorId(Long medidorId);

	default List<Medidor> listar() {
		return List.of();
	}

	Medidor guardar(Medidor medidor);
}
