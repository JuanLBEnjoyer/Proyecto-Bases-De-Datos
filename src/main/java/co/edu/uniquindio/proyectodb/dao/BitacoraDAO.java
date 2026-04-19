package co.edu.uniquindio.proyectodb.dao;

import co.edu.uniquindio.proyectodb.db.ConexionDB;
import co.edu.uniquindio.proyectodb.model.Bitacora;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad Bitacora.
 *
 * La bitácora registra CUÁNDO entran y salen los usuarios del sistema.
 * Tiene un flujo especial de dos pasos:
 *
 * 1. Al hacer LOGIN → registrarIngreso(idUsuario) → INSERT con
 * fecha_hora_salida = NULL
 * 2. Al hacer LOGOUT → registrarSalida(idRegistro) → UPDATE solo
 * fecha_hora_salida
 *
 */
public class BitacoraDAO {

    // CONSTANTES SQL

    private static final String SQL_REGISTRAR_INGRESO = "INSERT INTO Bitacora (id_usuario) VALUES (?)";
    // fecha_hora_ingreso se llena sola con DEFAULT GETDATE()
    // fecha_hora_salida queda NULL (sesión activa)

    private static final String SQL_REGISTRAR_SALIDA = "UPDATE Bitacora SET fecha_hora_salida = GETDATE() WHERE id_registro = ?";

    private static final String SQL_LISTAR_POR_USUARIO = "SELECT id_registro, id_usuario, fecha_hora_ingreso, fecha_hora_salida "
            +
            "FROM Bitacora WHERE id_usuario = ? ORDER BY fecha_hora_ingreso DESC";

    private static final String SQL_LISTAR_SESIONES_ACTIVAS = "SELECT id_registro, id_usuario, fecha_hora_ingreso, fecha_hora_salida "
            +
            "FROM Bitacora WHERE fecha_hora_salida IS NULL ORDER BY fecha_hora_ingreso DESC";

    private static final String SQL_LISTAR_TODOS = "SELECT id_registro, id_usuario, fecha_hora_ingreso, fecha_hora_salida "
            +
            "FROM Bitacora ORDER BY fecha_hora_ingreso DESC";

    /**
     * Registra el ingreso de un usuario al sistema.
     * Se llama justo después de una autenticación exitosa.
     *
     * @param idUsuario ID del usuario que inicia sesión
     * @return el ID del registro creado (necesario para registrar la salida
     *         después)
     *         o -1 si ocurrió un error
     */
    public int registrarIngreso(int idUsuario) {
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(
                    SQL_REGISTRAR_INGRESO, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, idUsuario);
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1); // retornamos el id_registro generado
            }
        } catch (SQLException e) {
            System.err.println("[BitacoraDAO] Error al registrar ingreso: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Registra la salida de un usuario del sistema.
     * Se llama cuando el usuario elige "Cerrar sesión" en el menú.
     *
     * @param idRegistro el ID devuelto por registrarIngreso()
     * @return true si se actualizó correctamente
     */
    public boolean registrarSalida(int idRegistro) {
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_REGISTRAR_SALIDA);
            ps.setInt(1, idRegistro);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BitacoraDAO] Error al registrar salida: " + e.getMessage());
        }
        return false;
    }

    /**
     * Lista todas las sesiones de un usuario específico, de más reciente a más
     * antigua.
     * 
     * @param idUsuario ID del usuario
     * @return Lista de bitácoras
     */
    public List<Bitacora> listarPorUsuario(int idUsuario) {
        List<Bitacora> lista = new ArrayList<>();
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_LISTAR_POR_USUARIO);
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                lista.add(mapearResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[BitacoraDAO] Error al listar por usuario: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lista sesiones activas (sin fecha de salida registrada).
     *
     * IS NULL en SQL:
     * En SQL no se puede comparar NULL con = (NULL = NULL es falso en SQL).
     * La única forma correcta de verificar si un valor es nulo es con IS NULL.
     * 
     * @return Lista de bitácoras
     */
    public List<Bitacora> listarSesionesActivas() {
        List<Bitacora> lista = new ArrayList<>();
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            ResultSet rs = con.prepareStatement(SQL_LISTAR_SESIONES_ACTIVAS).executeQuery();
            while (rs.next())
                lista.add(mapearResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[BitacoraDAO] Error al listar sesiones activas: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lista todas las bitácoras.
     * 
     * @return Lista de bitácoras
     */
    public List<Bitacora> listarTodos() {
        List<Bitacora> lista = new ArrayList<>();
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            ResultSet rs = con.prepareStatement(SQL_LISTAR_TODOS).executeQuery();
            while (rs.next())
                lista.add(mapearResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[BitacoraDAO] Error al listar: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Convierte un ResultSet a objeto Bitacora.
     * 
     * @param rs ResultSet con los datos de la bitácora
     * @return Objeto Bitacora
     * @throws SQLException Si ocurre un error al obtener los datos
     */
    private Bitacora mapearResultSet(ResultSet rs) throws SQLException {
        Bitacora b = new Bitacora();
        b.setIdRegistro(rs.getInt("id_registro"));
        b.setIdUsuario(rs.getInt("id_usuario"));
        b.setFechaHoraIngreso(rs.getTimestamp("fecha_hora_ingreso").toLocalDateTime());

        /*
         * fecha_hora_salida puede ser NULL en la BD (sesión activa).
         * rs.getTimestamp() devuelve null en ese caso.
         * Debemos verificar antes de llamar toLocalDateTime() para evitar
         * un NullPointerException.
         */
        Timestamp salida = rs.getTimestamp("fecha_hora_salida");
        b.setFechaHoraSalida(salida != null ? salida.toLocalDateTime() : null);

        return b;
    }
}
