package co.edu.uptc.smart_grid.medicion.infraestructura;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.edu.uptc.smart_grid.medicion.aplicacion.puerto.salida.MedidorRepositoryPort;
import co.edu.uptc.smart_grid.medicion.aplicacion.servicio.DetectarAnomaliaApplicationService;

@Configuration
public class MedicionConfiguration {

	@Bean
	public DetectarAnomaliaApplicationService detectarAnomaliaApplicationService(
			MedidorRepositoryPort medidorRepository) {
		return new DetectarAnomaliaApplicationService(medidorRepository);
	}
}
