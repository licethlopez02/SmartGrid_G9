package co.edu.uptc.facturacion_service.facturacion.infraestructura.salida.persistencia;

import co.edu.uptc.facturacion_service.facturacion.dominio.Tarifa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TarifaRepository extends JpaRepository<Tarifa, Long> {
}