package co.edu.uptc.smart_grid.facturacion.aplicacion;

import co.edu.uptc.smart_grid.facturacion.FacturaFactory;
import co.edu.uptc.smart_grid.facturacion.dominio.Factura;
import co.edu.uptc.smart_grid.facturacion.dominio.TarifaVigente;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class FacturaService implements FacturaUseCase {

    private final RepositorioFacturas repositorioFacturas;
    private final FacturaFactory facturaFactory;

    public FacturaService(RepositorioFacturas repositorioFacturas, FacturaFactory facturaFactory) {
        this.repositorioFacturas = repositorioFacturas;
        this.facturaFactory = facturaFactory;
    }

    @Override
    public Factura generarFactura(Long medidorId, LocalDate periodoInicio, LocalDate periodoFin,
                                   BigDecimal consumoAcumuladoKwh, boolean periodoCompleto,
                                   TarifaVigente tarifaVigente) {
        Factura factura = facturaFactory.crear(medidorId, periodoInicio, periodoFin,
            consumoAcumuladoKwh, periodoCompleto, tarifaVigente);
        return repositorioFacturas.guardar(factura);
    }

    @Override
    public Factura buscarPorId(Long id) {
        return repositorioFacturas.buscarPorId(id)
            .orElseThrow(() -> new IllegalArgumentException("No existe una factura con id " + id));
    }

    @Override
    public List<Factura> listarPorMedidor(Long medidorId) {
        return repositorioFacturas.listarPorMedidorId(medidorId);
    }
}
