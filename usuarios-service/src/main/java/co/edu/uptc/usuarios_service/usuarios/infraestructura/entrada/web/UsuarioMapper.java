package co.edu.uptc.usuarios_service.usuarios.infraestructura.entrada.web;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import co.edu.uptc.usuarios_service.usuarios.aplicacion.TokenAcceso;
import co.edu.uptc.usuarios_service.usuarios.dominio.TipoRol;
import co.edu.uptc.usuarios_service.usuarios.dominio.Usuario;

public final class UsuarioMapper {
    private UsuarioMapper() {}

    public static UsuarioResponse aUsuarioResponse(Usuario u) {
        Set<String> roles = u.getRoles().stream().map(TipoRol::nombre)
                .collect(Collectors.toCollection(TreeSet::new));
        return new UsuarioResponse(u.getId(), u.getNombre(), u.getCorreo(), roles, u.isActivo());
    }

    public static TokenResponse aTokenResponse(TokenAcceso t) {
        return new TokenResponse(t.token(), "Bearer", t.expiraEnSegundos());
    }
}