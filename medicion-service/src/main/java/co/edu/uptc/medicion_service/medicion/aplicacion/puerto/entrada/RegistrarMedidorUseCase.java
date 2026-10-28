package co.edu.uptc.medicion_service.medicion.aplicacion.puerto.entrada;

import co.edu.uptc.medicion_service.medicion.dominio.Medidor;

public interface RegistrarMedidorUseCase {

	Medidor registrar(Long id, String ubicacion);
}
