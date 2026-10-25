package co.edu.uptc.usuarios_service.usuarios.infraestructura.entrada.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank @Email String correo, @NotBlank String contrasena) {
    
}
