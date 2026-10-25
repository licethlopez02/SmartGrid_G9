package co.edu.uptc.usuarios_service.usuarios.infraestructura.entrada.web;

import java.util.Set;

import co.edu.uptc.usuarios_service.usuarios.dominio.TipoRol;
import jakarta.validation.constraints.NotEmpty;

public record RolesRequest(@NotEmpty Set<TipoRol> roles) {
    
}
