package co.edu.uptc.medicion_service.medicion.aplicacion.servicio;

import java.util.Map;
import java.util.Objects;

import org.springframework.transaction.annotation.Transactional;

import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.entrada.RegistrarMedidorUseCase;
import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.salida.MedidorRepositoryPort;
import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.salida.PublicadorEventos;
import co.edu.uptc.medicion_service.medicion.dominio.Medidor;
import co.edu.uptc.medicion_service.medicion.dominio.MedidorFactory;

public class RegistrarMedidorApplicationService implements RegistrarMedidorUseCase {

	private final MedidorRepositoryPort medidorRepository;
	private final PublicadorEventos publicadorEventos;

	public RegistrarMedidorApplicationService(MedidorRepositoryPort medidorRepository,
			PublicadorEventos publicadorEventos) {
		this.medidorRepository = Objects.requireNonNull(medidorRepository, "El repositorio es obligatorio");
		this.publicadorEventos = Objects.requireNonNull(publicadorEventos, "El publicador es obligatorio");
	}

	@Override
	@Transactional
	public Medidor registrar(Long id, String ubicacion) {
		Objects.requireNonNull(id, "El id del medidor es obligatorio");
		if (medidorRepository.buscarPorId(id).isPresent()) {
			throw new IllegalArgumentException("Ya existe un medidor con id " + id);
		}
		Medidor medidor = MedidorFactory.crear(id, ubicacion);
		Medidor guardado = medidorRepository.guardar(medidor);

		// Transactional Outbox: el evento se guarda en la MISMA transacción que el medidor.
		publicadorEventos.publicar(
				"medidores.registrados",
				String.valueOf(id),
				Map.of("id", id, "ubicacion", ubicacion));
		return guardado;
	}
}