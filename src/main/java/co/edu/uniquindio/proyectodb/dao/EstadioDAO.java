package co.edu.uniquindio.proyectodb.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import co.edu.uniquindio.proyectodb.db.ConexionDB;
import co.edu.uniquindio.proyectodb.model.Estadio;

/**
 * DAO para la entidad Estadio.
 */

public class EstadioDAO {

    // Constantes SQL
    private static final String SQL_INSERTAR = "INSERT INTO Estadio (nombre, capacidad, idCiudad) VALUES (?, ?, ?)";

    private static final String SQL_BUSCAR_POR_ID = "SELECT id_estadio, nombre, capacidad, idCiudad FROM Estadio WHERE id_estadio = ?";

    private static final String SQL_LISTAR_POR_CIUDAD = "SELECT id_estadio, nombre, capacidad, idCiudad FROM Estadio WHERE idCiudad = ? ORDER BY nombre";

    private static final String SQL_LISTAR_POR_CAPACIDAD_MINIMA = "SELECT id_estadio, nombre, capacidad, idCiudad FROM Estadio WHERE capacidad >= ? ORDER BY capacidad DESC";

    private static final String SQL_LISTAR_TODOS = "SELECT id_estadio, nombre, capacidad, idCiudad FROM Estadio ORDER BY nombre";

    private static final String SQL_ACTUALIZAR = "UPDATE Estadio SET nombre = ?, capacidad = ?, idCiudad = ? WHERE id_estadio = ?";

    private static final String SQL_ELIMINAR = "DELETE FROM Estadio WHERE id_estadio = ?";

    /**
     * Inserta un nuevo estadio en la base de datos.
     * 
     * @param estadio Estadio a insertar
     * @return true si se insertó correctamente, false en caso contrario
     */
    public boolean insertar(Estadio estadio) {
        try {
            PreparedStatement ps = ConexionDB.getInstancia().getConnection()
                    .prepareStatement(SQL_INSERTAR);
            ps.setString(1, estadio.getNombre());
            ps.setInt(2, estadio.getCapacidad());
            ps.setInt(3, estadio.getIdCiudad());
            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    estadio.setIdEstadio(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[EstadioDAO] " + e.getMessage());
        }
        return false;
    }

    /**
     * Busca un estadio por su ID.
     * 
     * @param idEstadio ID del estadio
     * @return Estadio encontrado o null si no existe
     */
    public Estadio buscarPorId(int idEstadio) {
        try {
            PreparedStatement ps = ConexionDB.getInstancia().getConnection()
                    .prepareStatement(SQL_BUSCAR_POR_ID);
            ps.setInt(1, idEstadio);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("[EstadioDAO] " + e.getMessage());
        }
        return null;
    }

    /**
     * Busca estadios por su ciudad anfitriona.
     * 
     * @param idCiudad ID de la ciudad anfitriona
     * @return Lista de estadios encontrados
     */
    public List<Estadio> listarPorCiudad(int idCiudad) {
        List<Estadio> lista = new ArrayList<>();
        try {
            PreparedStatement ps = ConexionDB.getInstancia().getConnection()
                    .prepareStatement(SQL_LISTAR_POR_CIUDAD);
            ps.setInt(1, idCiudad);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("[EstadioDAO] " + e.getMessage());
        }
        return lista;
    }

    /**
     * Busca estadios por capacidad minima.
     * 
     * @param capacidadMinima capacidad minima del estadio
     * @return Lista de estadios encontrados
     */
    public List<Estadio> listarPorCapacidadMinima(int capacidadMinima) {
        List<Estadio> lista = new ArrayList<>();
        try {
            PreparedStatement ps = ConexionDB.getInstancia().getConnection()
                    .prepareStatement(SQL_LISTAR_POR_CAPACIDAD_MINIMA);
            ps.setInt(1, capacidadMinima);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("[EstadioDAO] " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lista todos los estadios de la base de datos.
     * 
     * @return Lista de estadios
     */
    public List<Estadio> listarTodos() {
        List<Estadio> lista = new ArrayList<>();
        try {
            Statement stmt = ConexionDB.getInstancia().getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(SQL_LISTAR_TODOS);
            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("[EstadioDAO] " + e.getMessage());
        }
        return lista;
    }

    /**
     * Actualiza un estadio existente.
     * 
     * @param estadio Estadio con los datos actualizados
     * @return true si se actualizó correctamente, false en caso contrario
     */
    public boolean actualizar(Estadio estadio) {
        try {
            PreparedStatement ps = ConexionDB.getInstancia().getConnection()
                    .prepareStatement(SQL_ACTUALIZAR);
            ps.setString(1, estadio.getNombre());
            ps.setInt(2, estadio.getCapacidad());
            ps.setInt(3, estadio.getIdCiudad());
            ps.setInt(4, estadio.getIdEstadio());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[EstadioDAO] " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un estadio por su ID.
     * 
     * @param idEstadio ID del estadio a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    public boolean eliminar(int idEstadio) {
        try {
            PreparedStatement ps = ConexionDB.getInstancia().getConnection()
                    .prepareStatement(SQL_ELIMINAR);
            ps.setInt(1, idEstadio);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[EstadioDAO] " + e.getMessage());
            return false;
        }
    }

    /**
     * Mapea un ResultSet a un objeto Estadio.
     * 
     * @param rs ResultSet con los datos del estadio
     * @return Objeto Estadio
     * @throws SQLException Si ocurre un error al leer el ResultSet
     */
    private Estadio mapearResultSet(ResultSet rs) throws SQLException {
        Estadio estadio = new Estadio();
        estadio.setIdEstadio(rs.getInt("id_estadio"));
        estadio.setNombre(rs.getString("nombre"));
        estadio.setCapacidad(rs.getInt("capacidad"));
        estadio.setIdCiudad(rs.getInt("idCiudad"));
        return estadio;
    }
}
