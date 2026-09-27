package co.edu.uptc.smart_grid.facturacion.infraestructura.salida.persistencia;

import co.edu.uptc.smart_grid.facturacion.FacturaRepository;
import co.edu.uptc.smart_grid.facturacion.aplicacion.RepositorioFacturas;
import co.edu.uptc.smart_grid.facturacion.dominio.Factura;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class FacturaRepositoryJpaAdapter implements RepositorioFacturas {

    private final FacturaRepository facturaRepository;

    public FacturaRepositoryJpaAdapter(FacturaRepository facturaRepository) {
        this.facturaRepository = facturaRepository;
    }

    @Override
    public Optional<Factura> buscarPorId(Long id) {
        return facturaRepository.findById(id);
    }

    @Override
    public List<Factura> listarPorMedidorId(Long medidorId) {
        return facturaRepository.findByMedidorId(medidorId);
    }

    @Override
    public boolean existePorMedidorYPeriodo(Long medidorId, LocalDate periodoInicio, LocalDate periodoFin) {
        return facturaRepository.existsByMedidorIdAndPeriodoInicioAndPeriodoFin(medidorId, periodoInicio, periodoFin);
    }

    @Override
    public Factura guardar(Factura factura) {
        return facturaRepository.save(factura);
    }
}