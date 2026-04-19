package co.edu.uniquindio.proyectodb.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import co.edu.uniquindio.proyectodb.db.ConexionDB;
import co.edu.uniquindio.proyectodb.model.Ciudad;

/**
 * DAO para la entidad Ciudad.
 */

public class CiudadDAO {

    // Constantes SQL
    private static final String SQL_INSERTAR = "INSERT INTO Ciudad (nombre, idPaisAnfitrion) VALUES (?, ?)";
    private static final String SQL_BUSCAR_POR_ID = "SELECT id_ciudad, nombre, idPaisAnfitrion FROM Ciudad WHERE id_ciudad = ?";
    private static final String SQL_LISTAR_POR_PAIS = "SELECT id_ciudad, nombre, idPaisAnfitrion FROM Ciudad WHERE idPaisAnfitrion = ? ORDER BY nombre";

    private static final String SQL_LISTAR_TODOS = "SELECT id_ciudad, nombre, idPaisAnfitrion FROM Ciudad ORDER BY nombre";
    private static final String SQL_ACTUALIZAR = "UPDATE Ciudad SET nombre = ?, idPaisAnfitrion = ? WHERE id_ciudad = ?";
    private static final String SQL_ELIMINAR = "DELETE FROM Ciudad WHERE id_ciudad = ?";

    /**
     * Inserta una nueva ciudad en la base de datos.
     * 
     * @param ciudad Ciudad a insertar
     * @return true si se insertó correctamente, false en caso contrario
     */

    public boolean insertar(Ciudad ciudad) {
        try {
            PreparedStatement ps = ConexionDB.getInstancia().getConnection()
                    .prepareStatement(SQL_INSERTAR,
                            Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, ciudad.getNombre());
            ps.setInt(2, ciudad.getIdPaisAnfitrion());
            if (ps.executeUpdate() > 0) {
                ResultSet k = ps.getGeneratedKeys();
                if (k.next())
                    ciudad.setIdCiudad(k.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[CiudadDAO] " + e.getMessage());
        }
        return false;
    }

    /**
     * Busca una ciudad por su ID.
     * 
     * @param idCiudad ID de la ciudad
     * @return Ciudad encontrada o null si no existe
     */

    public Ciudad buscarPorId(int idCiudad) {
        try {
            PreparedStatement ps = ConexionDB.getInstancia().getConnection()
                    .prepareStatement(SQL_BUSCAR_POR_ID);
            ps.setInt(1, idCiudad);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return mapearResultSet(rs);
        } catch (SQLException e) {
            System.err.println("[CiudadDAO] " + e.getMessage());
        }
        return null;
    }

    /**
     * Busca ciudades por su pais anfitrion.
     * 
     * @param idPaisAnfitrion ID del pais anfitrion
     * @return Lista de ciudades encontradas
     */

    public List<Ciudad> listarPorPais(int idPaisAnfitrion) {
        List<Ciudad> lista = new ArrayList<>();
        try {
            PreparedStatement ps = ConexionDB.getInstancia().getConnection()
                    .prepareStatement(SQL_LISTAR_POR_PAIS);
            ps.setInt(1, idPaisAnfitrion);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                lista.add(mapearResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[CiudadDAO] " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lista todas las ciudades de la base de datos.
     * 
     * @return Lista de ciudades
     */

    public List<Ciudad> listarTodos() {
        List<Ciudad> lista = new ArrayList<>();
        try {
            ResultSet rs = ConexionDB.getInstancia().getConnection()
                    .prepareStatement(SQL_LISTAR_TODOS)
                    .executeQuery();
            while (rs.next())
                lista.add(mapearResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[CiudadDAO] " + e.getMessage());
        }
        return lista;
    }

    /**
     * Actualiza una ciudad en la base de datos.
     * 
     * @param ciudad Ciudad a actualizar
     * @return true si se actualizó correctamente, false en caso contrario
     */

    public boolean actualizar(Ciudad ciudad) {
        try {
            PreparedStatement ps = ConexionDB.getInstancia().getConnection()
                    .prepareStatement(SQL_ACTUALIZAR);
            ps.setString(1, ciudad.getNombre());
            ps.setInt(2, ciudad.getIdPaisAnfitrion());
            ps.setInt(3, ciudad.getIdCiudad());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CiudadDAO] " + e.getMessage());
        }
        return false;
    }

    /**
     * Elimina una ciudad de la base de datos.
     * 
     * @param idCiudad ID de la ciudad
     * @return true si se eliminó correctamente, false en caso contrario
     */

    public boolean eliminar(int idCiudad) {
        try {
            PreparedStatement ps = ConexionDB.getInstancia().getConnection()
                    .prepareStatement(SQL_ELIMINAR);
            ps.setInt(1, idCiudad);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CiudadDAO] " + e.getMessage());
        }
        return false;
    }

    /**
     * Mapea un ResultSet a un objeto Ciudad.
     * 
     * @param rs ResultSet a mapear
     * @return Ciudad mapeada
     */

    private Ciudad mapearResultSet(ResultSet rs) throws SQLException {
        return new Ciudad(rs.getInt("id_ciudad"), rs.getString("nombre"), rs.getInt("idPaisAnfitrion"));
    }

}
