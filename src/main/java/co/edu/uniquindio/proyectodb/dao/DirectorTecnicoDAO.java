package co.edu.uniquindio.proyectodb.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import co.edu.uniquindio.proyectodb.db.ConexionDB;
import co.edu.uniquindio.proyectodb.model.DirectorTecnico;

/**
 * DAO para la entidad DirectorTecnico
 */

public class DirectorTecnicoDAO {

    // Constantes SQL

    private static final String COLS = "nombre, fecha_nacimiento, nacionalidad, id_equipo";

    private static final String SQL_INSERTAR = "INSERT INTO DirectorTecnico (nombre, fecha_nacimiento, nacionalidad, id_equipo) VALUES (?, ?, ?, ?)";

    private static final String SQL_BUSCAR_POR_ID = "SELECT" + COLS
            + "FROM DirectorTecnico WHERE id_director_tecnico = ?";

    private static final String SQL_LISTAR_TODOS = "SELECT" + COLS + "FROM DirectorTecnico ORDER BY nombre";

    private static final String SQL_LISTAR_POR_EQUIPO = "SELECT" + COLS +
            " FROM DirectorTecnico WHERE id_equipo = ? ORDER BY nombre";

    private static final String SQL_LISTAR_POR_NACIONALIDAD = "SELECT " + COLS
            + "FROM DirectorTecnico WHERE nacionalidad = ? ORDER BY nombre";

    private static final String SQL_LISTAR_MAYORES_DE = "SELECT" + COLS
            + "FROM DirectorTecnico WHERE DATEDIFF(year,fecha_nacimiento,GETDATE()) > ?"
            + "ORDER BY fecha_nacimiento ASC";

    private static final String SQL_LISTAR_MENORES_DE = "SELECT" + COLS
            + "FROM DirectorTecnico WHERE DATEDIFF(year,fecha_nacimiento,GETDATE()) < ?"
            + "ORDER BY fecha_nacimiento DESC";

    private static final String SQL_ACTUALIZAR = "UPDATE DirectorTecnico SET nombre = ?, fecha_nacimiento = ?, nacionalidad = ?, id_equipo = ? WHERE id_director_tecnico = ?";

    private static final String SQL_ELIMINAR = "DELETE FROM DirectorTecnico WHERE id_director_tecnico = ?";

    // CREATE

    /**
     * Inserta un nuevo director técnico en la base de datos.
     * 
     * @param directorTecnico objeto DirectorTecnico con todos los campos requeridos
     * @return true si la inserción fue exitosa
     */

    public boolean insertar(DirectorTecnico directorTecnico) {
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(
                    SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, directorTecnico.getNombre());
            ps.setDate(2, Date.valueOf(directorTecnico.getFechaNacimiento()));
            ps.setString(3, directorTecnico.getNacionalidad());
            ps.setInt(4, directorTecnico.getIdEquipo());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    directorTecnico.setIdDt(keys.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("[DirectorTecnicoDAO] Error al insertar: " + e.getMessage());
        }
        return false;
    }

    // READ

    /**
     * Busca un director técnico por su ID.
     * 
     * @param idDt ID del director técnico a buscar
     * @return objeto DirectorTecnico si se encuentra, null si no
     */

    public DirectorTecnico buscarPorId(int idDt) {
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_ID);
            ps.setInt(1, idDt);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("[DirectorTecnicoDAO] Error al buscar: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista todos los directores técnicos de la base de datos.
     * 
     * @return lista de directores técnicos
     */

    public List<DirectorTecnico> listarTodos() {
        List<DirectorTecnico> lista = new ArrayList<>();
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            ResultSet rs = con.prepareStatement(SQL_LISTAR_TODOS).executeQuery();
            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("[DirectorTecnicoDAO] Error al listar: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lista directores técnicos por equipo.
     * 
     * @param idEquipo ID del equipo
     * @return lista de directores técnicos
     */

    public List<DirectorTecnico> listarPorEquipo(int idEquipo) {
        List<DirectorTecnico> lista = new ArrayList<>();
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_LISTAR_POR_EQUIPO);
            ps.setInt(1, idEquipo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("[DirectorTecnicoDAO] Error al listar por equipo: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lista directores técnicos por nacionalidad.
     * 
     * @param nacionalidad nacionalidad del director técnico
     * @return lista de directores técnicos
     */

    public List<DirectorTecnico> listarPorNacionalidad(String nacionalidad) {
        List<DirectorTecnico> lista = new ArrayList<>();
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_LISTAR_POR_NACIONALIDAD);
            ps.setString(1, nacionalidad);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("[DirectorTecnicoDAO] Error al listar por nacionalidad: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lista directores técnicos mayores de una edad determinada.
     * 
     * @param edad edad mínima
     * @return lista de directores técnicos
     */

    public List<DirectorTecnico> listarMayoresDe(int edad) {
        List<DirectorTecnico> lista = new ArrayList<>();
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_LISTAR_MAYORES_DE);
            ps.setInt(1, edad);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("[DirectorTecnicoDAO] Error al listar mayores de: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lista directores técnicos menores de una edad determinada.
     * 
     * @param edad edad máxima
     * @return lista de directores técnicos
     */

    public List<DirectorTecnico> listarMenoresDe(int edad) {
        List<DirectorTecnico> lista = new ArrayList<>();
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_LISTAR_MENORES_DE);
            ps.setInt(1, edad);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("[DirectorTecnicoDAO] Error al listar menores de: " + e.getMessage());
        }
        return lista;
    }

    // UPDATE

    /**
     * Actualiza un director técnico en la base de datos.
     * 
     * @param directorTecnico objeto DirectorTecnico con todos los campos requeridos
     * @return true si la actualización fue exitosa
     */

    public boolean actualizar(DirectorTecnico directorTecnico) {
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_ACTUALIZAR);
            ps.setString(1, directorTecnico.getNombre());
            ps.setDate(2, Date.valueOf(directorTecnico.getFechaNacimiento()));
            ps.setString(3, directorTecnico.getNacionalidad());
            ps.setInt(4, directorTecnico.getIdEquipo());
            ps.setInt(5, directorTecnico.getIdDt());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DirectorTecnicoDAO] Error al actualizar: " + e.getMessage());
            return false;
        }
    }

    // DELETE

    /**
     * Elimina un director técnico de la base de datos.
     * 
     * @param idDt ID del director técnico a eliminar
     * @return true si la eliminación fue exitosa
     */

    public boolean eliminar(int idDt) {
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_ELIMINAR);
            ps.setInt(1, idDt);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DirectorTecnicoDAO] Error al eliminar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Mapea un ResultSet a un objeto DirectorTecnico.
     * 
     * @param rs ResultSet con los datos del director técnico
     * @return objeto DirectorTecnico
     */

    private DirectorTecnico mapearResultSet(ResultSet rs) throws SQLException {
        DirectorTecnico dt = new DirectorTecnico();
        dt.setIdDt(rs.getInt("id_dt"));
        dt.setNombre(rs.getString("nombre"));
        dt.setFechaNacimiento(rs.getDate("fecha_nacimiento").toLocalDate());
        dt.setNacionalidad(rs.getString("nacionalidad"));
        dt.setIdEquipo(rs.getInt("id_equipo"));
        return dt;
    }

}
