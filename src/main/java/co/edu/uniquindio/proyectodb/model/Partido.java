package co.edu.uniquindio.proyectodb.model;

import java.time.LocalDateTime;

public class Partido {

    private int idPartido;
    private LocalDateTime fechaHora;
    private int idEstadio;
    private int idGrupo;
    private int idEquipoLocal;
    private int idEquipoVisitante;
    private int golesLocal;
    private int golesVisitante;

    // Referencias de objeto
    private Estadio estadio;
    private Grupo grupo;
    private Equipo equipoLocal;
    private Equipo equipoVisitante;

    public Partido() {
    }

    public Partido(int idPartido, LocalDateTime fechaHora, int idEstadio, int idGrupo,
            int idEquipoLocal, int idEquipoVisitante, int golesLocal, int golesVisitante) {
        this.idPartido = idPartido;
        this.fechaHora = fechaHora;
        this.idEstadio = idEstadio;
        this.idGrupo = idGrupo;
        this.idEquipoLocal = idEquipoLocal;
        this.idEquipoVisitante = idEquipoVisitante;
        this.golesLocal = golesLocal;
        this.golesVisitante = golesVisitante;
    }

    public int getIdPartido() {
        return idPartido;
    }

    public void setIdPartido(int idPartido) {
        this.idPartido = idPartido;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public int getIdEstadio() {
        return idEstadio;
    }

    public void setIdEstadio(int idEstadio) {
        this.idEstadio = idEstadio;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    public int getIdEquipoLocal() {
        return idEquipoLocal;
    }

    public void setIdEquipoLocal(int idEquipoLocal) {
        this.idEquipoLocal = idEquipoLocal;
    }

    public int getIdEquipoVisitante() {
        return idEquipoVisitante;
    }

    public void setIdEquipoVisitante(int idEquipoVisitante) {
        this.idEquipoVisitante = idEquipoVisitante;
    }

    public int getGolesLocal() {
        return golesLocal;
    }

    public void setGolesLocal(int golesLocal) {
        this.golesLocal = golesLocal;
    }

    public int getGolesVisitante() {
        return golesVisitante;
    }

    public void setGolesVisitante(int golesVisitante) {
        this.golesVisitante = golesVisitante;
    }

    public Estadio getEstadio() {
        return estadio;
    }

    public void setEstadio(Estadio estadio) {
        this.estadio = estadio;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public Equipo getEquipoLocal() {
        return equipoLocal;
    }

    public void setEquipoLocal(Equipo equipoLocal) {
        this.equipoLocal = equipoLocal;
    }

    public Equipo getEquipoVisitante() {
        return equipoVisitante;
    }

    public void setEquipoVisitante(Equipo equipoVisitante) {
        this.equipoVisitante = equipoVisitante;
    }

    @Override
    public String toString() {
        return "Partido{id=" + idPartido + ", local=" + idEquipoLocal +
                " vs visitante=" + idEquipoVisitante + ", fecha=" + fechaHora + "}";
    }
}
