package co.edu.uptc.medicion_service.medicion.infraestructura.persistencia;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.salida.AnomaliaRepositoryPort;
import co.edu.uptc.medicion_service.medicion.dominio.Anomalia;

@Repository
public class AnomaliaPersistenceAdapter implements AnomaliaRepositoryPort {

	private final SpringDataAnomaliaRepository repository;

	public AnomaliaPersistenceAdapter(SpringDataAnomaliaRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional
	public Anomalia guardar(Anomalia anomalia) {
		return aDominio(repository.save(new AnomaliaJpaEntity(anomalia)));
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Anomalia> buscarPorId(Long id) {
		return repository.findById(id).map(this::aDominio);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Anomalia> buscarPorMedidor(Long medidorId, Instant desde, Instant hasta,
			boolean soloNoResueltas) {
		List<AnomaliaJpaEntity> encontrados;
		if (desde != null && hasta != null) {
			encontrados = soloNoResueltas
					? repository.findByMedidorIdAndResueltaFalseAndTimestampBetweenOrderByTimestampAsc(
							medidorId, desde, hasta)
					: repository.findByMedidorIdAndTimestampBetweenOrderByTimestampAsc(medidorId, desde, hasta);
		} else {
			encontrados = repository.findByMedidorIdOrderByTimestampAsc(medidorId);
			if (soloNoResueltas) {
				encontrados = encontrados.stream().filter(a -> !a.isResuelta()).toList();
			}
		}
		if (desde != null || hasta != null) {
			encontrados = encontrados.stream()
					.filter(a -> desde == null || !a.getTimestamp().isBefore(desde))
					.filter(a -> hasta == null || !a.getTimestamp().isAfter(hasta))
					.toList();
		}
		return encontrados.stream().map(this::aDominio).toList();
	}

	private Anomalia aDominio(AnomaliaJpaEntity entity) {
		return new Anomalia(entity.getId(), entity.getMedidorId(), entity.getValor(),
				entity.getTimestamp(), entity.getRazon(), entity.isResuelta());
	}
}
