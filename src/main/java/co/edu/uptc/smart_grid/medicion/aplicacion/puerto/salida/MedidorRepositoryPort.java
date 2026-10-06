package co.edu.uptc.smart_grid.medicion.aplicacion.puerto.salida;

import java.util.Optional;
import java.util.List;

import co.edu.uptc.smart_grid.medicion.dominio.Medidor;

public interface MedidorRepositoryPort {

	Optional<Medidor> buscarPorId(Long medidorId);

	default List<Medidor> listar() {
		return List.of();
	}

	Medidor guardar(Medidor medidor);
}
