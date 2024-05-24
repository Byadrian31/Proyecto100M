package montaditos;

public class Usuario {
    private String id;
    private String nombre;
    private String correo;
    private boolean trabajador;

    public Usuario(String id, String nombre, String correo,  boolean trabajador) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.trabajador = trabajador;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isTrabajador() {
        return trabajador;
    }

    public void setTrabajador(boolean trabajador) {
        this.trabajador = trabajador;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
