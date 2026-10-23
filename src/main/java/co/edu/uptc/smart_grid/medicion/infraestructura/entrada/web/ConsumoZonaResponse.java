package co.edu.uptc.smart_grid.medicion.infraestructura.entrada.web;

import java.math.BigDecimal;

public record ConsumoZonaResponse(String zona, BigDecimal consumoAcumulado) {
}
