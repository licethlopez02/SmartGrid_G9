package co.edu.uptc.facturacion_service.facturacion.aplicacion;

import co.edu.uptc.facturacion_service.facturacion.dominio.Factura;
import co.edu.uptc.facturacion_service.facturacion.dominio.MedidorNoExisteException;
import co.edu.uptc.facturacion_service.facturacion.dominio.PeriodoNoCerradoException;
import co.edu.uptc.facturacion_service.facturacion.dominio.Tarifa;
import co.edu.uptc.facturacion_service.facturacion.dominio.TarifaNoVigenteException;
import co.edu.uptc.facturacion_service.facturacion.dominio.TarifaVigente;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class FacturacionPeriodoService implements FacturarPeriodoUseCase {

    private final VerificadorMedidor verificadorMedidor;
    private final ClienteMedicion clienteMedicion;
    private final RepositorioTarifas repositorioTarifas;
    private final FacturaFactory facturaFactory;
    private final RepositorioFacturas repositorioFacturas;

    public FacturacionPeriodoService(VerificadorMedidor verificadorMedidor, ClienteMedicion clienteMedicion,
                                     RepositorioTarifas repositorioTarifas, FacturaFactory facturaFactory,
                                     RepositorioFacturas repositorioFacturas) {
        this.verificadorMedidor = verificadorMedidor;
        this.clienteMedicion = clienteMedicion;
        this.repositorioTarifas = repositorioTarifas;
        this.facturaFactory = facturaFactory;
        this.repositorioFacturas = repositorioFacturas;
    }

    @Override
    public Factura generarFacturaDelPeriodo(Long medidorId, LocalDate periodoInicio, LocalDate periodoFin) {
        return generarFacturaDelPeriodo(medidorId, periodoInicio, periodoFin, LocalDate.now());
    }

    public Factura generarFacturaDelPeriodo(Long medidorId, LocalDate periodoInicio, LocalDate periodoFin,
                                            LocalDate hoy) {
        if (medidorId == null || periodoInicio == null || periodoFin == null) {
            throw new IllegalArgumentException("El medidor y el período son obligatorios");
        }
        if (periodoInicio.isAfter(periodoFin)) {
            throw new IllegalArgumentException("El inicio del período no puede ser posterior al fin");
        }
        if (!periodoFin.isBefore(hoy)) {
            throw new PeriodoNoCerradoException(periodoFin);
        }
        if (!verificadorMedidor.existe(medidorId)) {
            throw new MedidorNoExisteException(medidorId);
        }

        TarifaVigente tarifa = repositorioTarifas.listar().stream()
                .map(Tarifa::vigente)
                .filter(t -> t.estaVigente(periodoFin))
                .findFirst()
                .orElseThrow(() -> new TarifaNoVigenteException(periodoFin));

        ConsumoPeriodo consumo = clienteMedicion.obtenerConsumo(medidorId, periodoInicio, periodoFin);

        Factura factura = facturaFactory.crear(medidorId, periodoInicio, periodoFin,
                consumo.consumo(), consumo.periodoCompleto(), tarifa);
        factura.incluirAnomalias(clienteMedicion.anomaliasNoResueltas(medidorId, periodoInicio, periodoFin));
        return repositorioFacturas.guardar(factura);
    }
}