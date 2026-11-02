package co.edu.uptc.facturacion_service.facturacion.infraestructura.entrada.programada;

import co.edu.uptc.facturacion_service.facturacion.aplicacion.FacturacionMensualService;
import co.edu.uptc.facturacion_service.facturacion.aplicacion.ResumenFacturacionMensual;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.time.ZoneId;

@Component
public class FacturacionMensualScheduler {

    private static final Logger log = LoggerFactory.getLogger(FacturacionMensualScheduler.class);
    private static final ZoneId ZONA = ZoneId.of("America/Bogota");

    private final FacturacionMensualService facturacionMensual;

    public FacturacionMensualScheduler(FacturacionMensualService facturacionMensual) {
        this.facturacionMensual = facturacionMensual;
    }

    @Scheduled(cron = "0 0 2 1 * *", zone = "America/Bogota")
    public void facturarMesAnterior() {
        YearMonth mes = YearMonth.now(ZONA).minusMonths(1);
        ResumenFacturacionMensual resumen = facturacionMensual.facturarMes(mes);
        log.info("Facturación mensual {}: {} generadas, {} ya existían, {} rechazadas",
                resumen.mes(), resumen.generadas(), resumen.duplicadas(), resumen.rechazadas().size());
    }
}
