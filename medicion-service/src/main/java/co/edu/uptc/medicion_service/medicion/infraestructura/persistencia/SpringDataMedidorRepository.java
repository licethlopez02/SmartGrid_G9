package co.edu.uptc.medicion_service.medicion.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataMedidorRepository extends JpaRepository<MedidorJpaEntity, String> {

	boolean existsByUsuarioIdAndPropiedadAndEstado(Long usuarioId, String propiedad, String estado);
}
