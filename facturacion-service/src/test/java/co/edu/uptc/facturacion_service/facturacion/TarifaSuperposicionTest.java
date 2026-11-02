package co.edu.uptc.facturacion_service.facturacion;

import co.edu.uptc.facturacion_service.facturacion.dominio.TarifaVigente;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TarifaSuperposicionTest {

    private static TarifaVigente tarifa(LocalDate desde, LocalDate hasta) {
        return new TarifaVigente(new BigDecimal("850"), desde, hasta);
    }

    @Test
    void dosTarifasContiguasNoSeSuperponen() {
        var primera = tarifa(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 6, 30));
        var segunda = tarifa(LocalDate.of(2026, 7, 1), null);
        assertFalse(primera.seSuperponeCon(segunda));
        assertFalse(segunda.seSuperponeCon(primera));
    }

    @Test
    void unaTarifaAbiertaSeSuperponeConUnaPosterior() {
        var abierta = tarifa(LocalDate.of(2026, 1, 1), null);
        var posterior = tarifa(LocalDate.of(2026, 7, 1), LocalDate.of(2026, 12, 31));
        assertTrue(abierta.seSuperponeCon(posterior));
    }

    @Test
    void dosTarifasAbiertasSiempreSeSuperponen() {
        var una = tarifa(LocalDate.of(2026, 1, 1), null);
        var otra = tarifa(LocalDate.of(2027, 1, 1), null);
        assertTrue(una.seSuperponeCon(otra));
    }
}