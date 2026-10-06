package co.edu.uptc.smart_grid.medicion.infraestructura.entrada.web;

public record ResultadoDeteccionResponse(
		boolean esAnomalia,
		String razon) {
}
