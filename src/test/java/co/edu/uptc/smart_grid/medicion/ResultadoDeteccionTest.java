package co.edu.uptc.smart_grid.medicion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ResultadoDeteccionTest {

	@Test
	void creaResultadoConDatosValidos() {
		ResultadoDeteccion resultado = new ResultadoDeteccion(true, "Desviación superior al umbral");

		assertEquals(true, resultado.esAnomalia());
		assertEquals("Desviación superior al umbral", resultado.razon());
	}

	@Test
	void rechazaRazonNulaVaciaOEnBlanco() {
		assertThrows(NullPointerException.class, () -> new ResultadoDeteccion(false, null));
		assertThrows(IllegalArgumentException.class, () -> new ResultadoDeteccion(false, ""));
		assertThrows(IllegalArgumentException.class, () -> new ResultadoDeteccion(false, "   "));
	}
}
