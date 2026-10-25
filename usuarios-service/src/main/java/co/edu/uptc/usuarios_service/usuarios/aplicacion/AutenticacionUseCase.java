package co.edu.uptc.usuarios_service.usuarios.aplicacion;

import co.edu.uptc.usuarios_service.usuarios.dominio.Usuario;

public interface AutenticacionUseCase {
    Usuario registrar(String nombre, String correo, String contrasena);
    TokenAcceso iniciarSesion(String correo, String contrasena);
}