package co.edu.uptc.usuarios_service.usuarios.aplicacion;

import java.util.List;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import co.edu.uptc.usuarios_service.usuarios.dominio.ReglaDeUsuarioException;
import co.edu.uptc.usuarios_service.usuarios.dominio.TipoRol;
import co.edu.uptc.usuarios_service.usuarios.dominio.Usuario;
import co.edu.uptc.usuarios_service.usuarios.dominio.UsuarioNoEncontradoException;

@Service
public class GestionUsuariosService implements GestionUsuariosUseCase {
    private final RepositorioUsuarios repositorio;
    private final PasswordEncoder passwordEncoder;

    public GestionUsuariosService(RepositorioUsuarios repositorio, PasswordEncoder passwordEncoder) {
        this.repositorio = repositorio;
        this.passwordEncoder = passwordEncoder;
    }

    @Override public Usuario obtener(Long id) { 
        return buscar(id); 
    }

    @Override public List<Usuario> listar() { 
        return repositorio.listar(); 
    }

    @Override
    public void cambiarContrasena(Long id, String contrasenaActual, String contrasenaNueva) {
        Usuario usuario = buscar(id);
        if (!passwordEncoder.matches(contrasenaActual, usuario.getContrasenaHash())) {
            throw new ReglaDeUsuarioException("La contraseña actual es incorrecta");
        }
        usuario.cambiarContrasenaHash(passwordEncoder.encode(contrasenaNueva));
        repositorio.guardar(usuario);
    }

    @Override
    public Usuario reemplazarRoles(Long id, Set<TipoRol> roles) {
        Usuario usuario = buscar(id);
        usuario.reemplazarRoles(roles);
        return repositorio.guardar(usuario);
    }

    @Override
    public Usuario cambiarEstado(Long id, boolean activo) {
        Usuario usuario = buscar(id);
        usuario.cambiarEstado(activo);
        return repositorio.guardar(usuario);
    }

    private Usuario buscar(Long id) {
        return repositorio.buscarPorId(id).orElseThrow(() -> new UsuarioNoEncontradoException(id));
    }
}