package co.edu.uptc.facturacion_service.facturacion.infraestructura.entrada.web;

import java.time.LocalDate;

public record FacturaRequest(Long medidorId, LocalDate periodoInicio, LocalDate periodoFin) {
    
}