package co.edu.uptc.medicion_service.medicion.infraestructura.persistencia;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataLecturaRepository extends JpaRepository<LecturaJpaEntity, Long> {
	List<LecturaJpaEntity> findByMedidorIdOrderByTimestampAsc(String medidorId);
}
