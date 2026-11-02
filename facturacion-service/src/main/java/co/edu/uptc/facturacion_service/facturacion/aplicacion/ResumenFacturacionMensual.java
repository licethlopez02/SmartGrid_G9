package co.edu.uptc.facturacion_service.facturacion.aplicacion;

import java.util.List;

public record ResumenFacturacionMensual(String mes, int generadas, int duplicadas, List<String> rechazadas) {}
