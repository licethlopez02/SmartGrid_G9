package co.edu.uptc.medicion_service.medicion.aplicacion.servicio;

import java.util.Map;
import java.util.Objects;

import org.springframework.transaction.annotation.Transactional;

import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.entrada.EliminarMedidorUseCase;
import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.salida.MedidorRepositoryPort;
import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.salida.PublicadorEventos;
import co.edu.uptc.medicion_service.medicion.dominio.Medidor;

public class EliminarMedidorApplicationService implements EliminarMedidorUseCase {

	private final MedidorRepositoryPort medidorRepository;
	private final PublicadorEventos publicadorEventos;

	public EliminarMedidorApplicationService(MedidorRepositoryPort medidorRepository,
			PublicadorEventos publicadorEventos) {
		this.medidorRepository = Objects.requireNonNull(medidorRepository, "El repositorio es obligatorio");
		this.publicadorEventos = Objects.requireNonNull(publicadorEventos, "El publicador es obligatorio");
	}

	@Override
	@Transactional
	public void iniciarEliminacion(Long medidorId) {
		Objects.requireNonNull(medidorId, "El id del medidor es obligatorio");
		Medidor medidor = medidorRepository.buscarPorId(medidorId)
				.orElseThrow(() -> new IllegalArgumentException("El medidor no existe"));
		if (medidor.estaEnEliminacion()) {
			return; // idempotente: la Saga ya está en curso
		}
		medidor.marcarEnEliminacion();
		medidorRepository.guardar(medidor);
		publicadorEventos.publicar("medidores.en-eliminacion", String.valueOf(medidorId),
				Map.of("medidorId", medidorId));
	}

	@Override
	@Transactional
	public void confirmarEliminacion(Long medidorId) {
		medidorRepository.buscarPorId(medidorId)
				.filter(Medidor::estaEnEliminacion)
				.ifPresent(medidor -> medidorRepository.eliminar(medidor.id()));
	}
}