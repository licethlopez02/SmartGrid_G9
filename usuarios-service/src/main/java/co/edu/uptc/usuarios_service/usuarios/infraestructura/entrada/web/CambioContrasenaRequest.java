package co.edu.uptc.usuarios_service.usuarios.infraestructura.entrada.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CambioContrasenaRequest(
        @NotBlank String contrasenaActual,
        @NotBlank @Size(min = 8) String contrasenaNueva) {
            
        }