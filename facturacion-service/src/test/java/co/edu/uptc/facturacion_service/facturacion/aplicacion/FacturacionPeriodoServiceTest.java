package co.edu.uptc.facturacion_service.facturacion.aplicacion;

import co.edu.uptc.facturacion_service.facturacion.dominio.AnomaliaFacturada;
import co.edu.uptc.facturacion_service.facturacion.dominio.EstadoFactura;
import co.edu.uptc.facturacion_service.facturacion.dominio.Factura;
import co.edu.uptc.facturacion_service.facturacion.dominio.FacturaDuplicadaException;
import co.edu.uptc.facturacion_service.facturacion.dominio.MedidorNoExisteException;
import co.edu.uptc.facturacion_service.facturacion.dominio.PeriodoIncompletoException;
import co.edu.uptc.facturacion_service.facturacion.dominio.PeriodoNoCerradoException;
import co.edu.uptc.facturacion_service.facturacion.dominio.Tarifa;
import co.edu.uptc.facturacion_service.facturacion.dominio.TarifaNoVigenteException;
import co.edu.uptc.facturacion_service.facturacion.dominio.TarifaVigente;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FacturacionPeriodoServiceTest {

    private static final LocalDate HOY = LocalDate.of(2026, 11, 1);
    private static final LocalDate INICIO = LocalDate.of(2026, 10, 1);
    private static final LocalDate FIN = LocalDate.of(2026, 10, 31);

    private final RepositorioFacturasEnMemoria facturas = new RepositorioFacturasEnMemoria();
    private final List<Tarifa> tarifas = new ArrayList<>();
    private final List<AnomaliaFacturada> anomalias = new ArrayList<>();
    private boolean medidorExiste = true;
    private boolean periodoCompleto = true;

    private void conTarifaVigente() {
        tarifas.add(new Tarifa(new TarifaVigente(new BigDecimal("850"), LocalDate.of(2026, 1, 1), null)));
    }

    private FacturacionPeriodoService servicio() {
        VerificadorMedidor verificador = id -> medidorExiste;
        ClienteMedicion medicion = new ClienteMedicion() {
            @Override
            public ConsumoPeriodo obtenerConsumo(Long id, LocalDate inicio, LocalDate fin) {
                return new ConsumoPeriodo(new BigDecimal("122"), periodoCompleto, 3);
            }

            @Override
            public List<AnomaliaFacturada> anomaliasNoResueltas(Long id, LocalDate inicio, LocalDate fin) {
                return anomalias;
            }
        };
        RepositorioTarifas repositorioTarifas = new RepositorioTarifas() {
            @Override
            public Tarifa guardar(Tarifa tarifa) {
                tarifas.add(tarifa);
                return tarifa;
            }

            @Override
            public List<Tarifa> listar() {
                return tarifas;
            }
        };
        return new FacturacionPeriodoService(verificador, medicion, repositorioTarifas,
                new FacturaFactory(facturas, new GeneracionFacturaService()), facturas);
    }

    @Test
    void generaLaFacturaConConsumoTarifaYAnomaliasDeMedicion() {
        conTarifaVigente();
        anomalias.add(new AnomaliaFacturada(1L, new BigDecimal("100"),
                Instant.parse("2026-10-20T10:00:00Z"), "La lectura se desvía más del 50% del promedio histórico"));

        Factura factura = servicio().generarFacturaDelPeriodo(1L, INICIO, FIN, HOY);

        assertEquals(new BigDecimal("103700"), factura.getMontoTotal());
        assertEquals(1, factura.getAnomaliasNoResueltas().size());
        assertEquals(EstadoFactura.PENDIENTE, factura.getEstado());
    }

    @Test
    void noFacturaUnPeriodoQueNoHaTerminado() {
        conTarifaVigente();
        assertThrows(PeriodoNoCerradoException.class,
                () -> servicio().generarFacturaDelPeriodo(1L, INICIO, HOY, HOY));
    }

    @Test
    void rechazaUnMedidorQueNoExiste() {
        conTarifaVigente();
        medidorExiste = false;
        assertThrows(MedidorNoExisteException.class,
                () -> servicio().generarFacturaDelPeriodo(1L, INICIO, FIN, HOY));
    }

    @Test
    void rechazaCuandoNoHayTarifaVigente() {
        assertThrows(TarifaNoVigenteException.class,
                () -> servicio().generarFacturaDelPeriodo(1L, INICIO, FIN, HOY));
    }

    @Test
    void rechazaUnPeriodoIncompleto() {
        conTarifaVigente();
        periodoCompleto = false;
        assertThrows(PeriodoIncompletoException.class,
                () -> servicio().generarFacturaDelPeriodo(1L, INICIO, FIN, HOY));
    }

    @Test
    void noGeneraDosVecesLaFacturaDelMismoPeriodo() {
        conTarifaVigente();
        FacturacionPeriodoService servicio = servicio();
        servicio.generarFacturaDelPeriodo(1L, INICIO, FIN, HOY);
        assertThrows(FacturaDuplicadaException.class,
                () -> servicio.generarFacturaDelPeriodo(1L, INICIO, FIN, HOY));
    }

    private static class RepositorioFacturasEnMemoria implements RepositorioFacturas {
        private final List<Factura> guardadas = new ArrayList<>();

        @Override
        public Optional<Factura> buscarPorId(Long id) {
            return guardadas.stream().filter(f -> id.equals(f.getId())).findFirst();
        }

        @Override
        public List<Factura> listarPorMedidorId(Long medidorId) {
            return guardadas.stream().filter(f -> medidorId.equals(f.getMedidorId())).toList();
        }

        @Override
        public boolean existePorMedidorYPeriodo(Long medidorId, LocalDate inicio, LocalDate fin) {
            return guardadas.stream().anyMatch(f -> medidorId.equals(f.getMedidorId())
                    && inicio.equals(f.getPeriodoInicio()) && fin.equals(f.getPeriodoFin()));
        }

        @Override
        public Factura guardar(Factura factura) {
            guardadas.add(factura);
            return factura;
        }
    }
}
