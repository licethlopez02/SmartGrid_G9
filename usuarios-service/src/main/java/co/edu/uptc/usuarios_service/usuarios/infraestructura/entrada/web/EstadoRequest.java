package co.edu.uptc.usuarios_service.usuarios.infraestructura.entrada.web;

import jakarta.validation.constraints.NotNull;

public record EstadoRequest(@NotNull Boolean activo) {
    
}