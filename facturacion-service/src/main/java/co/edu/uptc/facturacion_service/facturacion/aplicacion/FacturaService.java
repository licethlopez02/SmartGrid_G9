package co.edu.uptc.facturacion_service.facturacion.aplicacion;

import co.edu.uptc.facturacion_service.facturacion.dominio.Factura;
import co.edu.uptc.facturacion_service.facturacion.dominio.MedidorNoExisteException;
import co.edu.uptc.facturacion_service.facturacion.dominio.TarifaVigente;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class FacturaService implements FacturaUseCase {

    private final RepositorioFacturas repositorioFacturas;
    private final FacturaFactory facturaFactory;
    private final VerificadorMedidor verificadorMedidor;

    public FacturaService(RepositorioFacturas repositorioFacturas, FacturaFactory facturaFactory,
                          VerificadorMedidor verificadorMedidor) {
        this.repositorioFacturas = repositorioFacturas;
        this.facturaFactory = facturaFactory;
        this.verificadorMedidor = verificadorMedidor;
    }

    @Override
    public Factura generarFactura(Long medidorId, LocalDate periodoInicio, LocalDate periodoFin,
                                   BigDecimal consumoAcumuladoKwh, boolean periodoCompleto,
                                   TarifaVigente tarifaVigente) {
        if (!verificadorMedidor.existe(medidorId)) {
            throw new MedidorNoExisteException(medidorId);
        }
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