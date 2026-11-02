package co.edu.uptc.facturacion_service.facturacion.aplicacion;

import java.math.BigDecimal;

public record ConsumoPeriodo(BigDecimal consumo, boolean periodoCompleto, int cantidadLecturas) {
    
}