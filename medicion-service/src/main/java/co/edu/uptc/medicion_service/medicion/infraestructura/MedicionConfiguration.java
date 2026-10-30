package co.edu.uptc.medicion_service.medicion.infraestructura;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.salida.MedidorRepositoryPort;
import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.salida.PublicadorEventos;
import co.edu.uptc.medicion_service.medicion.aplicacion.servicio.DetectarAnomaliaApplicationService;
import co.edu.uptc.medicion_service.medicion.aplicacion.servicio.EliminarMedidorApplicationService;
import co.edu.uptc.medicion_service.medicion.aplicacion.servicio.ConsultarMedicionApplicationService;
import co.edu.uptc.medicion_service.medicion.aplicacion.servicio.RegistrarMedidorApplicationService;

@Configuration
public class MedicionConfiguration {

	@Bean
	public DetectarAnomaliaApplicationService detectarAnomaliaApplicationService(
			MedidorRepositoryPort medidorRepository) {
		return new DetectarAnomaliaApplicationService(medidorRepository);
	}

	@Bean
	public RegistrarMedidorApplicationService registrarMedidorApplicationService(
			MedidorRepositoryPort medidorRepository, PublicadorEventos publicadorEventos) {
		return new RegistrarMedidorApplicationService(medidorRepository, publicadorEventos);
	}

	@Bean
	public ConsultarMedicionApplicationService consultarMedicionApplicationService(
			MedidorRepositoryPort medidorRepository) {
		return new ConsultarMedicionApplicationService(medidorRepository);
	}

	@Bean
	public EliminarMedidorApplicationService eliminarMedidorApplicationService(
			MedidorRepositoryPort medidorRepository, PublicadorEventos publicadorEventos) {
		return new EliminarMedidorApplicationService(medidorRepository, publicadorEventos);
	}
}
