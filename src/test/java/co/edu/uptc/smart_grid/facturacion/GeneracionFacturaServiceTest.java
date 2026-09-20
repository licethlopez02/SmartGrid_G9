package co.edu.uptc.smart_grid.facturacion;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class GeneracionFacturaServiceTest {

    private final GeneracionFacturaService service = new GeneracionFacturaService();

    @Test
    void noPermiteGenerarSiPeriodoIncompleto() {
        TarifaVigente tarifa = new TarifaVigente(new BigDecimal("850"), LocalDate.of(2026, 1, 1), null);
        assertFalse(service.puedeGenerarse(false, tarifa, LocalDate.of(2026, 6, 30)));
    }

    @Test
    void noPermiteGenerarSiTarifaNoVigente() {
        TarifaVigente tarifa = new TarifaVigente(new BigDecimal("850"),
            LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31));
        assertFalse(service.puedeGenerarse(true, tarifa, LocalDate.of(2026, 6, 30)));
    }

    @Test
    void permiteGenerarSiPeriodoCompletoYTarifaVigente() {
        TarifaVigente tarifa = new TarifaVigente(new BigDecimal("850"), LocalDate.of(2026, 1, 1), null);
        assertTrue(service.puedeGenerarse(true, tarifa, LocalDate.of(2026, 6, 30)));
    }
}