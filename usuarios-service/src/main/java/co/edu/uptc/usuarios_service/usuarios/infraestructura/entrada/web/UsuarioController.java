package co.edu.uptc.usuarios_service.usuarios.infraestructura.entrada.web;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import co.edu.uptc.usuarios_service.usuarios.aplicacion.GestionUsuariosUseCase;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final GestionUsuariosUseCase gestion;

    public UsuarioController(GestionUsuariosUseCase gestion) { this.gestion = gestion; }

    @GetMapping("/me")
    public UsuarioResponse yo(@AuthenticationPrincipal Jwt jwt) {
        return UsuarioMapper.aUsuarioResponse(gestion.obtener(Long.valueOf(jwt.getSubject())));
    }

    @PutMapping("/me/contrasena")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cambiarContrasena(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CambioContrasenaRequest r) {
        gestion.cambiarContrasena(Long.valueOf(jwt.getSubject()), r.contrasenaActual(), r.contrasenaNueva());
    }

    @GetMapping
    @PreAuthorize("hasRole('OPERADOR')")
    public List<UsuarioResponse> listar() {
        return gestion.listar().stream().map(UsuarioMapper::aUsuarioResponse).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public UsuarioResponse obtener(@PathVariable Long id) {
        return UsuarioMapper.aUsuarioResponse(gestion.obtener(id));
    }

    @PutMapping("/{id}/roles")
    @PreAuthorize("hasRole('OPERADOR')")
    public UsuarioResponse roles(@PathVariable Long id, @Valid @RequestBody RolesRequest r) {
        return UsuarioMapper.aUsuarioResponse(gestion.reemplazarRoles(id, r.roles()));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('OPERADOR')")
    public UsuarioResponse estado(@PathVariable Long id, @Valid @RequestBody EstadoRequest r) {
        return UsuarioMapper.aUsuarioResponse(gestion.cambiarEstado(id, r.activo()));
    }
}