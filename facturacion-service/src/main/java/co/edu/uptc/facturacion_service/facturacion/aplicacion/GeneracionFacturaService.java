package co.edu.uptc.facturacion_service.facturacion.aplicacion;

import org.springframework.stereotype.Service;

import co.edu.uptc.facturacion_service.facturacion.dominio.TarifaVigente;

import java.time.LocalDate;

@Service
public class GeneracionFacturaService {

    public boolean puedeGenerarse(boolean periodoCompleto, TarifaVigente tarifaVigente, 
        LocalDate fechaCierrePeriodo) {
        if (!periodoCompleto) {
            return false;
        }
        return tarifaVigente.estaVigente(fechaCierrePeriodo);
    }
}