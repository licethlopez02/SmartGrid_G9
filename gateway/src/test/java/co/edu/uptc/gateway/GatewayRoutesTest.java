package co.edu.uptc.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;

class GatewayRoutesTest {

	private PropertySource<?> applicationProperties() throws IOException {
		List<PropertySource<?>> sources = new YamlPropertySourceLoader()
				.load("application", new ClassPathResource("application.yaml"));
		return sources.getFirst();
	}

	@Test
	void routesEachApiAreaToItsDeclaredService() throws IOException {
		PropertySource<?> properties = applicationProperties();

		assertEquals("8000", properties.getProperty("server.port").toString());
		assertEquals("gateway", properties.getProperty("spring.application.name"));

		assertRoute(properties, 0, "medicion-route", "lb://medicion-service",
				"Path=/api/medidores/**,/api/anomalias/**");
		assertRoute(properties, 1, "facturacion-route", "lb://facturacion-service",
				"Path=/api/facturas/**,/api/tarifas/**");
		assertRoute(properties, 2, "usuarios-route", "lb://usuarios-service",
				"Path=/api/auth/**,/api/usuarios/**");
		assertEquals("http://localhost:8761/eureka",
				properties.getProperty("eureka.client.service-url.defaultZone"));
	}

	private static void assertRoute(PropertySource<?> properties, int index, String id, String uri,
			String predicate) {
		String route = "spring.cloud.gateway.server.webflux.routes[" + index + "].";
		assertEquals(id, properties.getProperty(route + "id"));
		assertEquals(uri, properties.getProperty(route + "uri"));
		assertEquals(predicate, properties.getProperty(route + "predicates[0]"));
	}
}
