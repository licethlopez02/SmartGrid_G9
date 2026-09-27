package co.edu.uptc.smart_grid.facturacion;

import co.edu.uptc.smart_grid.facturacion.dominio.Factura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface FacturaRepository extends JpaRepository<Factura, Long> {
    boolean existsByMedidorIdAndPeriodoInicioAndPeriodoFin(Long medidorId, LocalDate periodoInicio, LocalDate periodoFin);
    List<Factura> findByMedidorId(Long medidorId);
}