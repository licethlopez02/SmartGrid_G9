package co.edu.uptc.usuarios_service.usuarios.aplicacion;

import java.util.List;
import java.util.Optional;

import co.edu.uptc.usuarios_service.usuarios.dominio.TipoRol;
import co.edu.uptc.usuarios_service.usuarios.dominio.Usuario;

public interface RepositorioUsuarios {
    Optional<Usuario> buscarPorId(Long id);
    Optional<Usuario> buscarPorCorreo(String correo);
    boolean existePorCorreo(String correo);
    boolean existeConRol(TipoRol rol);
    List<Usuario> listar();
    Usuario guardar(Usuario usuario);
}