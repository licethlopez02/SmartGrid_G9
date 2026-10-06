package co.edu.uptc.smart_grid.medicion.aplicacion.puerto.entrada;

import co.edu.uptc.smart_grid.medicion.dominio.LecturaConsumo;
import co.edu.uptc.smart_grid.medicion.dominio.ResultadoDeteccion;

public interface DetectarAnomaliaUseCase {

	ResultadoDeteccion detectar(Long medidorId, LecturaConsumo nuevaLectura);
}
