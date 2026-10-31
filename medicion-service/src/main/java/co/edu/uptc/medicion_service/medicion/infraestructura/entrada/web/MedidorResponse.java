package co.edu.uptc.medicion_service.medicion.infraestructura.entrada.web;

public record MedidorResponse(
		Long id,
		String ubicacion,
		Long usuarioId,
		String propiedad) {

	public MedidorResponse(Long id, String ubicacion) {
		this(id, ubicacion, null, ubicacion);
	}
}
