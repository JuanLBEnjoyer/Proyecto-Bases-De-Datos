package co.edu.uniquindio.proyectodb.model;

public class Estadio {

    private int idEstadio;
    private String nombre;
    private int capacidad;
    private int idCiudad;

    // Referencia de objeto
    private Ciudad ciudad;

    public Estadio() {
    }

    public Estadio(int idEstadio, String nombre, int capacidad, int idCiudad) {
        this.idEstadio = idEstadio;
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.idCiudad = idCiudad;
    }

    public int getIdEstadio() {
        return idEstadio;
    }

    public void setIdEstadio(int idEstadio) {
        this.idEstadio = idEstadio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public int getIdCiudad() {
        return idCiudad;
    }

    public void setIdCiudad(int idCiudad) {
        this.idCiudad = idCiudad;
    }

    public Ciudad getCiudad() {
        return ciudad;
    }

    public void setCiudad(Ciudad ciudad) {
        this.ciudad = ciudad;
    }

    @Override
    public String toString() {
        return "Estadio{id=" + idEstadio + ", nombre='" + nombre + "', capacidad=" + capacidad + "}";
    }
}
