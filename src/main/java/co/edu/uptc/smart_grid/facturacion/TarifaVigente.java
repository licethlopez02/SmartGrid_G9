package co.edu.uptc.smart_grid.facturacion;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Embeddable;

@Embeddable
public record TarifaVigente(BigDecimal valor, LocalDate vigenciaDesde, LocalDate vigenciaHasta) {

    public TarifaVigente {
        if (valor == null || valor.signum() <= 0) {
            throw new IllegalArgumentException("La tarifa debe ser un valor positivo");
        }
        if (vigenciaDesde == null) {
            throw new IllegalArgumentException("La tarifa debe tener una fecha de inicio de vigencia");
        }
        if (vigenciaHasta != null && vigenciaHasta.isBefore(vigenciaDesde)) {
            throw new IllegalArgumentException(
                "La fecha de fin de vigencia no puede ser anterior a la fecha de inicio");
        }
    }

    public boolean estaVigente(LocalDate fecha) {
        boolean yaIniciada = !fecha.isBefore(vigenciaDesde);
        boolean noFinalizada = vigenciaHasta == null || !fecha.isAfter(vigenciaHasta);
        return yaIniciada && noFinalizada;
    }
}