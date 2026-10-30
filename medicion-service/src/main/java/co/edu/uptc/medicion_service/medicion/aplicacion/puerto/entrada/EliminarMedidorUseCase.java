package co.edu.uptc.medicion_service.medicion.aplicacion.puerto.entrada;

public interface EliminarMedidorUseCase {

	void iniciarEliminacion(Long medidorId);

	void confirmarEliminacion(Long medidorId);
}