package co.edu.uniquindio.proyectodb.model;

import java.time.LocalDateTime;

import co.edu.uniquindio.proyectodb.model.valueobjects.TipoUsuario;

public class Usuario {

    private int idUsuario;
    private String nombreUsuario;
    private String contrasenaHash;
    private TipoUsuario tipoUsuario;
    private LocalDateTime fechaCreacion;

    public Usuario() {
    }

    public Usuario(int idUsuario, String nombreUsuario, String contrasenaHash,
            TipoUsuario tipoUsuario, LocalDateTime fechaCreacion) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.contrasenaHash = contrasenaHash;
        this.tipoUsuario = tipoUsuario;
        this.fechaCreacion = fechaCreacion;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getContrasenaHash() {
        return contrasenaHash;
    }

    public void setContrasenaHash(String contrasenaHash) {
        this.contrasenaHash = contrasenaHash;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @Override
    public String toString() {
        return "Usuario{id=" + idUsuario + ", usuario='" + nombreUsuario + "', tipo=" + tipoUsuario + "}";
    }
}
