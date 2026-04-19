package co.edu.uniquindio.proyectodb.dao;

import co.edu.uniquindio.proyectodb.db.ConexionDB;
import co.edu.uniquindio.proyectodb.model.Partido;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad Partido.
 */
public class PartidoDAO {

    private static final String COLS = "id_partido, fecha_hora, id_estadio, id_grupo, " +
            "id_equipo_local, id_equipo_visitante, goles_local, goles_visitante";

    private static final String SQL_INSERTAR = "INSERT INTO Partido (fecha_hora, id_estadio, id_grupo, id_equipo_local, id_equipo_visitante) "
            +
            "VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_BUSCAR_POR_ID = "SELECT " + COLS + "FROM Partido WHERE id_partido = ?";

    private static final String SQL_LISTAR_TODOS = "SELECT " + COLS + " FROM Partido ORDER BY fecha_hora";

    private static final String SQL_LISTAR_POR_GRUPO = "SELECT " + COLS
            + " FROM Partido WHERE id_grupo = ? ORDER BY fecha_hora";

    /*
     * Necesitamos partidos donde el equipo participe sin importar si es
     * local o visitante. La condición OR devuelve la fila si AL MENOS
     * una de las dos condiciones es verdadera.
     */
    private static final String SQL_LISTAR_POR_EQUIPO = "SELECT " + COLS + " FROM Partido " +
            "WHERE id_equipo_local = ? OR id_equipo_visitante = ? " +
            "ORDER BY fecha_hora";

    private static final String SQL_ACTUALIZAR_RESULTADO = "UPDATE Partido SET goles_local = ?, goles_visitante = ? WHERE id_partido = ?";

    private static final String SQL_ELIMINAR = "DELETE FROM Partido WHERE id_partido = ?";

    /*
     * CREATE
     */

    /**
     * Inserta un nuevo partido en la base de datos.
     *
     * @param partido objeto Partido con todos los campos requeridos
     * @return true si la inserción fue exitosa
     */

    public boolean insertar(Partido partido) {
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS);

            ps.setTimestamp(1, Timestamp.valueOf(partido.getFechaHora()));
            ps.setInt(2, partido.getIdEstadio());
            ps.setInt(3, partido.getIdGrupo());
            ps.setInt(4, partido.getIdEquipoLocal());
            ps.setInt(5, partido.getIdEquipoVisitante());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next())
                    partido.setIdPartido(keys.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[PartidoDAO] Error al insertar: " + e.getMessage());
        }
        return false;
    }

    /*
     * READ
     */

    /**
     * Busca un partido por su ID.
     *
     * @param idPartido ID del partido a buscar
     * @return objeto Partido si se encuentra, null si no
     */

    public Partido buscarPorId(int idPartido) {
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_ID);
            ps.setInt(1, idPartido);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return mapearResultSet(rs);
        } catch (SQLException e) {
            System.err.println("[PartidoDAO] Error al buscar: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista todos los partidos de la base de datos.
     *
     * @return lista de partidos
     */

    public List<Partido> listarTodos() {
        List<Partido> lista = new ArrayList<>();
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            ResultSet rs = con.prepareStatement(SQL_LISTAR_TODOS).executeQuery();
            while (rs.next())
                lista.add(mapearResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[PartidoDAO] Error al listar: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lista partidos filtrados por grupo.
     *
     * @param idGrupo ID del grupo
     * @return lista de partidos
     */

    public List<Partido> listarPorGrupo(int idGrupo) {
        List<Partido> lista = new ArrayList<>();
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_LISTAR_POR_GRUPO);
            ps.setInt(1, idGrupo);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                lista.add(mapearResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[PartidoDAO] Error al listar por grupo: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lista todos los partidos en que participa un equipo (como local o visitante).
     *
     * El mismo idEquipo se pasa dos veces porque hay dos '?' en el SQL
     * (uno para id_equipo_local y otro para id_equipo_visitante).
     *
     * @param idEquipo ID del equipo
     * @return lista de partidos
     */
    public List<Partido> listarPorEquipo(int idEquipo) {
        List<Partido> lista = new ArrayList<>();
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_LISTAR_POR_EQUIPO);
            ps.setInt(1, idEquipo); // para id_equipo_local
            ps.setInt(2, idEquipo); // para id_equipo_visitante
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                lista.add(mapearResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[PartidoDAO] Error al listar por equipo: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Actualiza solo el resultado (goles) de un partido ya existente.
     *
     * ACTUALIZACIÓN PARCIAL:
     * Este método solo toca goles_local y goles_visitante, dejando
     * intactos fecha, estadio, etc.
     *
     * @param idPartido      ID del partido
     * @param golesLocal     Goles del equipo local
     * @param golesVisitante Goles del equipo visitante
     * @return true si la actualización fue exitosa
     */
    public boolean actualizarResultado(int idPartido, int golesLocal, int golesVisitante) {
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_ACTUALIZAR_RESULTADO);
            ps.setInt(1, golesLocal);
            ps.setInt(2, golesVisitante);
            ps.setInt(3, idPartido);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PartidoDAO] Error al actualizar resultado: " + e.getMessage());
        }
        return false;
    }

    /*
     * DELETE
     */

    /**
     * Elimina un partido de la base de datos.
     *
     * @param idPartido ID del partido a eliminar
     * @return true si la eliminación fue exitosa
     */

    public boolean eliminar(int idPartido) {
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_ELIMINAR);
            ps.setInt(1, idPartido);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PartidoDAO] Error al eliminar: " + e.getMessage());
        }
        return false;
    }

    /*
     * MAPEO
     */

    /**
     * Convierte una fila de ResultSet a objeto Partido.
     *
     * @param rs ResultSet con los datos del partido
     * @return objeto Partido
     */

    private Partido mapearResultSet(ResultSet rs) throws SQLException {
        Partido p = new Partido();
        p.setIdPartido(rs.getInt("id_partido"));

        // java.sql.Timestamp → java.time.LocalDateTime
        p.setFechaHora(rs.getTimestamp("fecha_hora").toLocalDateTime());

        p.setIdEstadio(rs.getInt("id_estadio"));
        p.setIdGrupo(rs.getInt("id_grupo"));
        p.setIdEquipoLocal(rs.getInt("id_equipo_local"));
        p.setIdEquipoVisitante(rs.getInt("id_equipo_visitante"));
        p.setGolesLocal(rs.getInt("goles_local"));
        p.setGolesVisitante(rs.getInt("goles_visitante"));
        return p;
    }
}
