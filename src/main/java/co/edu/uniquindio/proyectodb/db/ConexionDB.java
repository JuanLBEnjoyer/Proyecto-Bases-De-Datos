package co.edu.uniquindio.proyectodb.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase responsable de gestionar la conexión a la base de datos SQL Server.
 *
 * PATRÓN SINGLETON:
 * Garantiza que exista una única instancia de esta clase durante toda la
 * ejecución del programa. Esto evita abrir múltiples conexiones innecesarias,
 * lo cual sería costoso en términos de tiempo y recursos del sistema.
 *
 * JDBC (Java Database Connectivity):
 * Es la API estándar de Java para interactuar con bases de datos relacionales.
 * Independientemente del motor de base de datos (SQL Server, MySQL, etc.),
 * el código Java siempre usa las mismas clases: Connection, Statement,
 * PreparedStatement, ResultSet.
 */
public class ConexionDB {

    // CONFIGURACIÓN DE CONEXIÓN

    // Nombre del servidor donde corre SQL Server.
    private static final String SERVER = "localhost";

    // Puerto de SQL Server.
    private static final int PORT = 1433;

    // Nombre de la base de datos creada en el script SQL.
    private static final String DATABASE = "Mundial2026";

    // Usuario de SQL
    private static final String USER = "[USERNAME]";

    // Contraseña del usuario.
    private static final String PASSWORD = "[PASSWORD]";

    /**
     * Cadena de conexión JDBC para SQL Server.
     *
     * Formato: jdbc:sqlserver://<servidor>:<puerto>;databaseName=<bd>;...
     */
    private static final String URL = String.format(
            "jdbc:sqlserver://%s:%d;databaseName=%s;encrypt=false;trustServerCertificate=true",
            SERVER, PORT, DATABASE);

    // SINGLETON

    /**
     * La única instancia de esta clase. Se declara 'static' porque pertenece
     * a la clase en sí, no a ningún objeto en particular.
     *
     * Se inicializa en null porque todavía no se ha creado ninguna conexión.
     */
    private static ConexionDB instancia = null;

    /**
     * El objeto Connection de JDBC que representa la sesión activa con la BD.
     * A través de él se crean todos los PreparedStatement para ejecutar SQL.
     */
    private Connection connection;

    // CONSTRUCTOR PRIVADO
    // Al ser privado, nadie fuera de esta clase puede hacer: new ConexionDB().

    /**
     * Constructor privado. Registra el driver JDBC y abre la conexión.
     *
     * @throws SQLException si los datos de conexión son incorrectos o
     *                      SQL Server no está disponible.
     */
    private ConexionDB() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver de SQL Server no encontrado. " +
                    "Verifica que mssql-jdbc esté en tu pom.xml.", e);
        }

        /*
         * DriverManager.getConnection intenta abrir una sesión con la BD
         * usando la URL, usuario y contraseña indicados.
         * Si falla, lanza una SQLException con el motivo.
         */
        this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
        System.out.println("[DB] Conexión establecida con " + DATABASE);
    }

    // MÉTODO DE ACCESO A LA INSTANCIA

    /**
     * Punto de acceso global a la única instancia de ConexionDB.
     *
     * Lógica:
     * 1. Si nunca se ha creado una instancia → la crea.
     * 2. Si la conexión existe pero está cerrada → la recrea.
     * 3. En cualquier otro caso → devuelve la conexión existente.
     *
     * @return la instancia única de ConexionDB.
     * @throws SQLException si no se puede establecer la conexión.
     */
    public static ConexionDB getInstancia() throws SQLException {
        if (instancia == null || instancia.connection.isClosed()) {
            instancia = new ConexionDB();
        }
        return instancia;
    }

    // MÉTODO PRINCIPAL

    /**
     * Devuelve el objeto Connection de JDBC.
     *
     * Este es el objeto que usan los DAOs para crear PreparedStatement
     * y ejecutar consultas SQL contra la base de datos.
     *
     * @return Connection activo con SQL Server.
     */
    public Connection getConnection() {
        return connection;
    }

    // MÉTODO DE CIERRE

    /**
     * Cierra la conexión con la base de datos de forma segura.
     *
     * Debe llamarse al finalizar la aplicación para liberar los recursos del
     * servidor.
     */
    public void cerrarConexion() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Conexión cerrada correctamente.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error al cerrar la conexión: " + e.getMessage());
        }
    }
}
