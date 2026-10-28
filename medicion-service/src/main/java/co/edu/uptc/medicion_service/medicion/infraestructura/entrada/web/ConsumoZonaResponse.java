package co.edu.uptc.medicion_service.medicion.infraestructura.entrada.web;

import java.math.BigDecimal;

public record ConsumoZonaResponse(String zona, BigDecimal consumoAcumulado) {
}
