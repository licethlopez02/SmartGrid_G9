package co.edu.uptc.usuarios_service.usuarios.dominio;

public interface Rol {
    String nombre();
    String descripcion();
    default String autoridad() { return "ROLE_" + nombre(); }
}