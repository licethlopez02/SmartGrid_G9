package co.edu.uptc.smart_grid.medicion;

import java.util.Objects;

public record ResultadoDeteccion(boolean esAnomalia, String razon) {

	public ResultadoDeteccion {
		Objects.requireNonNull(razon, "La razón es obligatoria");
		if (razon.isBlank()) {
			throw new IllegalArgumentException("La razón no puede estar vacía");
		}
	}
}
