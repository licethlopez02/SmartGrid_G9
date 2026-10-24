package co.edu.uptc.usuarios_service.usuarios.dominio;

public enum TipoRol implements Rol {
    USUARIO("Usuario / hogar medido"),
    OPERADOR("Operador de red");

    private final String descripcion;
    TipoRol(String descripcion) { this.descripcion = descripcion; }

    @Override public String nombre() { return name(); }
    @Override public String descripcion() { return descripcion; }
}