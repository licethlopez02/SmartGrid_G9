package co.edu.uptc.medicion_service.medicion.infraestructura;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.salida.MedidorRepositoryPort;
import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.salida.PublicadorEventos;
import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.salida.VerificadorUsuario;
import co.edu.uptc.medicion_service.medicion.aplicacion.servicio.DetectarAnomaliaApplicationService;
import co.edu.uptc.medicion_service.medicion.aplicacion.servicio.EliminarMedidorApplicationService;
import co.edu.uptc.medicion_service.medicion.aplicacion.servicio.ConsultarMedicionApplicationService;
import co.edu.uptc.medicion_service.medicion.aplicacion.servicio.RegistrarMedidorApplicationService;

@Configuration
public class MedicionConfiguration {

	@Bean
	public DetectarAnomaliaApplicationService detectarAnomaliaApplicationService(
			MedidorRepositoryPort medidorRepository,
			co.edu.uptc.medicion_service.medicion.aplicacion.puerto.salida.AnomaliaRepositoryPort anomaliaRepository,
			PublicadorEventos publicadorEventos) {
		return new DetectarAnomaliaApplicationService(medidorRepository,
				new co.edu.uptc.medicion_service.medicion.dominio.DeteccionAnomaliaService(),
				anomaliaRepository, publicadorEventos);
	}

	@Bean
	public RegistrarMedidorApplicationService registrarMedidorApplicationService(
			MedidorRepositoryPort medidorRepository, PublicadorEventos publicadorEventos,
			VerificadorUsuario verificadorUsuario) {
		return new RegistrarMedidorApplicationService(medidorRepository, publicadorEventos, verificadorUsuario);
	}

	@Bean
	public ConsultarMedicionApplicationService consultarMedicionApplicationService(
			MedidorRepositoryPort medidorRepository,
			co.edu.uptc.medicion_service.medicion.aplicacion.puerto.salida.AnomaliaRepositoryPort anomaliaRepository) {
		return new ConsultarMedicionApplicationService(medidorRepository, anomaliaRepository);
	}

	@Bean
	public EliminarMedidorApplicationService eliminarMedidorApplicationService(
			MedidorRepositoryPort medidorRepository, PublicadorEventos publicadorEventos) {
		return new EliminarMedidorApplicationService(medidorRepository, publicadorEventos);
	}
}
