package co.edu.uptc.usuarios_service.usuarios.dominio;

import co.edu.uptc.usuarios_service.compartido.NegocioException;

public class ReglaDeUsuarioException extends NegocioException {
    public ReglaDeUsuarioException(String mensaje) { super(mensaje, 400); }
}