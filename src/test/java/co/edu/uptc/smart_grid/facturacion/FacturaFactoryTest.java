package co.edu.uptc.smart_grid.facturacion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacturaFactoryTest {

    @Mock private FacturaRepository facturaRepository;
    @Mock private GeneracionFacturaService generacionFacturaService;

    private FacturaFactory facturaFactory;

    @BeforeEach
    void setUp() {
        facturaFactory = new FacturaFactory(facturaRepository, generacionFacturaService);
    }

    @Test
    void rechazaFacturaDuplicadaParaElMismoPeriodo() {
        Long medidorId = 1L;
        LocalDate inicio = LocalDate.of(2026, 6, 1);
        LocalDate fin = LocalDate.of(2026, 6, 30);
        TarifaVigente tarifa = new TarifaVigente(new BigDecimal("850"), LocalDate.of(2026, 1, 1), null);

        when(facturaRepository.existsByMedidorIdAndPeriodoInicioAndPeriodoFin(medidorId, inicio, fin))
            .thenReturn(true);

        assertThrows(FacturaDuplicadaException.class, () ->
            facturaFactory.crear(medidorId, inicio, fin, new BigDecimal("120"), true, tarifa));
    }

    @Test
    void rechazaSiElPeriodoDeConsumoEstaIncompleto() {
        Long medidorId = 1L;
        LocalDate inicio = LocalDate.of(2026, 6, 1);
        LocalDate fin = LocalDate.of(2026, 6, 30);
        TarifaVigente tarifa = new TarifaVigente(new BigDecimal("850"), LocalDate.of(2026, 1, 1), null);

        when(facturaRepository.existsByMedidorIdAndPeriodoInicioAndPeriodoFin(medidorId, inicio, fin))
            .thenReturn(false);
        when(generacionFacturaService.puedeGenerarse(eq(false), any(), eq(fin)))
            .thenReturn(false);

        assertThrows(PeriodoIncompletoException.class, () ->
            facturaFactory.crear(medidorId, inicio, fin, new BigDecimal("120"), false, tarifa));
    }

    @Test
    void creaLaFacturaCuandoTodoEsValido() {
        Long medidorId = 1L;
        LocalDate inicio = LocalDate.of(2026, 6, 1);
        LocalDate fin = LocalDate.of(2026, 6, 30);
        TarifaVigente tarifa = new TarifaVigente(new BigDecimal("850"), LocalDate.of(2026, 1, 1), null);

        when(facturaRepository.existsByMedidorIdAndPeriodoInicioAndPeriodoFin(medidorId, inicio, fin))
            .thenReturn(false);
        when(generacionFacturaService.puedeGenerarse(true, tarifa, fin))
            .thenReturn(true);

        Factura factura = facturaFactory.crear(medidorId, inicio, fin, new BigDecimal("120"), true, tarifa);

        assertNotNull(factura);
        assertEquals(medidorId, factura.getMedidorId());
        assertEquals(EstadoFactura.PENDIENTE, factura.getEstado());
        assertEquals(0, new BigDecimal("102000").compareTo(factura.getMontoTotal())); // 120 * 850
    }
}