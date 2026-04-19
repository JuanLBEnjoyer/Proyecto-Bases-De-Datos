package co.edu.uniquindio.proyectodb.model;

import java.time.LocalDateTime;

public class Bitacora {

    private int idRegistro;
    private int idUsuario;
    private LocalDateTime fechaHoraIngreso;
    private LocalDateTime fechaHoraSalida; // puede ser null (sesión activa)

    // Referencia de objeto
    private Usuario usuario;

    public Bitacora() {
    }

    public Bitacora(int idRegistro, int idUsuario,
            LocalDateTime fechaHoraIngreso, LocalDateTime fechaHoraSalida) {
        this.idRegistro = idRegistro;
        this.idUsuario = idUsuario;
        this.fechaHoraIngreso = fechaHoraIngreso;
        this.fechaHoraSalida = fechaHoraSalida;
    }

    public int getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(int idRegistro) {
        this.idRegistro = idRegistro;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public LocalDateTime getFechaHoraIngreso() {
        return fechaHoraIngreso;
    }

    public void setFechaHoraIngreso(LocalDateTime fechaHoraIngreso) {
        this.fechaHoraIngreso = fechaHoraIngreso;
    }

    public LocalDateTime getFechaHoraSalida() {
        return fechaHoraSalida;
    }

    public void setFechaHoraSalida(LocalDateTime fechaHoraSalida) {
        this.fechaHoraSalida = fechaHoraSalida;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    // Retorna true si la sesión sigue activa
    public boolean isSesionActiva() {
        return fechaHoraSalida == null;
    }

    @Override
    public String toString() {
        return "Bitacora{id=" + idRegistro + ", idUsuario=" + idUsuario +
                ", ingreso=" + fechaHoraIngreso + ", salida=" + fechaHoraSalida + "}";
    }
}
