package co.edu.uniquindio.proyectodb.model;

import java.time.LocalDate;

public class DirectorTecnico {

    private int idDt;
    private String nombre;
    private String nacionalidad;
    private LocalDate fechaNacimiento;
    private int idEquipo;

    // Referencia de objeto
    private Equipo equipo;

    public DirectorTecnico() {
    }

    public DirectorTecnico(int idDt, String nombre, String nacionalidad,
            LocalDate fechaNacimiento, int idEquipo) {
        this.idDt = idDt;
        this.nombre = nombre;
        this.nacionalidad = nacionalidad;
        this.fechaNacimiento = fechaNacimiento;
        this.idEquipo = idEquipo;
    }

    public int getIdDt() {
        return idDt;
    }

    public void setIdDt(int idDt) {
        this.idDt = idDt;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public int getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(int idEquipo) {
        this.idEquipo = idEquipo;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    @Override
    public String toString() {
        return "DirectorTecnico{id=" + idDt + ", nombre='" + nombre + "', idEquipo=" + idEquipo + "}";
    }
}