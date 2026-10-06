package co.edu.uptc.smart_grid.medicion.infraestructura.entrada.web;

import java.math.BigDecimal;
import java.time.Instant;

public record ConsumoResponse(BigDecimal consumoAcumulado, Instant desde, Instant hasta) {
}
