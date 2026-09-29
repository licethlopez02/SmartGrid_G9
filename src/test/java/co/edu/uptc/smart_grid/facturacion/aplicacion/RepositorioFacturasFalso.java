package co.edu.uptc.smart_grid.facturacion.aplicacion;

import co.edu.uptc.smart_grid.facturacion.dominio.Factura;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class RepositorioFacturasFalso implements RepositorioFacturas {

    private final Map<Long, Factura> almacen = new LinkedHashMap<>();
    private long siguienteId = 1;

    @Override
    public Optional<Factura> buscarPorId(Long id) {
        return Optional.ofNullable(almacen.get(id));
    }

    @Override
    public List<Factura> listarPorMedidorId(Long medidorId) {
        return almacen.values().stream()
            .filter(f -> f.getMedidorId().equals(medidorId))
            .collect(Collectors.toList());
    }

    @Override
    public boolean existePorMedidorYPeriodo(Long medidorId, LocalDate periodoInicio, LocalDate periodoFin) {
        return almacen.values().stream()
            .anyMatch(f -> f.getMedidorId().equals(medidorId)
                && f.getPeriodoInicio().equals(periodoInicio)
                && f.getPeriodoFin().equals(periodoFin));
    }

    @Override
    public Factura guardar(Factura factura) {
        if (factura.getId() == null) {
            factura.setId(siguienteId++);
        }
        almacen.put(factura.getId(), factura);
        return factura;
    }
}