package co.edu.uptc.usuarios_service.usuarios.dominio;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(nullable = false)
    private String contrasenaHash;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "usuario_roles", joinColumns = @JoinColumn(name = "usuario_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false)
    private Set<TipoRol> roles = new HashSet<>();

    @Column(nullable = false)
    private boolean activo = true;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    protected Usuario() {}

    public Usuario(String nombre, String correo, String contrasenaHash, Set<TipoRol> roles) {
        this.nombre = nombre;
        this.correo = correo;
        this.contrasenaHash = contrasenaHash;
        reemplazarRoles(roles);
    }

    public boolean tieneRol(TipoRol rol) { return roles.contains(rol); }

    public void reemplazarRoles(Set<TipoRol> nuevos) {
        if (nuevos == null || nuevos.isEmpty()) {
            throw new ReglaDeUsuarioException("El usuario debe tener al menos un rol");
        }
        roles.clear();
        roles.addAll(nuevos);
    }

    public void cambiarContrasenaHash(String hash) { this.contrasenaHash = hash; }
    public void cambiarEstado(boolean activo) { this.activo = activo; }
    public void setId(Long id) { this.id = id; } 

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public String getContrasenaHash() { return contrasenaHash; }
    public Set<TipoRol> getRoles() { return Set.copyOf(roles); }
    public boolean isActivo() { return activo; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
}
