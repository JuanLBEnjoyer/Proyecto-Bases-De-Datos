package co.edu.uniquindio.proyectodb.dao;

import co.edu.uniquindio.proyectodb.db.ConexionDB;
import co.edu.uniquindio.proyectodb.model.Confederacion;
import java.sql.*;
import java.util.*;

/**
 * DAO para la entidad Confederacion.
 */

public class ConfederacionDAO {

    // CONSTANTES SQL

    private static final String SQL_INSERTAR = "INSERT INTO Confederacion (nombre, siglas) VALUES (?, ?)";
    private static final String SQL_BUSCAR_POR_ID = "SELECT id_confederacion, nombre, siglas FROM Confederacion WHERE id_confederacion = ?";
    private static final String SQL_LISTAR_TODOS = "SELECT id_confederacion, nombre, siglas FROM Confederacion ORDER BY siglas";
    private static final String SQL_ACTUALIZAR = "UPDATE Confederacion SET nombre = ?, siglas = ? WHERE id_confederacion = ?";
    private static final String SQL_ELIMINAR = "DELETE FROM Confederacion WHERE id_confederacion = ?";

    /**
     * Inserta una nueva confederación en la base de datos.
     * 
     * @param confederacion Confederacion a insertar
     * @return true si se insertó correctamente, false en caso contrario
     */
    public boolean insertar(Confederacion confederacion) {
        try {
            PreparedStatement ps = ConexionDB.getInstancia().getConnection()
                    .prepareStatement(SQL_INSERTAR,
                            Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, confederacion.getNombre());
            ps.setString(2, confederacion.getSiglas());
            if (ps.executeUpdate() > 0) {
                ResultSet k = ps.getGeneratedKeys();
                if (k.next())
                    confederacion.setIdConfederacion(k.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[ConfederacionDAO] " + e.getMessage());
        }
        return false;
    }

    /**
     * Busca una confederación por su ID.
     * 
     * @param idConfederacion ID de la confederación
     * @return Confederacion encontrada o null si no existe
     */
    public Confederacion buscarPorId(int idConfederacion) {
        try {
            PreparedStatement ps = ConexionDB.getInstancia().getConnection()
                    .prepareStatement(SQL_BUSCAR_POR_ID);
            ps.setInt(1, idConfederacion);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return mapearResultSet(rs);
        } catch (SQLException e) {
            System.err.println("[ConfederacionDAO] " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista todas las confederaciones de la base de datos.
     * 
     * @return Lista de confederaciones
     */

    public List<Confederacion> listarTodos() {
        List<Confederacion> lista = new ArrayList<>();
        try {
            ResultSet rs = ConexionDB.getInstancia().getConnection()
                    .prepareStatement(SQL_LISTAR_TODOS)
                    .executeQuery();
            while (rs.next())
                lista.add(mapearResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[ConfederacionDAO] " + e.getMessage());
        }
        return lista;
    }

    /**
     * Actualiza una confederación en la base de datos.
     * 
     * @param confederacion Confederacion a actualizar
     * @return true si se actualizó correctamente, false en caso contrario
     */

    public boolean actualizar(Confederacion confederacion) {
        try {
            PreparedStatement ps = ConexionDB.getInstancia().getConnection()
                    .prepareStatement(SQL_ACTUALIZAR);
            ps.setString(1, confederacion.getNombre());
            ps.setString(2, confederacion.getSiglas());
            ps.setInt(3, confederacion.getIdConfederacion());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ConfederacionDAO] " + e.getMessage());
        }
        return false;
    }

    /**
     * Elimina una confederación de la base de datos.
     * 
     * @param idConfederacion ID de la confederación
     * @return true si se eliminó correctamente, false en caso contrario
     */

    public boolean eliminar(int idConfederacion) {
        try {
            PreparedStatement ps = ConexionDB.getInstancia().getConnection()
                    .prepareStatement(SQL_ELIMINAR);
            ps.setInt(1, idConfederacion);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ConfederacionDAO] " + e.getMessage());
        }
        return false;
    }

    /**
     * Mapea un ResultSet a un objeto Confederacion.
     * 
     * @param rs ResultSet a mapear
     * @return Confederacion mapeada
     */

    private Confederacion mapearResultSet(ResultSet rs) throws SQLException {
        return new Confederacion(rs.getInt("id_confederacion"), rs.getString("nombre"), rs.getString("siglas"));
    }
}
