package co.edu.uptc.smart_grid.medicion.infraestructura;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.edu.uptc.smart_grid.medicion.aplicacion.puerto.salida.MedidorRepositoryPort;
import co.edu.uptc.smart_grid.medicion.aplicacion.servicio.DetectarAnomaliaApplicationService;
import co.edu.uptc.smart_grid.medicion.aplicacion.servicio.ConsultarMedicionApplicationService;
import co.edu.uptc.smart_grid.medicion.aplicacion.servicio.RegistrarMedidorApplicationService;

@Configuration
public class MedicionConfiguration {

	@Bean
	public DetectarAnomaliaApplicationService detectarAnomaliaApplicationService(
			MedidorRepositoryPort medidorRepository) {
		return new DetectarAnomaliaApplicationService(medidorRepository);
	}

	@Bean
	public RegistrarMedidorApplicationService registrarMedidorApplicationService(
			MedidorRepositoryPort medidorRepository) {
		return new RegistrarMedidorApplicationService(medidorRepository);
	}

	@Bean
	public ConsultarMedicionApplicationService consultarMedicionApplicationService(
			MedidorRepositoryPort medidorRepository) {
		return new ConsultarMedicionApplicationService(medidorRepository);
	}
}
