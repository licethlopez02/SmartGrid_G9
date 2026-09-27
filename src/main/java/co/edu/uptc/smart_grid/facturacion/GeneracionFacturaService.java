package co.edu.uptc.smart_grid.facturacion;

import org.springframework.stereotype.Service;
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