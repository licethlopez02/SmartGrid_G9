package co.edu.uptc.smart_grid.medicion.dominio;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Aggregate root for the readings belonging to one meter.
 */
public final class Medidor {

	private final Long id;
	private final String ubicacion;
	private final List<LecturaConsumo> lecturas = new ArrayList<>();

	Medidor(Long id, String ubicacion) {
		this.id = Objects.requireNonNull(id, "El id del medidor es obligatorio");
		this.ubicacion = validarTexto(ubicacion, "La ubicación");
	}

	public Long id() {
		return id;
	}

	public String ubicacion() {
		return ubicacion;
	}

	public List<LecturaConsumo> historialLecturas() {
		return List.copyOf(lecturas);
	}

	public List<LecturaConsumo> historialReciente(int cantidad) {
		if (cantidad < 1) {
			throw new IllegalArgumentException("La cantidad debe ser positiva");
		}
		int desde = Math.max(0, lecturas.size() - cantidad);
		return List.copyOf(lecturas.subList(desde, lecturas.size()));
	}

	public void registrarLectura(LecturaConsumo lectura) {
		Objects.requireNonNull(lectura, "La lectura es obligatoria");
		if (!id.equals(lectura.medidorId())) {
			throw new IllegalArgumentException("La lectura pertenece a otro medidor");
		}
		if (!lecturas.isEmpty()) {
			Instant ultimaLectura = lecturas.getLast().timestamp();
			if (lectura.timestamp().isBefore(ultimaLectura)) {
				throw new IllegalArgumentException("Las lecturas deben registrarse en orden cronológico");
			}
		}
		lecturas.add(lectura);
	}

	private static String validarTexto(String valor, String nombre) {
		Objects.requireNonNull(valor, nombre + " es obligatoria");
		if (valor.isBlank()) {
			throw new IllegalArgumentException(nombre + " no puede estar vacía");
		}
		return valor;
	}
}
