package co.edu.uptc.smart_grid.facturacion.aplicacion;

import co.edu.uptc.smart_grid.facturacion.dominio.Factura;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RepositorioFacturas {

    Optional<Factura> buscarPorId(Long id);

    List<Factura> listarPorMedidorId(Long medidorId);

    boolean existePorMedidorYPeriodo(Long medidorId, LocalDate periodoInicio, LocalDate periodoFin);

    Factura guardar(Factura factura);
}
