package co.edu.uptc.eureka_server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;

class EurekaServerConfigTest {

	private PropertySource<?> applicationProperties() throws IOException {
		List<PropertySource<?>> sources = new YamlPropertySourceLoader()
				.load("application", new ClassPathResource("application.yaml"));
		return sources.getFirst();
	}

	@Test
	void configuresStandaloneEurekaServerOnItsDeclaredPort() throws IOException {
		PropertySource<?> properties = applicationProperties();

		assertEquals("8761", properties.getProperty("server.port").toString());
		assertEquals("eureka-server", properties.getProperty("spring.application.name"));
		assertEquals(Boolean.FALSE, properties.getProperty("eureka.client.register-with-eureka"));
		assertEquals(Boolean.FALSE, properties.getProperty("eureka.client.fetch-registry"));
		assertTrue(EurekaServerApplication.class.isAnnotationPresent(EnableEurekaServer.class));
	}
}
