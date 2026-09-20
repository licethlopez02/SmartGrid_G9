package co.edu.uptc.smart_grid.facturacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;

public interface FacturaRepository extends JpaRepository<Factura, Long> {
    boolean existsByMedidorIdAndPeriodoInicioAndPeriodoFin(
    Long medidorId, LocalDate periodoInicio, LocalDate periodoFin);
}