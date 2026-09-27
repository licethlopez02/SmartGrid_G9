package co.edu.uptc.smart_grid.facturacion.aplicacion;

import co.edu.uptc.smart_grid.facturacion.dominio.Factura;
import co.edu.uptc.smart_grid.facturacion.dominio.TarifaVigente;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FacturaServiceConFalsoTest {

    @Test
    void generaYRecuperaUnaFacturaSinNingunaDependenciaDeSpringNiDeBaseDeDatos() {
        RepositorioFacturasFalso repositorio = new RepositorioFacturasFalso();
        GeneracionFacturaService generacionFacturaService = new GeneracionFacturaService();
        FacturaFactory factory = new FacturaFactory(repositorio, generacionFacturaService);
        FacturaService service = new FacturaService(repositorio, factory);

        TarifaVigente tarifa = new TarifaVigente(new BigDecimal("850"), LocalDate.of(2026, 1, 1), null);

        Factura generada = service.generarFactura(1L, LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 30),
            new BigDecimal("120"), true, tarifa);

        assertNotNull(generada.getId());
        assertEquals(1L, service.buscarPorId(generada.getId()).getMedidorId());
    }
}