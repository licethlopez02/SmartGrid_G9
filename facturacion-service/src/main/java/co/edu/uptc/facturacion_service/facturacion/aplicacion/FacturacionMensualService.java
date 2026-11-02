package co.edu.uptc.facturacion_service.facturacion.aplicacion;

import co.edu.uptc.facturacion_service.facturacion.dominio.FacturaDuplicadaException;
import co.edu.uptc.facturacion_service.facturacion.dominio.MedidorLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
public class FacturacionMensualService {

    private static final Logger log = LoggerFactory.getLogger(FacturacionMensualService.class);

    private final FacturarPeriodoUseCase facturarPeriodo;
    private final RepositorioMedidoresLocales medidores;

    public FacturacionMensualService(FacturarPeriodoUseCase facturarPeriodo,
                                     RepositorioMedidoresLocales medidores) {
        this.facturarPeriodo = facturarPeriodo;
        this.medidores = medidores;
    }

    public ResumenFacturacionMensual facturarMes(YearMonth mes) {
        LocalDate inicio = mes.atDay(1);
        LocalDate fin = mes.atEndOfMonth();
        int generadas = 0;
        int duplicadas = 0;
        List<String> rechazadas = new ArrayList<>();

        for (MedidorLocal medidor : medidores.listarActivos()) {
            try {
                facturarPeriodo.generarFacturaDelPeriodo(medidor.getId(), inicio, fin);
                generadas++;
            } catch (FacturaDuplicadaException e) {
                duplicadas++;
            } catch (RuntimeException e) {
                rechazadas.add("Medidor " + medidor.getId() + ": " + e.getMessage());
                log.warn("No se facturó el medidor {} del mes {}: {}", medidor.getId(), mes, e.getMessage());
            }
        }
        return new ResumenFacturacionMensual(mes.toString(), generadas, duplicadas, rechazadas);
    }
}