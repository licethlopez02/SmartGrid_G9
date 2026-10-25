package co.edu.uptc.usuarios_service.usuarios.aplicacion;

import java.util.List;
import java.util.Set;

import co.edu.uptc.usuarios_service.usuarios.dominio.TipoRol;
import co.edu.uptc.usuarios_service.usuarios.dominio.Usuario;

public interface GestionUsuariosUseCase {
    Usuario obtener(Long id);
    List<Usuario> listar();
    void cambiarContrasena(Long id, String contrasenaActual, String contrasenaNueva);
    Usuario reemplazarRoles(Long id, Set<TipoRol> roles);
    Usuario cambiarEstado(Long id, boolean activo);
}