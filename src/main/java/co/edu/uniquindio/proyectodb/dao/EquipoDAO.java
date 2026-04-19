package co.edu.uniquindio.proyectodb.dao;

import co.edu.uniquindio.proyectodb.db.ConexionDB;
import co.edu.uniquindio.proyectodb.model.Equipo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad Equipo.
 *
 * Incluye la consulta : listarPorConfederacion.
 */
public class EquipoDAO {

    // CONSTANTES SQL

    private static final String COLS = "id_equipo, nombre, pais, valor_total_equipo, id_confederacion";

    private static final String SQL_INSERTAR = "INSERT INTO Equipo (nombre, pais, valor_total_equipo, id_confederacion) VALUES (?, ?, ?, ?)";

    private static final String SQL_BUSCAR_POR_ID = "SELECT " + COLS + " FROM Equipo WHERE id_equipo = ?";

    private static final String SQL_LISTAR_TODOS = "SELECT " + COLS + " FROM Equipo ORDER BY nombre";

    private static final String SQL_LISTAR_POR_CONFEDERACION = "SELECT " + COLS
            + " FROM Equipo WHERE id_confederacion = ? ORDER BY nombre";

    private static final String SQL_ACTUALIZAR = "UPDATE Equipo SET nombre = ?, pais = ?, valor_total_equipo = ?, id_confederacion = ? "
            +
            "WHERE id_equipo = ?";

    private static final String SQL_ELIMINAR = "DELETE FROM Equipo WHERE id_equipo = ?";
    /*
     * CREATE
     */

    /**
     * Inserta un nuevo equipo en la base de datos.
     *
     * @param equipo objeto Equipo con todos los campos requeridos
     * @return true si la inserción fue exitosa
     */

    public boolean insertar(Equipo equipo) {
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, equipo.getNombre());
            ps.setString(2, equipo.getPais());
            ps.setBigDecimal(3, equipo.getValorTotalEquipo());
            ps.setInt(4, equipo.getIdConfederacion());
            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next())
                    equipo.setIdEquipo(keys.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[EquipoDAO] Error al insertar: " + e.getMessage());
        }
        return false;
    }

    /*
     * READ
     *
     * /**
     * Busca un equipo por su ID.
     *
     * @param idEquipo ID del equipo a buscar
     * 
     * @return objeto Equipo si se encuentra, null si no
     */

    public Equipo buscarPorId(int idEquipo) {
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_ID);
            ps.setInt(1, idEquipo);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return mapearResultSet(rs);
        } catch (SQLException e) {
            System.err.println("[EquipoDAO] Error al buscar: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista todos los equipos de la base de datos.
     *
     * @return lista de equipos
     */

    public List<Equipo> listarTodos() {
        List<Equipo> lista = new ArrayList<>();
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            ResultSet rs = con.prepareStatement(SQL_LISTAR_TODOS).executeQuery();
            while (rs.next())
                lista.add(mapearResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[EquipoDAO] Error al listar: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lista equipos filtrados por confederación.
     *
     * Se filtra por ID y no por nombre de confederación para evitar
     * problemas con tildes, mayúsculas o espacios.
     *
     * @param idConfederacion ID de la confederación
     * @return lista de equipos
     */
    public List<Equipo> listarPorConfederacion(int idConfederacion) {
        List<Equipo> lista = new ArrayList<>();
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_LISTAR_POR_CONFEDERACION);
            ps.setInt(1, idConfederacion);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                lista.add(mapearResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[EquipoDAO] Error al listar por confederación: " + e.getMessage());
        }
        return lista;
    }

    /*
     * UPDATE
     */

    /**
     * Actualiza un equipo existente en la base de datos.
     *
     * @param equipo objeto Equipo con todos los campos requeridos
     * @return true si la actualización fue exitosa
     */

    public boolean actualizar(Equipo equipo) {
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_ACTUALIZAR);
            ps.setString(1, equipo.getNombre());
            ps.setString(2, equipo.getPais());
            ps.setBigDecimal(3, equipo.getValorTotalEquipo());
            ps.setInt(4, equipo.getIdConfederacion());
            ps.setInt(5, equipo.getIdEquipo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[EquipoDAO] Error al actualizar: " + e.getMessage());
        }
        return false;
    }

    /*
     * DELETE
     */

    /**
     * Elimina un equipo de la base de datos.
     *
     * @param idEquipo ID del equipo a eliminar
     * @return true si la eliminación fue exitosa
     */

    public boolean eliminar(int idEquipo) {
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_ELIMINAR);
            ps.setInt(1, idEquipo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[EquipoDAO] Error al eliminar: " + e.getMessage());
        }
        return false;
    }

    /*
     * MAPEO
     */

    /**
     * Convierte un ResultSet a un objeto Equipo.
     *
     * @param rs ResultSet con los datos del equipo
     * @return objeto Equipo
     */

    private Equipo mapearResultSet(ResultSet rs) throws SQLException {
        Equipo e = new Equipo();
        e.setIdEquipo(rs.getInt("id_equipo"));
        e.setNombre(rs.getString("nombre"));
        e.setPais(rs.getString("pais"));
        e.setValorTotalEquipo(rs.getBigDecimal("valor_total_equipo"));
        e.setIdConfederacion(rs.getInt("id_confederacion"));
        return e;
    }
}