package co.edu.uptc.facturacion_service.facturacion.aplicacion;

import co.edu.uptc.facturacion_service.facturacion.dominio.Factura;

import java.time.LocalDate;

public interface FacturarPeriodoUseCase {
    Factura generarFacturaDelPeriodo(Long medidorId, LocalDate periodoInicio, LocalDate periodoFin);
}