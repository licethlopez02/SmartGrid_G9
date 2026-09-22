package co.edu.uptc.smart_grid.medicion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

class MedidorFactoryTest {

	@Test
	void crearConstruyeMedidorValidoConHistorialVacio() {
		Medidor medidor = MedidorFactory.crear("M-001", "Casa 1");

		assertEquals("M-001", medidor.id());
		assertEquals("Casa 1", medidor.ubicacion());
		assertEquals(List.of(), medidor.historialLecturas());
	}

	@Test
	void crearRechazaIdNuloOVacio() {
		assertThrows(NullPointerException.class, () -> MedidorFactory.crear(null, "Casa 1"));
		assertThrows(IllegalArgumentException.class, () -> MedidorFactory.crear("", "Casa 1"));
		assertThrows(IllegalArgumentException.class, () -> MedidorFactory.crear("   ", "Casa 1"));
	}

	@Test
	void crearRechazaUbicacionNulaOVacia() {
		assertThrows(NullPointerException.class, () -> MedidorFactory.crear("M-001", null));
		assertThrows(IllegalArgumentException.class, () -> MedidorFactory.crear("M-001", ""));
		assertThrows(IllegalArgumentException.class, () -> MedidorFactory.crear("M-001", "   "));
	}

	@Test
	void crearConHistorialRegistraTodasLasLecturas() {
		LecturaConsumo primera = lectura("M-001", "10", 0);
		LecturaConsumo segunda = lectura("M-001", "12", 1);

		Medidor medidor = MedidorFactory.crearConHistorial(
				"M-001", "Casa 1", List.of(primera, segunda));

		assertEquals(List.of(primera, segunda), medidor.historialLecturas());
	}

	private static LecturaConsumo lectura(String medidorId, String valor, long segundo) {
		return new LecturaConsumo(
				new BigDecimal(valor), Instant.parse("2026-01-01T10:00:00Z").plusSeconds(segundo), medidorId);
	}
}
