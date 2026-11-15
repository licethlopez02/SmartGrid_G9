package co.edu.uptc.usuarios_service.usuarios.infraestructura.salida.correo;

import co.edu.uptc.usuarios_service.usuarios.aplicacion.EnviadorCorreo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EnviadorCorreoSmtp implements EnviadorCorreo {

    private final JavaMailSender mailSender;
    private final String remitente;

    public EnviadorCorreoSmtp(JavaMailSender mailSender,
                              @Value("${spring.mail.username:}") String remitente) {
        this.mailSender = mailSender;
        this.remitente = remitente;
    }

    @Override
    public void enviar(String destinatario, String asunto, String texto) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        if (remitente != null && !remitente.isBlank()) {
            mensaje.setFrom(remitente);
        }
        mensaje.setTo(destinatario);
        mensaje.setSubject(asunto);
        mensaje.setText(texto);
        mailSender.send(mensaje);
    }
}
