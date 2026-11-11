package co.edu.uptc.medicion_service.medicion.aplicacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.salida.AnomaliaRepositoryPort;
import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.salida.MedidorRepositoryPort;
import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.salida.PublicadorEventos;
import co.edu.uptc.medicion_service.medicion.aplicacion.servicio.ConsultarMedicionApplicationService;
import co.edu.uptc.medicion_service.medicion.aplicacion.servicio.DetectarAnomaliaApplicationService;
import co.edu.uptc.medicion_service.medicion.dominio.Anomalia;
import co.edu.uptc.medicion_service.medicion.dominio.LecturaConsumo;
import co.edu.uptc.medicion_service.medicion.dominio.Medidor;
import co.edu.uptc.medicion_service.medicion.dominio.MedidorFactory;

class AnomaliaYPeriodoApplicationServiceTest {

	@Test
	void persisteAnomaliaPublicaEventoYPermiteResolverla() {
		FakeMedidorRepository medidores = new FakeMedidorRepository();
		medidores.guardar(MedidorFactory.crear(1L, "Zona norte", 42L, "Casa"));
		FakeAnomaliaRepository anomalias = new FakeAnomaliaRepository();
		FakePublicadorEventos eventos = new FakePublicadorEventos();
		DetectarAnomaliaApplicationService servicio = new DetectarAnomaliaApplicationService(medidores,
				new co.edu.uptc.medicion_service.medicion.dominio.DeteccionAnomaliaService(),
				anomalias, eventos);

		servicio.detectar(1L, lectura("10", 0));
		servicio.detectar(1L, lectura("30", 1));

		assertEquals(1, anomalias.guardadas.size());
		assertEquals("anomalias.detectadas", eventos.topicos.getLast());
		assertEquals(42L, eventos.eventos.getLast().get("destinatarioId"));
		Long id = anomalias.guardadas.getFirst().id();
		servicio.resolver(id);
		assertTrue(anomalias.guardadas.getFirst().resuelta());
	}

	@Test
	void periodoCompletoUsaLaPrimeraYLaUltimaLectura() {
		FakeMedidorRepository medidores = new FakeMedidorRepository();
		Medidor medidor = MedidorFactory.crear(1L, "Zona norte");
		medidor.registrarLectura(lectura("10", 0));
		medidor.registrarLectura(lectura("12", 3600));
		medidores.guardar(medidor);

		Instant desde = Instant.parse("2026-01-01T10:00:00Z");
		Instant hasta = desde.plusSeconds(3600);
		assertTrue(new ConsultarMedicionApplicationService(medidores).periodoCompleto(1L, desde, hasta));
	}

	@Test
	void alertaDeSensorConservaElValorNegativoReal() {
		FakeMedidorRepository medidores = new FakeMedidorRepository();
		medidores.guardar(MedidorFactory.crear(1L, "Zona norte", 42L, "Casa"));
		FakePublicadorEventos eventos = new FakePublicadorEventos();

		new DetectarAnomaliaApplicationService(medidores,
				new co.edu.uptc.medicion_service.medicion.dominio.DeteccionAnomaliaService(),
				new FakeAnomaliaRepository(), eventos)
				.alertarSensorDefectuoso(1L, new BigDecimal("-3"), Instant.parse("2026-01-01T10:00:00Z"),
						"Se recibió una lectura negativa");

		assertEquals(new BigDecimal("-3"), eventos.eventos.getFirst().get("valor"));
	}

	private static LecturaConsumo lectura(String valor, long segundos) {
		return new LecturaConsumo(new BigDecimal(valor),
				Instant.parse("2026-01-01T10:00:00Z").plusSeconds(segundos), 1L);
	}

	private static final class FakeMedidorRepository implements MedidorRepositoryPort {
		private final Map<Long, Medidor> medidores = new HashMap<>();
		@Override public Optional<Medidor> buscarPorId(Long id) { return Optional.ofNullable(medidores.get(id)); }
		@Override public List<Medidor> listar() { return new ArrayList<>(medidores.values()); }
		@Override public Medidor guardar(Medidor medidor) { medidores.put(medidor.id(), medidor); return medidor; }
	}

	private static final class FakeAnomaliaRepository implements AnomaliaRepositoryPort {
		private final List<Anomalia> guardadas = new ArrayList<>();
		@Override public Anomalia guardar(Anomalia anomalia) {
			Anomalia persistida = anomalia.id() == null
					? new Anomalia((long) guardadas.size() + 1, anomalia.medidorId(), anomalia.valor(),
							anomalia.timestamp(), anomalia.razon(), anomalia.resuelta())
					: anomalia;
			guardadas.removeIf(a -> a.id().equals(persistida.id()));
			guardadas.add(persistida);
			return persistida;
		}
		@Override public Optional<Anomalia> buscarPorId(Long id) {
			return guardadas.stream().filter(a -> a.id().equals(id)).findFirst();
		}
		@Override public List<Anomalia> buscarPorMedidor(Long id, Instant desde, Instant hasta, boolean pendientes) {
			return guardadas.stream().filter(a -> a.medidorId().equals(id)).toList();
		}
	}

	private static final class FakePublicadorEventos implements PublicadorEventos {
		private final List<String> topicos = new ArrayList<>();
		private final List<Map<String, Object>> eventos = new ArrayList<>();
		@Override public void publicar(String topico, String clave, Map<String, Object> contenido) {
			topicos.add(topico);
			eventos.add(contenido);
		}
	}
}
