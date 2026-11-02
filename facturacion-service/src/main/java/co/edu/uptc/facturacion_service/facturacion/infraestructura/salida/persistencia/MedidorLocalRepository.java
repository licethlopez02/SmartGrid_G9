package co.edu.uptc.facturacion_service.facturacion.infraestructura.salida.persistencia;

import co.edu.uptc.facturacion_service.facturacion.dominio.MedidorLocal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedidorLocalRepository extends JpaRepository<MedidorLocal, Long> {
    List<MedidorLocal> findByActivoTrue();
    List<MedidorLocal> findByUsuarioId(Long usuarioId);
}