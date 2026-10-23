package co.edu.uptc.smart_grid.medicion.aplicacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import co.edu.uptc.smart_grid.medicion.aplicacion.puerto.salida.MedidorRepositoryPort;
import co.edu.uptc.smart_grid.medicion.aplicacion.servicio.DetectarAnomaliaApplicationService;
import co.edu.uptc.smart_grid.medicion.dominio.LecturaConsumo;
import co.edu.uptc.smart_grid.medicion.dominio.Medidor;
import co.edu.uptc.smart_grid.medicion.dominio.MedidorFactory;
import co.edu.uptc.smart_grid.medicion.dominio.ResultadoDeteccion;

class DetectarAnomaliaApplicationServiceTest {

	@Test
	void detectaAnomaliaYGuardaLaNuevaLecturaUsandoFake() {
		FakeMedidorRepository fake = new FakeMedidorRepository();
		Medidor medidor = MedidorFactory.crear(1L, "Casa 1");
		medidor.registrarLectura(lectura("10", 0));
		fake.guardar(medidor);

		DetectarAnomaliaApplicationService casoDeUso = new DetectarAnomaliaApplicationService(fake);
		ResultadoDeteccion resultado = casoDeUso.detectar(1L, lectura("30", 1));

		assertTrue(resultado.esAnomalia());
		assertEquals(2, fake.buscarPorId(1L).orElseThrow().historialLecturas().size());
	}

	private static LecturaConsumo lectura(String valor, long segundo) {
		return new LecturaConsumo(new BigDecimal(valor),
				Instant.parse("2026-01-01T10:00:00Z").plusSeconds(segundo), 1L);
	}

	private static final class FakeMedidorRepository implements MedidorRepositoryPort {
		private final Map<Long, Medidor> medidores = new HashMap<>();

		@Override
		public Optional<Medidor> buscarPorId(Long medidorId) {
			return Optional.ofNullable(medidores.get(medidorId));
		}

		@Override
		public Medidor guardar(Medidor medidor) {
			medidores.put(medidor.id(), medidor);
			return medidor;
		}
	}
}
