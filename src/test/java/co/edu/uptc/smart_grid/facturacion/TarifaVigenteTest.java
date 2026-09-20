package co.edu.uptc.smart_grid.facturacion;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class TarifaVigenteTest {

    @Test
    void rechazaValorCeroONegativo() {
        assertThrows(IllegalArgumentException.class,
            () -> new TarifaVigente(BigDecimal.ZERO, LocalDate.now(), null));
        assertThrows(IllegalArgumentException.class,
            () -> new TarifaVigente(new BigDecimal("-100"), LocalDate.now(), null));
    }

    @Test
    void rechazaVigenciaHastaAnteriorAVigenciaDesde() {
        LocalDate desde = LocalDate.of(2026, 1, 1);
        LocalDate hasta = LocalDate.of(2025, 12, 31);
        assertThrows(IllegalArgumentException.class,
            () -> new TarifaVigente(new BigDecimal("850"), desde, hasta));
    }

    @Test
    void estaVigenteDentroDelRango() {
        TarifaVigente tarifa = new TarifaVigente(new BigDecimal("850"),
            LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31));

        assertTrue(tarifa.estaVigente(LocalDate.of(2026, 6, 15)));
        assertFalse(tarifa.estaVigente(LocalDate.of(2025, 12, 31)));
        assertFalse(tarifa.estaVigente(LocalDate.of(2027, 1, 1)));
    }

    @Test
    void esVigenteIndefinidamenteSinFechaFin() {
        TarifaVigente tarifa = new TarifaVigente(new BigDecimal("900"), LocalDate.of(2026, 1, 1), null);
        assertTrue(tarifa.estaVigente(LocalDate.of(2030, 1, 1)));
    }
}
