package co.edu.uniquindio.proyectodb.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import co.edu.uniquindio.proyectodb.db.ConexionDB;
import co.edu.uniquindio.proyectodb.model.PaisAnfitrion;

/**
 * DAO para la entidad PaisAnfitrion.
 */

public class PaisAnfitrionDAO {

    // Constantes SQL
    private static final String SQL_INSERTAR = "INSERT INTO PaisAnfitrion (nombre) VALUES (?)";
    private static final String SQL_BUSCAR_POR_ID = "SELECT id_pais_anfitrion, nombre FROM PaisAnfitrion WHERE id_pais_anfitrion = ?";
    private static final String SQL_LISTAR_TODOS = "SELECT id_pais_anfitrion, nombre FROM PaisAnfitrion ORDER BY nombre";
    private static final String SQL_ACTUALIZAR = "UPDATE PaisAnfitrion SET nombre = ? WHERE id_pais_anfitrion = ?";
    private static final String SQL_ELIMINAR = "DELETE FROM PaisAnfitrion WHERE id_pais_anfitrion = ?";

    /**
     * Inserta un nuevo pais anfitrion en la base de datos.
     * 
     * @param paisAnfitrion Pais anfitrion a insertar
     * @return true si se insertó correctamente, false en caso contrario
     */

    public boolean insertar(PaisAnfitrion paisAnfitrion) {
        try {
            PreparedStatement ps = ConexionDB.getInstancia().getConnection()
                    .prepareStatement(SQL_INSERTAR,
                            Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, paisAnfitrion.getNombre());
            if (ps.executeUpdate() > 0) {
                ResultSet k = ps.getGeneratedKeys();
                if (k.next())
                    paisAnfitrion.setIdPaisAnfitrion(k.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[PaisAnfitrionDAO] " + e.getMessage());
        }
        return false;
    }

    /**
     * Busca un pais anfitrion por su ID.
     * 
     * @param idPaisAnfitrion ID del pais anfitrion
     * @return Pais anfitrion encontrado o null si no existe
     */

    public PaisAnfitrion buscarPorId(int idPaisAnfitrion) {
        try {
            PreparedStatement ps = ConexionDB.getInstancia().getConnection()
                    .prepareStatement(SQL_BUSCAR_POR_ID);
            ps.setInt(1, idPaisAnfitrion);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return mapearResultSet(rs);
        } catch (SQLException e) {
            System.err.println("[PaisAnfitrionDAO] " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista todos los paises anfitriones de la base de datos.
     * 
     * @return Lista de paises anfitriones
     */

    public List<PaisAnfitrion> listarTodos() {
        List<PaisAnfitrion> lista = new ArrayList<>();
        try {
            ResultSet rs = ConexionDB.getInstancia().getConnection()
                    .prepareStatement(SQL_LISTAR_TODOS)
                    .executeQuery();
            while (rs.next())
                lista.add(mapearResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[PaisAnfitrionDAO] " + e.getMessage());
        }
        return lista;
    }

    /**
     * Actualiza un pais anfitrion en la base de datos.
     * 
     * @param paisAnfitrion Pais anfitrion a actualizar
     * @return true si se actualizó correctamente, false en caso contrario
     */

    public boolean actualizar(PaisAnfitrion paisAnfitrion) {
        try {
            PreparedStatement ps = ConexionDB.getInstancia().getConnection()
                    .prepareStatement(SQL_ACTUALIZAR);
            ps.setString(1, paisAnfitrion.getNombre());
            ps.setInt(2, paisAnfitrion.getIdPaisAnfitrion());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PaisAnfitrionDAO] " + e.getMessage());
        }
        return false;
    }

    /**
     * Elimina un pais anfitrion de la base de datos.
     * 
     * @param idPaisAnfitrion ID del pais anfitrion
     * @return true si se eliminó correctamente, false en caso contrario
     */

    public boolean eliminar(int idPaisAnfitrion) {
        try {
            PreparedStatement ps = ConexionDB.getInstancia().getConnection()
                    .prepareStatement(SQL_ELIMINAR);
            ps.setInt(1, idPaisAnfitrion);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PaisAnfitrionDAO] " + e.getMessage());
        }
        return false;
    }

    /**
     * Mapea un ResultSet a un objeto PaisAnfitrion.
     * 
     * @param rs ResultSet a mapear
     * @return PaisAnfitrion mapeado
     */

    private PaisAnfitrion mapearResultSet(ResultSet rs) throws SQLException {
        return new PaisAnfitrion(rs.getInt("id_pais_anfitrion"), rs.getString("nombre"));
    }

}
