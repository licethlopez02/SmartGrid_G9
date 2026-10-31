package co.edu.uptc.facturacion_service.facturacion;

import co.edu.uptc.facturacion_service.facturacion.dominio.EstadoFactura;
import co.edu.uptc.facturacion_service.facturacion.dominio.Factura;
import co.edu.uptc.facturacion_service.facturacion.dominio.FacturaYaPagadaException;
import co.edu.uptc.facturacion_service.facturacion.dominio.TarifaVigente;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FacturaTest {

    private static final LocalDate GENERACION = LocalDate.of(2026, 10, 1);

    private Factura nuevaFactura() {
        TarifaVigente tarifa = new TarifaVigente(new BigDecimal("850"), LocalDate.of(2026, 1, 1), null);
        return new Factura(1L, LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30),
            new BigDecimal("120.5"), tarifa, GENERACION);
    }

    @Test
    void laFechaDeVencimientoSeCalculaDesdeLaGeneracion() {
        assertEquals(GENERACION.plusDays(Factura.DIAS_PARA_VENCER), nuevaFactura().getFechaVencimiento());
    }

    @Test
    void estaPendienteHastaElDiaDelVencimientoInclusive() {
        Factura factura = nuevaFactura();
        assertEquals(EstadoFactura.PENDIENTE, factura.estadoActual(factura.getFechaVencimiento()));
    }

    @Test
    void estaVencidaDespuesDeLaFechaDeVencimiento() {
        Factura factura = nuevaFactura();
        assertEquals(EstadoFactura.VENCIDA, factura.estadoActual(factura.getFechaVencimiento().plusDays(1)));
    }

    @Test
    void unaFacturaVencidaSePuedePagar() {
        Factura factura = nuevaFactura();
        LocalDate despuesDelVencimiento = factura.getFechaVencimiento().plusDays(30);
        factura.registrarPago();
        assertEquals(EstadoFactura.PAGADA, factura.estadoActual(despuesDelVencimiento));
    }

    @Test
    void noSePuedePagarDosVeces() {
        Factura factura = nuevaFactura();
        factura.registrarPago();
        assertThrows(FacturaYaPagadaException.class, factura::registrarPago);
    }
}