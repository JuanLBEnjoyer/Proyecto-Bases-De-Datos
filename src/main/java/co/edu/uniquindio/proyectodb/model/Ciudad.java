package co.edu.uniquindio.proyectodb.model;

public class Ciudad {

    private int idCiudad;
    private String nombre;
    private int idPaisAnfitrion;

    // Referencia de objeto
    private PaisAnfitrion paisAnfitrion;

    public Ciudad() {
    }

    public Ciudad(int idCiudad, String nombre, int idPaisAnfitrion) {
        this.idCiudad = idCiudad;
        this.nombre = nombre;
        this.idPaisAnfitrion = idPaisAnfitrion;
    }

    public int getIdCiudad() {
        return idCiudad;
    }

    public void setIdCiudad(int idCiudad) {
        this.idCiudad = idCiudad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getIdPaisAnfitrion() {
        return idPaisAnfitrion;
    }

    public void setIdPaisAnfitrion(int idPaisAnfitrion) {
        this.idPaisAnfitrion = idPaisAnfitrion;
    }

    public PaisAnfitrion getPaisAnfitrion() {
        return paisAnfitrion;
    }

    public void setPaisAnfitrion(PaisAnfitrion paisAnfitrion) {
        this.paisAnfitrion = paisAnfitrion;
    }

    @Override
    public String toString() {
        return "Ciudad{id=" + idCiudad + ", nombre='" + nombre + "', idPais=" + idPaisAnfitrion + "}";
    }
}