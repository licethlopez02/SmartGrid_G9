package co.edu.uptc.medicion_service.medicion.aplicacion.puerto.salida;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import co.edu.uptc.medicion_service.medicion.dominio.Anomalia;

public interface AnomaliaRepositoryPort {

	Anomalia guardar(Anomalia anomalia);
	Optional<Anomalia> buscarPorId(Long id);
	List<Anomalia> buscarPorMedidor(Long medidorId, Instant desde, Instant hasta, boolean soloNoResueltas);
}
