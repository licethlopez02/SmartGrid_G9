package co.edu.uptc.facturacion_service.facturacion.aplicacion;

import co.edu.uptc.facturacion_service.facturacion.dominio.AnomaliaFacturada;

import java.time.LocalDate;
import java.util.List;

public interface ClienteMedicion {

    ConsumoPeriodo obtenerConsumo(Long medidorId, LocalDate periodoInicio, LocalDate periodoFin);

    List<AnomaliaFacturada> anomaliasNoResueltas(Long medidorId, LocalDate periodoInicio, LocalDate periodoFin);
}