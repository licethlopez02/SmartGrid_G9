package co.edu.uptc.smart_grid.medicion.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

class MedidorFactoryTest {

	@Test
	void crearConstruyeMedidorValidoConHistorialVacio() {
		Medidor medidor = MedidorFactory.crear(1L, "Casa 1");
		assertEquals(1L, medidor.id());
		assertEquals("Casa 1", medidor.ubicacion());
		assertEquals(List.of(), medidor.historialLecturas());
	}

	@Test
	void crearRechazaIdNuloOVacio() {
		assertThrows(NullPointerException.class, () -> MedidorFactory.crear(null, "Casa 1"));
		assertThrows(NullPointerException.class, () -> MedidorFactory.crear(null, "Casa 1"));
	}

	@Test
	void crearRechazaUbicacionNulaOVacia() {
		assertThrows(NullPointerException.class, () -> MedidorFactory.crear(1L, null));
		assertThrows(IllegalArgumentException.class, () -> MedidorFactory.crear(1L, ""));
		assertThrows(IllegalArgumentException.class, () -> MedidorFactory.crear(1L, "   "));
	}

	@Test
	void crearConHistorialRegistraTodasLasLecturas() {
		LecturaConsumo primera = lectura(1L, "10", 0);
		LecturaConsumo segunda = lectura(1L, "12", 1);

		Medidor medidor = MedidorFactory.crearConHistorial(
				1L, "Casa 1", List.of(primera, segunda));

		assertEquals(List.of(primera, segunda), medidor.historialLecturas());
	}

	private static LecturaConsumo lectura(Long medidorId, String valor, long segundo) {
		return new LecturaConsumo(
				new BigDecimal(valor), Instant.parse("2026-01-01T10:00:00Z").plusSeconds(segundo), medidorId);
	}
}
