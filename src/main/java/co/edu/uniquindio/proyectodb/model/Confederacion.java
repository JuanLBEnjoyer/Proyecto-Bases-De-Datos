package co.edu.uniquindio.proyectodb.model;

public class Confederacion {

    private int idConfederacion;
    private String nombre;
    private String siglas;

    public Confederacion() {
    }

    public Confederacion(int idConfederacion, String nombre, String siglas) {
        this.idConfederacion = idConfederacion;
        this.nombre = nombre;
        this.siglas = siglas;
    }

    public int getIdConfederacion() {
        return idConfederacion;
    }

    public void setIdConfederacion(int idConfederacion) {
        this.idConfederacion = idConfederacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSiglas() {
        return siglas;
    }

    public void setSiglas(String siglas) {
        this.siglas = siglas;
    }

    @Override
    public String toString() {
        return "Confederacion{id=" + idConfederacion + ", siglas='" + siglas + "', nombre='" + nombre + "'}";
    }
}