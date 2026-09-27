package co.edu.uptc.smart_grid.facturacion.aplicacion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import co.edu.uptc.smart_grid.facturacion.dominio.Factura;
import co.edu.uptc.smart_grid.facturacion.dominio.TarifaVigente;

public interface FacturaUseCase {

    Factura generarFactura(Long medidorId, LocalDate periodoInicio, LocalDate periodoFin,
                            BigDecimal consumoAcumuladoKwh, boolean periodoCompleto,
                            TarifaVigente tarifaVigente);

    Factura buscarPorId(Long id);

    List<Factura> listarPorMedidor(Long medidorId);
}
