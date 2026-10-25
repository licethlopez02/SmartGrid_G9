package co.edu.uptc.usuarios_service.usuarios.dominio;

import co.edu.uptc.usuarios_service.compartido.NegocioException;

public class CredencialesInvalidasException extends NegocioException {
    public CredencialesInvalidasException() { super("Credenciales inválidas", 401); }
}