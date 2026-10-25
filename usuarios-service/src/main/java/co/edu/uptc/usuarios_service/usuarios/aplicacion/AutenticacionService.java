package co.edu.uptc.usuarios_service.usuarios.aplicacion;

import java.util.EnumSet;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import co.edu.uptc.usuarios_service.usuarios.dominio.CorreoYaRegistradoException;
import co.edu.uptc.usuarios_service.usuarios.dominio.CredencialesInvalidasException;
import co.edu.uptc.usuarios_service.usuarios.dominio.TipoRol;
import co.edu.uptc.usuarios_service.usuarios.dominio.Usuario;

@Service
public class AutenticacionService implements AutenticacionUseCase {
    private final RepositorioUsuarios repositorio;
    private final PasswordEncoder passwordEncoder;
    private final EmisorTokens emisorTokens;

    public AutenticacionService(RepositorioUsuarios repositorio, PasswordEncoder passwordEncoder,
                                EmisorTokens emisorTokens) {
        this.repositorio = repositorio;
        this.passwordEncoder = passwordEncoder;
        this.emisorTokens = emisorTokens;
    }

    @Override
    public Usuario registrar(String nombre, String correo, String contrasena) {
        String correoNormalizado = normalizar(correo);
        if (repositorio.existePorCorreo(correoNormalizado)) {
            throw new CorreoYaRegistradoException(correoNormalizado);
        }
        Usuario usuario = new Usuario(nombre.trim(), correoNormalizado,
                passwordEncoder.encode(contrasena), EnumSet.of(TipoRol.USUARIO));
        return repositorio.guardar(usuario);
    }

    @Override
    public TokenAcceso iniciarSesion(String correo, String contrasena) {
        return repositorio.buscarPorCorreo(normalizar(correo))
                .filter(Usuario::isActivo)
                .filter(u -> passwordEncoder.matches(contrasena, u.getContrasenaHash()))
                .map(emisorTokens::emitir)
                .orElseThrow(CredencialesInvalidasException::new);
    }

    private String normalizar(String correo) { 
        return correo.trim().toLowerCase(); 
    }
}