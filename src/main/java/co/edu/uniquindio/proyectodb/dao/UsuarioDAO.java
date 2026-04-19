package co.edu.uniquindio.proyectodb.dao;

import co.edu.uniquindio.proyectodb.db.ConexionDB;
import co.edu.uniquindio.proyectodb.model.Usuario;
import co.edu.uniquindio.proyectodb.model.valueobjects.TipoUsuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) para la entidad Usuario.
 *
 * OPERACIONES IMPLEMENTADAS (CRUD):
 * - CREATE : insertar(Usuario)
 * - READ : buscarPorId(int), buscarPorNombre(String), listarTodos()
 * - UPDATE : actualizar(Usuario)
 * - DELETE : eliminar(int)
 *
 * Adicionalmente se incluye:
 * - buscarPorCredenciales → usado por el sistema de autenticación
 */
public class UsuarioDAO {

    // CONSTANTES SQL

    private static final String SQL_INSERTAR = "INSERT INTO Usuario (nombre_usuario, contrasena_hash, tipo_usuario) " +
            "VALUES (?, ?, ?)";

    private static final String SQL_BUSCAR_POR_ID = "SELECT id_usuario, nombre_usuario, contrasena_hash, tipo_usuario, fecha_creacion "
            +
            "FROM Usuario WHERE id_usuario = ?";

    private static final String SQL_BUSCAR_POR_NOMBRE = "SELECT id_usuario, nombre_usuario, contrasena_hash, tipo_usuario, fecha_creacion "
            +
            "FROM Usuario WHERE nombre_usuario = ?";

    private static final String SQL_BUSCAR_POR_CREDENCIALES = "SELECT id_usuario, nombre_usuario, contrasena_hash, tipo_usuario, fecha_creacion "
            +
            "FROM Usuario WHERE nombre_usuario = ? AND contrasena_hash = ?";

    private static final String SQL_LISTAR_TODOS = "SELECT id_usuario, nombre_usuario, contrasena_hash, tipo_usuario, fecha_creacion "
            +
            "FROM Usuario ORDER BY nombre_usuario";

    private static final String SQL_ACTUALIZAR = "UPDATE Usuario SET nombre_usuario = ?, contrasena_hash = ?, tipo_usuario = ? "
            +
            "WHERE id_usuario = ?";

    private static final String SQL_ELIMINAR = "DELETE FROM Usuario WHERE id_usuario = ?";

    // CREATE — INSERT

    /**
     * Inserta un nuevo usuario en la base de datos.
     *
     * STATEMENT.RETURN_GENERATED_KEYS:
     * SQL Server asigna automáticamente el id_usuario (IDENTITY).
     * Al indicar RETURN_GENERATED_KEYS, JDBC nos devuelve el ID generado
     * para que podamos asignarlo al objeto Java y dejarlo sincronizado con la BD.
     *
     * @param usuario objeto con los datos a insertar
     * @return true si la inserción fue exitosa
     */
    public boolean insertar(Usuario usuario) {
        try {
            Connection con = ConexionDB.getInstancia().getConnection();

            PreparedStatement ps = con.prepareStatement(
                    SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS);

            // Los índices de setXxx van de 1 a N, en el orden de los '?' en el SQL.
            ps.setString(1, usuario.getNombreUsuario());
            ps.setString(2, usuario.getContrasenaHash());
            ps.setString(3, usuario.getTipoUsuario().name());

            /*
             * executeUpdate() ejecuta INSERT, UPDATE o DELETE.
             * Devuelve el número de filas afectadas
             */
            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                /*
                 * getGeneratedKeys() devuelve un ResultSet especial con
                 * las claves primarias generadas.
                 */
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    usuario.setIdUsuario(keys.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("[UsuarioDAO] Error al insertar usuario: " + e.getMessage());
        }
        return false;
    }

    // READ — SELECT por ID

    /**
     * Busca un usuario por su clave primaria.
     *
     * 1. Se prepara el SQL con prepareStatement
     * 2. Se asignan parámetros con setXxx
     * 3. Se ejecuta con executeQuery (solo para SELECT)
     * 4. Se recorre el ResultSet con while/if (next())
     * 5. Se mapea cada columna a un campo del POJO con mapearResultSet
     *
     * @param idUsuario clave primaria del usuario a buscar
     * @return el Usuario encontrado, o null si no existe
     */
    public Usuario buscarPorId(int idUsuario) {
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_ID);
            ps.setInt(1, idUsuario);

            /*
             * executeQuery() se usa exclusivamente para SELECT.
             * Devuelve un ResultSet
             */
            ResultSet rs = ps.executeQuery();

            /*
             * rs.next() avanza el cursor a la siguiente fila.
             * Si devuelve true, hay datos; si false, no se encontró nada.
             * Aquí usamos if porque esperamos como máximo 1 fila.
             */
            if (rs.next()) {
                return mapearResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("[UsuarioDAO] Error al buscar por ID: " + e.getMessage());
        }
        return null;
    }

    // READ — SELECT por nombre de usuario

    /**
     * Busca un usuario por su nombre de usuario (campo UNIQUE en la BD).
     *
     * @param nombreUsuario nombre único del usuario
     * @return el Usuario encontrado
     */
    public Usuario buscarPorNombre(String nombreUsuario) {
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_NOMBRE);
            ps.setString(1, nombreUsuario);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("[UsuarioDAO] Error al buscar por nombre: " + e.getMessage());
        }
        return null;
    }

    // READ — SELECT por credenciales (login)

    /**
     * Valida las credenciales de un usuario comparando nombre y hash de contraseña.
     *
     * SEGURIDAD — HASHING:
     * Se almacena el hash de la contraseña, nunca en texto plano
     * SHA-256. Al autenticar, se hashea lo que el usuario escribe y se compara
     * con el hash almacenado. Si coinciden, la contraseña es correcta.
     *
     * Este método recibe el hash ya calculado
     *
     * @param nombreUsuario  nombre del usuario
     * @param contrasenaHash hash SHA-256 de la contraseña ingresada
     * @return el Usuario autenticado, o null si las credenciales no coinciden
     */
    public Usuario buscarPorCredenciales(String nombreUsuario, String contrasenaHash) {
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_CREDENCIALES);
            ps.setString(1, nombreUsuario);
            ps.setString(2, contrasenaHash);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("[UsuarioDAO] Error al validar credenciales: " + e.getMessage());
        }
        return null;
    }

    // READ — SELECT todos

    /**
     * Retorna todos los usuarios registrados, ordenados por nombre.
     *
     * Se usa List<Usuario> en lugar de un arreglo porque no sabemos de
     * antemano cuántas filas devolverá la consulta.
     *
     * @return lista de todos los usuarios
     */
    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();

        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_LISTAR_TODOS);

            ResultSet rs = ps.executeQuery();

            /*
             * Usamos while porque esperamos múltiples filas.
             * Por cada fila, creamos un objeto Usuario y lo agregamos a la lista.
             */
            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("[UsuarioDAO] Error al listar usuarios: " + e.getMessage());
        }
        return lista;
    }

    // UPDATE

    /**
     * Actualiza los datos de un usuario existente en la base de datos.
     *
     * El id_usuario del objeto determina que fila se actualiza.
     * Si el objeto no tiene un id válido, el UPDATE no afectará ninguna fila.
     *
     * @param usuario objeto con los nuevos datos
     * @return true si se actualizó al menos una fila, false en caso contrario
     */
    public boolean actualizar(Usuario usuario) {
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_ACTUALIZAR);

            ps.setString(1, usuario.getNombreUsuario());
            ps.setString(2, usuario.getContrasenaHash());
            ps.setString(3, usuario.getTipoUsuario().name());
            ps.setInt(4, usuario.getIdUsuario());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[UsuarioDAO] Error al actualizar usuario: " + e.getMessage());
        }
        return false;
    }

    // DELETE

    /**
     * Elimina un usuario de la base de datos por su clave primaria.
     *
     * INTEGRIDAD REFERENCIAL:
     * Si el usuario tiene registros en la tabla Bitacora, SQL Server lanzará
     * un error de FK (Foreign Key violation) y no permitirá la eliminación.
     * La BD protege la consistencia de los datos.
     *
     * @param idUsuario clave primaria del usuario a eliminar
     * @return true si se eliminó la fila, false si no existía o hubo error
     */
    public boolean eliminar(int idUsuario) {
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_ELIMINAR);
            ps.setInt(1, idUsuario);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[UsuarioDAO] Error al eliminar usuario: " + e.getMessage());
        }
        return false;
    }

    // MAPEO ResultSet → POJO

    /**
     * Convierte una fila del ResultSet en un objeto Usuario.
     *
     * MAPEO OBJETO-RELACIONAL (ORM manual):
     * Las bases de datos trabajan con filas y columnas (modelo relacional).
     * Java trabaja con objetos (modelo orientado a objetos).
     * Este método traduce una fila de la BD a un objeto Java.
     *
     * @param rs ResultSet posicionado en una fila válida (después de next())
     * @return objeto Usuario poblado con los datos de esa fila
     * @throws SQLException si algún nombre de columna es incorrecto
     */
    private Usuario mapearResultSet(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();

        u.setIdUsuario(rs.getInt("id_usuario"));
        u.setNombreUsuario(rs.getString("nombre_usuario"));
        u.setContrasenaHash(rs.getString("contrasena_hash"));

        /*
         * El tipo_usuario se almacena como VARCHAR en la BD ('Administrador',
         * 'Tradicional', 'Esporadico'). TipoUsuario.valueOf() convierte ese
         * String de vuelta al valor del enum correspondiente.
         */
        u.setTipoUsuario(TipoUsuario.valueOf(rs.getString("tipo_usuario")));

        /*
         * SQL Server devuelve DATETIME como java.sql.Timestamp.
         * Llamamos a toLocalDateTime() para convertirlo al tipo moderno
         * de Java (LocalDateTime), que es más seguro y fácil de manejar.
         */
        Timestamp ts = rs.getTimestamp("fecha_creacion");
        if (ts != null) {
            u.setFechaCreacion(ts.toLocalDateTime());
        }

        return u;
    }
}
