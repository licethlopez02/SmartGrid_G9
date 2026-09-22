package co.edu.uptc.smart_grid.medicion;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.Test;

class LecturaConsumoTest {

	@Test
	void creaLecturaValidaSinLanzarExcepcion() {
		assertDoesNotThrow(() -> new LecturaConsumo(
				new BigDecimal("12.50"), Instant.parse("2026-01-01T10:00:00Z"), "M-001"));
	}

	@Test
	void rechazaValorNegativo() {
		assertThrows(IllegalArgumentException.class, () -> new LecturaConsumo(
				new BigDecimal("-0.01"), Instant.parse("2026-01-01T10:00:00Z"), "M-001"));
	}

	@Test
	void rechazaMedidorIdVacioOEnBlanco() {
		assertThrows(IllegalArgumentException.class, () -> new LecturaConsumo(
				new BigDecimal("1"), Instant.parse("2026-01-01T10:00:00Z"), ""));
		assertThrows(IllegalArgumentException.class, () -> new LecturaConsumo(
				new BigDecimal("1"), Instant.parse("2026-01-01T10:00:00Z"), "   "));
	}

	@Test
	void sugiereSensorDefectuosoCuandoElValorEsCero() {
		LecturaConsumo lectura = crearLectura("0");

		assertTrue(lectura.sugiereSensorDefectuoso(new BigDecimal("10")));
	}

	@Test
	void sugiereSensorDefectuosoCuandoLaVariacionSuperaElCincuentaPorCiento() {
		LecturaConsumo lectura = crearLectura("16");

		assertTrue(lectura.sugiereSensorDefectuoso(new BigDecimal("10")));
	}

	@Test
	void noSugiereSensorDefectuosoCuandoLaVariacionEsNormal() {
		LecturaConsumo lectura = crearLectura("12");

		assertFalse(lectura.sugiereSensorDefectuoso(new BigDecimal("10")));
	}

	private static LecturaConsumo crearLectura(String valor) {
		return new LecturaConsumo(
				new BigDecimal(valor), Instant.parse("2026-01-01T10:00:00Z"), "M-001");
	}
}
