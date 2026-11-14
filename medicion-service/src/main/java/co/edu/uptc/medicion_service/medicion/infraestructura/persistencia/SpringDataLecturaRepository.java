package co.edu.uptc.medicion_service.medicion.infraestructura.persistencia;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataLecturaRepository extends JpaRepository<LecturaJpaEntity, Long> {
	List<LecturaJpaEntity> findByMedidorIdOrderByTimestampAsc(String medidorId);

	@Query("select avg(l.valor) from LecturaJpaEntity l where l.medidorId = :medidorId")
	java.math.BigDecimal promedioPorMedidor(@Param("medidorId") String medidorId);
}
