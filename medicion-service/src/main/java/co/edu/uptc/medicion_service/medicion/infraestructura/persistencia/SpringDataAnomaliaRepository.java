package co.edu.uptc.medicion_service.medicion.infraestructura.persistencia;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataAnomaliaRepository extends JpaRepository<AnomaliaJpaEntity, Long> {
	List<AnomaliaJpaEntity> findByMedidorIdAndTimestampBetweenOrderByTimestampAsc(Long medidorId,
			Instant desde, Instant hasta);
	List<AnomaliaJpaEntity> findByMedidorIdOrderByTimestampAsc(Long medidorId);
	List<AnomaliaJpaEntity> findByMedidorIdAndResueltaFalseAndTimestampBetweenOrderByTimestampAsc(
			Long medidorId, Instant desde, Instant hasta);
}
