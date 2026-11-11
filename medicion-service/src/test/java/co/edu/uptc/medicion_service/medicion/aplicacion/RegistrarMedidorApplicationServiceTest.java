package co.edu.uptc.medicion_service.medicion.aplicacion;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.salida.MedidorRepositoryPort;
import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.salida.PublicadorEventos;
import co.edu.uptc.medicion_service.medicion.aplicacion.servicio.RegistrarMedidorApplicationService;
import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.salida.VerificadorUsuario;
import co.edu.uptc.medicion_service.medicion.dominio.Medidor;

class RegistrarMedidorApplicationServiceTest {

	@Test
	void registraMedidorConUsuarioYUsaUbicacionComoPropiedadPorDefecto() {
		FakeMedidorRepository medidores = new FakeMedidorRepository();
		FakePublicadorEventos eventos = new FakePublicadorEventos();
		VerificadorUsuario verificador = usuarioId -> usuarioId == 42L;

		Medidor medidor = new RegistrarMedidorApplicationService(medidores, eventos, verificador)
				.registrar(1L, "Zona norte", 42L, null);

		assertEquals(42L, medidor.usuarioId());
		assertEquals("Zona norte", medidor.propiedad());
		assertEquals(1, eventos.eventos.size());
		assertEquals(42L, eventos.eventos.getFirst().get("usuarioId"));

		Medidor segundo = new RegistrarMedidorApplicationService(medidores, eventos, verificador)
				.registrar(2L, "Zona norte", 42L, null);
		assertEquals("Zona norte", segundo.ubicacion());
		assertEquals(2, medidores.medidores.size());
	}

	private static final class FakeMedidorRepository implements MedidorRepositoryPort {
		private final List<Medidor> medidores = new ArrayList<>();

		@Override
		public Optional<Medidor> buscarPorId(Long id) {
			return medidores.stream().filter(m -> m.id().equals(id)).findFirst();
		}

		@Override
		public Medidor guardar(Medidor medidor) {
			medidores.add(medidor);
			return medidor;
		}
	}

	private static final class FakePublicadorEventos implements PublicadorEventos {
		private final List<java.util.Map<String, Object>> eventos = new ArrayList<>();

		@Override
		public void publicar(String topico, String clave, java.util.Map<String, Object> contenido) {
			eventos.add(contenido);
		}
	}
}
