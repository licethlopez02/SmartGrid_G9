package co.edu.uptc.usuarios_service.usuarios.infraestructura.salida.persistencia;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import co.edu.uptc.usuarios_service.usuarios.aplicacion.RepositorioUsuarios;
import co.edu.uptc.usuarios_service.usuarios.dominio.TipoRol;
import co.edu.uptc.usuarios_service.usuarios.dominio.Usuario;

@Repository
public class UsuarioRepositoryJpaAdapter implements RepositorioUsuarios {
    private final UsuarioRepository jpa;

    public UsuarioRepositoryJpaAdapter(UsuarioRepository jpa) { this.jpa = jpa; }

    @Override 
    public Optional<Usuario> buscarPorId(Long id) { 
        return jpa.findById(id); 
    }
    
    @Override 
    public Optional<Usuario> buscarPorCorreo(String correo) { 
        return jpa.findByCorreo(correo); 
    }
    
    @Override public boolean existePorCorreo(String correo) { 
        return jpa.existsByCorreo(correo); 
    }
    
    @Override public boolean existeConRol(TipoRol rol) { 
        return jpa.existeConRol(rol); 
    }
    
    @Override public List<Usuario> listar() { 
        return jpa.findAll(); 
    }
    
    @Override public Usuario guardar(Usuario usuario) { 
        return jpa.save(usuario); 
    }
}