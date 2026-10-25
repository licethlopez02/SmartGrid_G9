package co.edu.uptc.usuarios_service.usuarios.dominio;

import co.edu.uptc.usuarios_service.compartido.NegocioException;

public class UsuarioNoEncontradoException extends NegocioException {
    public UsuarioNoEncontradoException(Long id) { super("No existe el usuario con id " + id, 404); }
}