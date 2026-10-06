package co.edu.uptc.smart_grid.medicion.aplicacion.puerto.entrada;

import co.edu.uptc.smart_grid.medicion.dominio.Medidor;

public interface RegistrarMedidorUseCase {

	Medidor registrar(Long id, String ubicacion);
}
