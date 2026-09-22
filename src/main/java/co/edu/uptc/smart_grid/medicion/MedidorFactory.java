package co.edu.uptc.smart_grid.medicion;

import java.util.Objects;

/**
 * Factory for creating a valid meter aggregate.
 */
public final class MedidorFactory {

	private MedidorFactory() {
	}

	public static Medidor crear(String id, String ubicacion) {
		return new Medidor(id, ubicacion);
	}

	public static Medidor crearConHistorial(String id, String ubicacion,
			Iterable<LecturaConsumo> lecturas) {
		Objects.requireNonNull(lecturas, "El historial es obligatorio");
		Medidor medidor = crear(id, ubicacion);
		for (LecturaConsumo lectura : lecturas) {
			medidor.registrarLectura(lectura);
		}
		return medidor;
	}
}
