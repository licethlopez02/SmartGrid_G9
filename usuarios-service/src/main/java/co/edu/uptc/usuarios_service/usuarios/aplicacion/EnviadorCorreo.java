package co.edu.uptc.usuarios_service.usuarios.aplicacion;

public interface EnviadorCorreo {

    void enviar(String destinatario, String asunto, String texto);
}
