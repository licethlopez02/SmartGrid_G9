package co.edu.uptc.usuarios_service.usuarios.infraestructura.entrada.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import co.edu.uptc.usuarios_service.usuarios.aplicacion.AutenticacionUseCase;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AutenticacionUseCase autenticacion;

    public AuthController(AutenticacionUseCase autenticacion) { this.autenticacion = autenticacion; }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse registro(@Valid @RequestBody RegistroRequest r) {
        return UsuarioMapper.aUsuarioResponse(autenticacion.registrar(r.nombre(), r.correo(), r.contrasena()));
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest r) {
        return UsuarioMapper.aTokenResponse(autenticacion.iniciarSesion(r.correo(), r.contrasena()));
    }
}