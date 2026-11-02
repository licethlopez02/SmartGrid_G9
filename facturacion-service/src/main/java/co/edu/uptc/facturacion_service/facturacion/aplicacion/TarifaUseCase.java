package co.edu.uptc.facturacion_service.facturacion.aplicacion;

import co.edu.uptc.facturacion_service.facturacion.dominio.Tarifa;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TarifaUseCase {
    Tarifa registrar(BigDecimal valor, LocalDate vigenciaDesde, LocalDate vigenciaHasta);
    List<Tarifa> listar();
}