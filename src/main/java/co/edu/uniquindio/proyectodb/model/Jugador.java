package co.edu.uniquindio.proyectodb.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Jugador {

    private int idJugador;
    private String nombre;
    private LocalDate fechaNacimiento;
    private String posicion;
    private BigDecimal peso;
    private BigDecimal estatura;
    private BigDecimal valorMercado;
    private int idEquipo;

    // Referencia de objeto
    private Equipo equipo;

    public Jugador() {
    }

    public Jugador(int idJugador, String nombre, LocalDate fechaNacimiento, String posicion,
            BigDecimal peso, BigDecimal estatura, BigDecimal valorMercado, int idEquipo) {
        this.idJugador = idJugador;
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.posicion = posicion;
        this.peso = peso;
        this.estatura = estatura;
        this.valorMercado = valorMercado;
        this.idEquipo = idEquipo;
    }

    public int getIdJugador() {
        return idJugador;
    }

    public void setIdJugador(int idJugador) {
        this.idJugador = idJugador;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getPosicion() {
        return posicion;
    }

    public void setPosicion(String posicion) {
        this.posicion = posicion;
    }

    public BigDecimal getPeso() {
        return peso;
    }

    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }

    public BigDecimal getEstatura() {
        return estatura;
    }

    public void setEstatura(BigDecimal estatura) {
        this.estatura = estatura;
    }

    public BigDecimal getValorMercado() {
        return valorMercado;
    }

    public void setValorMercado(BigDecimal valorMercado) {
        this.valorMercado = valorMercado;
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
        return "Jugador{id=" + idJugador + ", nombre='" + nombre + "', posicion='" + posicion + "'}";
    }
}
