package co.edu.uptc.usuarios_service.usuarios.aplicacion;

import co.edu.uptc.usuarios_service.usuarios.dominio.Usuario;

public interface EmisorTokens {
    TokenAcceso emitir(Usuario usuario);
}