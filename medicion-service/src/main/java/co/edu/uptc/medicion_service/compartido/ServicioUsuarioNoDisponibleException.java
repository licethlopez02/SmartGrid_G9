package co.edu.uptc.medicion_service.compartido;

public class ServicioUsuarioNoDisponibleException extends RuntimeException {

	public ServicioUsuarioNoDisponibleException(Throwable causa) {
		super("El servicio de usuarios no está disponible", causa);
	}
}
