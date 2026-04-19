package co.edu.uniquindio.proyectodb.model;

public class EquipoGrupo {

    private int idEquipoGrupo;
    private int idEquipo;
    private int idGrupo;

    // Referencias de objeto
    private Equipo equipo;
    private Grupo grupo;

    public EquipoGrupo() {
    }

    public EquipoGrupo(int idEquipoGrupo, int idEquipo, int idGrupo) {
        this.idEquipoGrupo = idEquipoGrupo;
        this.idEquipo = idEquipo;
        this.idGrupo = idGrupo;
    }

    public int getIdEquipoGrupo() {
        return idEquipoGrupo;
    }

    public void setIdEquipoGrupo(int idEquipoGrupo) {
        this.idEquipoGrupo = idEquipoGrupo;
    }

    public int getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(int idEquipo) {
        this.idEquipo = idEquipo;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    @Override
    public String toString() {
        return "EquipoGrupo{idEquipo=" + idEquipo + ", idGrupo=" + idGrupo + "}";
    }
}
