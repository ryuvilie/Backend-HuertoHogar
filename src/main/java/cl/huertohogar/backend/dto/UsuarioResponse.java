package cl.huertohogar.backend.dto;

public class UsuarioResponse {

    private Long id;
    private String nombre;
    private String correo;
    private String rol;

    public UsuarioResponse() {}

    public UsuarioResponse(Long id, String nombre, String correo, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.rol = rol;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }

    public void setCorreo(String correo) { this.correo = correo; }

    public String getRol() { return rol; }

    public void setRol(String rol) { this.rol = rol; }
}
