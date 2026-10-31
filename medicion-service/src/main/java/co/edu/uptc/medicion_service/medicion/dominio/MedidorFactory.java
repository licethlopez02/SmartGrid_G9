package co.edu.uptc.medicion_service.medicion.dominio;

import java.util.Objects;

/**
 * Factory for creating a valid meter aggregate.
 */
public final class MedidorFactory {

	private MedidorFactory() {
	}

	public static Medidor crear(Long id, String ubicacion) {
		return new Medidor(id, ubicacion);
	}

	public static Medidor crear(Long id, String ubicacion, Long usuarioId, String propiedad) {
		return new Medidor(id, ubicacion, usuarioId, propiedad);
	}

	public static Medidor crearConHistorial(Long id, String ubicacion,
			Iterable<LecturaConsumo> lecturas) {
		return crearConHistorial(id, ubicacion, null, ubicacion, lecturas);
	}

	public static Medidor crearConHistorial(Long id, String ubicacion, Long usuarioId,
			String propiedad, Iterable<LecturaConsumo> lecturas) {
		Objects.requireNonNull(lecturas, "El historial es obligatorio");
		Medidor medidor = crear(id, ubicacion, usuarioId, propiedad);
		for (LecturaConsumo lectura : lecturas) {
			medidor.registrarLecturaFueraDeOrden(lectura);
		}
		return medidor;
	}
}
