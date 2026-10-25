package co.edu.uptc.usuarios_service.usuarios.infraestructura.entrada.web;

import java.util.Set;

public record UsuarioResponse(Long id, String nombre, String correo, Set<String> roles, boolean activo) {
    
}
