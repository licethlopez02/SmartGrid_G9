package co.edu.uptc.usuarios_service.usuarios.infraestructura.entrada.web;

public record TokenResponse(String token, String tipo, long expiraEnSegundos) {

}