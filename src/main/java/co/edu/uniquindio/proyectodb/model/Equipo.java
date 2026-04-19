package co.edu.uniquindio.proyectodb.model;

import java.math.BigDecimal;

public class Equipo {

    private int idEquipo;
    private String nombre;
    private String pais;
    private BigDecimal valorTotalEquipo;
    private int idConfederacion;

    // Referencia de objeto
    private Confederacion confederacion;

    public Equipo() {
    }

    public Equipo(int idEquipo, String nombre, String pais, BigDecimal valorTotalEquipo, int idConfederacion) {
        this.idEquipo = idEquipo;
        this.nombre = nombre;
        this.pais = pais;
        this.valorTotalEquipo = valorTotalEquipo;
        this.idConfederacion = idConfederacion;
    }

    public int getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(int idEquipo) {
        this.idEquipo = idEquipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public BigDecimal getValorTotalEquipo() {
        return valorTotalEquipo;
    }

    public void setValorTotalEquipo(BigDecimal valorTotalEquipo) {
        this.valorTotalEquipo = valorTotalEquipo;
    }

    public int getIdConfederacion() {
        return idConfederacion;
    }

    public void setIdConfederacion(int idConfederacion) {
        this.idConfederacion = idConfederacion;
    }

    public Confederacion getConfederacion() {
        return confederacion;
    }

    public void setConfederacion(Confederacion confederacion) {
        this.confederacion = confederacion;
    }

    @Override
    public String toString() {
        return "Equipo{id=" + idEquipo + ", nombre='" + nombre + "', pais='" + pais + "'}";
    }
}
