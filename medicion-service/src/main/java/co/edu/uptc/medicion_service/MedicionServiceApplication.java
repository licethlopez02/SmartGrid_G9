package co.edu.uptc.medicion_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MedicionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedicionServiceApplication.class, args);
	}

}
