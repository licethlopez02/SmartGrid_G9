package co.edu.uptc.usuarios_service.usuarios.infraestructura.configuracion;

import java.util.EnumSet;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import co.edu.uptc.usuarios_service.usuarios.aplicacion.RepositorioUsuarios;
import co.edu.uptc.usuarios_service.usuarios.dominio.TipoRol;
import co.edu.uptc.usuarios_service.usuarios.dominio.Usuario;

@Component
public class OperadorInicial implements ApplicationRunner {
    private final RepositorioUsuarios repositorio;
    private final PasswordEncoder passwordEncoder;
    private final String correo;
    private final String contrasena;

    public OperadorInicial(RepositorioUsuarios repositorio, PasswordEncoder passwordEncoder,
                           @Value("${operador-inicial.correo}") String correo,
                           @Value("${operador-inicial.contrasena}") String contrasena) {
        this.repositorio = repositorio;
        this.passwordEncoder = passwordEncoder;
        this.correo = correo;
        this.contrasena = contrasena;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!repositorio.existeConRol(TipoRol.OPERADOR)) {
            repositorio.guardar(new Usuario("Operador de red", correo,
                    passwordEncoder.encode(contrasena), EnumSet.of(TipoRol.OPERADOR)));
        }
    }
}