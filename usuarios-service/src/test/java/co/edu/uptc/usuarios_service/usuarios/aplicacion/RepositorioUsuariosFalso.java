package co.edu.uptc.usuarios_service.usuarios.aplicacion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import co.edu.uptc.usuarios_service.usuarios.dominio.TipoRol;
import co.edu.uptc.usuarios_service.usuarios.dominio.Usuario;

public class RepositorioUsuariosFalso implements RepositorioUsuarios {
    private final Map<Long, Usuario> almacen = new HashMap<>();
    private long siguienteId = 1;

    @Override public Optional<Usuario> buscarPorId(Long id) { 
        return Optional.ofNullable(almacen.get(id)); 
    }

    @Override public Optional<Usuario> buscarPorCorreo(String correo) {
        return almacen.values().stream().filter(u -> u.getCorreo().equals(correo)).findFirst();
    }

    @Override public boolean existePorCorreo(String correo) { 
        return buscarPorCorreo(correo).isPresent(); 
    }

    @Override public boolean existeConRol(TipoRol rol) {
        return almacen.values().stream().anyMatch(u -> u.tieneRol(rol));
    }

    @Override public List<Usuario> listar() { 
        return new ArrayList<>(almacen.values()); 
    }

    @Override public Usuario guardar(Usuario usuario) {
        if (usuario.getId() == null) usuario.setId(siguienteId++);
        almacen.put(usuario.getId(), usuario);
        return usuario;
    }
}