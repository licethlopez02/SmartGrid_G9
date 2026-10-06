package co.edu.uptc.smart_grid.medicion.aplicacion.puerto.salida;

import java.util.Optional;

import co.edu.uptc.smart_grid.medicion.dominio.Medidor;

public interface MedidorRepositoryPort {

	Optional<Medidor> buscarPorId(Long medidorId);

	Medidor guardar(Medidor medidor);
}
