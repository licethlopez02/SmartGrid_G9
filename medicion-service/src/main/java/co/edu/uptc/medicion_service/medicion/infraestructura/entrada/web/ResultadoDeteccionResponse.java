package co.edu.uptc.medicion_service.medicion.infraestructura.entrada.web;

public record ResultadoDeteccionResponse(
		boolean esAnomalia,
		String razon) {
}
