package co.edu.uptc.smart_grid.medicion.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataMedidorRepository extends JpaRepository<MedidorJpaEntity, String> {
}
