package co.edu.uptc.usuarios_service.usuarios.dominio;

import co.edu.uptc.usuarios_service.compartido.NegocioException;

public class CorreoYaRegistradoException extends NegocioException {
    public CorreoYaRegistradoException(String correo) {
        super("El correo " + correo + " ya está registrado", 409);
    }
}
