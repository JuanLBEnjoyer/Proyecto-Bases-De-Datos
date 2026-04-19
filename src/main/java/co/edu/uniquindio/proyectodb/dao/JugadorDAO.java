package co.edu.uniquindio.proyectodb.dao;

import co.edu.uniquindio.proyectodb.db.ConexionDB;
import co.edu.uniquindio.proyectodb.model.Jugador;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad Jugador.
 *
 * Además del CRUD estándar, este DAO implementa consultas especializadas
 * que serán requeridas por los reportes del sistema:
 *
 * - listarPorEquipo → todos los jugadores de un equipo
 * - listarPorPosicion → filtro por posición (Delantero, Defensa, etc.)
 * - listarPorRangoValor → jugadores entre dos valores de mercado
 * - listarMayoresDe → jugadores mayores de N años
 * - listarMenoresDe → jugadores menores de N años
 */
public class JugadorDAO {

    // CONSTANTES SQL

    private static final String COLS = "id_jugador, nombre, fecha_nacimiento, posicion, peso, estatura, " +
            "valor_mercado, id_equipo";

    private static final String SQL_INSERTAR = "INSERT INTO Jugador (nombre, fecha_nacimiento, posicion, peso, estatura, valor_mercado, id_equipo) "
            +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_BUSCAR_POR_ID = "SELECT " + COLS + " FROM Jugador WHERE id_jugador = ?";

    private static final String SQL_LISTAR_TODOS = "SELECT " + COLS + " FROM Jugador ORDER BY nombre";

    private static final String SQL_LISTAR_POR_EQUIPO = "SELECT " + COLS
            + " FROM Jugador WHERE id_equipo = ? ORDER BY nombre";

    private static final String SQL_LISTAR_POR_POSICION = "SELECT " + COLS
            + " FROM Jugador WHERE posicion = ? ORDER BY valor_mercado DESC";

    private static final String SQL_LISTAR_POR_RANGO_VALOR = "SELECT " + COLS
            + " FROM Jugador WHERE valor_mercado BETWEEN ? AND ? ORDER BY valor_mercado DESC";

    /*
     * DATEDIFF en SQL Server:
     * DATEDIFF(year, fecha_nacimiento, GETDATE()) calcula la diferencia en años
     * entre la fecha de nacimiento y hoy.
     */
    private static final String SQL_LISTAR_MAYORES_DE = "SELECT id_jugador, nombre, fecha_nacimiento, posicion, peso, estatura, valor_mercado, id_equipo "
            +
            "FROM Jugador WHERE DATEDIFF(year, fecha_nacimiento, GETDATE()) > ? " +
            "ORDER BY fecha_nacimiento ASC";

    private static final String SQL_LISTAR_MENORES_DE = "SELECT id_jugador, nombre, fecha_nacimiento, posicion, peso, estatura, valor_mercado, id_equipo "
            +
            "FROM Jugador WHERE DATEDIFF(year, fecha_nacimiento, GETDATE()) < ? " +
            "ORDER BY fecha_nacimiento DESC";

    private static final String SQL_ACTUALIZAR = "UPDATE Jugador SET nombre = ?, fecha_nacimiento = ?, posicion = ?, " +
            "peso = ?, estatura = ?, valor_mercado = ?, id_equipo = ? " +
            "WHERE id_jugador = ?";

    private static final String SQL_ELIMINAR = "DELETE FROM Jugador WHERE id_jugador = ?";

    // CREATE

    /**
     * Inserta un nuevo jugador en la base de datos.
     *
     * @param jugador objeto Jugador con todos los campos requeridos
     * @return true si la inserción fue exitosa
     */
    public boolean insertar(Jugador jugador) {
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(
                    SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, jugador.getNombre());

            /*
             * LocalDate → java.sql.Date
             * Date.valueOf() lo convierte al tipo que JDBC sí entiende.
             */
            ps.setDate(2, Date.valueOf(jugador.getFechaNacimiento()));

            ps.setString(3, jugador.getPosicion());

            /*
             * setBigDecimal se usa para DECIMAL/NUMERIC en SQL.
             */
            ps.setBigDecimal(4, jugador.getPeso());
            ps.setBigDecimal(5, jugador.getEstatura());
            ps.setBigDecimal(6, jugador.getValorMercado());
            ps.setInt(7, jugador.getIdEquipo());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    jugador.setIdJugador(keys.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("[JugadorDAO] Error al insertar: " + e.getMessage());
        }
        return false;
    }

    // READ — por ID

    public Jugador buscarPorId(int idJugador) {
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_ID);
            ps.setInt(1, idJugador);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("[JugadorDAO] Error al buscar por ID: " + e.getMessage());
        }
        return null;
    }

    // READ — todos
    /**
     * Lista todos los jugadores de la base de datos.
     * 
     * @return lista de jugadores
     */

    public List<Jugador> listarTodos() {
        List<Jugador> lista = new ArrayList<>();
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_LISTAR_TODOS);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                lista.add(mapearResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[JugadorDAO] Error al listar todos: " + e.getMessage());
        }
        return lista;
    }

    // READ — por equipo

    /**
     * Lista todos los jugadores que pertenecen a un equipo específico.
     *
     * @param idEquipo clave foránea del equipo
     * @return lista de jugadores del equipo
     */
    public List<Jugador> listarPorEquipo(int idEquipo) {
        List<Jugador> lista = new ArrayList<>();
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_LISTAR_POR_EQUIPO);
            ps.setInt(1, idEquipo);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                lista.add(mapearResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[JugadorDAO] Error al listar por equipo: " + e.getMessage());
        }
        return lista;
    }

    // READ — por posición

    /**
     * Lista jugadores filtrando por posición, ordenados por valor de mercado
     * de mayor a menor (DESC).
     *
     * ORDER BY con DESC/ASC:
     * - ASC (ascendente) → de menor a mayor
     * - DESC (descendente) → de mayor a menor
     *
     * @param posicion ej: "Delantero", "Defensa", "Centrocampista", "Portero"
     * @return lista de jugadores en esa posición
     */
    public List<Jugador> listarPorPosicion(String posicion) {
        List<Jugador> lista = new ArrayList<>();
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_LISTAR_POR_POSICION);
            ps.setString(1, posicion);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                lista.add(mapearResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[JugadorDAO] Error al listar por posición: " + e.getMessage());
        }
        return lista;
    }

    // READ — por rango de valor de mercado

    /**
     * Lista jugadores cuyo valor de mercado esté dentro de un rango.
     *
     * BETWEEN en SQL:
     * WHERE valor_mercado BETWEEN 10000000 AND 50000000
     * Es equivalente a:
     * WHERE valor_mercado >= 10000000 AND valor_mercado <= 50000000
     *
     * @param valorMinimo valor mínimo en euros/dólares
     * @param valorMaximo valor máximo en euros/dólares
     * @return lista de jugadores en ese rango de valor
     */
    public List<Jugador> listarPorRangoValor(BigDecimal valorMinimo, BigDecimal valorMaximo) {
        List<Jugador> lista = new ArrayList<>();
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_LISTAR_POR_RANGO_VALOR);
            ps.setBigDecimal(1, valorMinimo);
            ps.setBigDecimal(2, valorMaximo);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                lista.add(mapearResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[JugadorDAO] Error al listar por rango de valor: " + e.getMessage());
        }
        return lista;
    }

    // READ — mayores de N años

    /**
     * Lista jugadores mayores de una edad dada.
     *
     * FUNCIONES DE FECHA EN SQL SERVER:
     * - GETDATE() → fecha y hora actual del servidor
     * - DATEDIFF(parte, inicio, fin) → diferencia entre dos fechas
     * partes comunes: year, month, day, hour
     *
     * @param edad edad mínima en años
     * @return lista de jugadores mayores de esa edad
     */
    public List<Jugador> listarMayoresDe(int edad) {
        List<Jugador> lista = new ArrayList<>();
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_LISTAR_MAYORES_DE);
            ps.setInt(1, edad);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                lista.add(mapearResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[JugadorDAO] Error al listar mayores de " + edad + ": " + e.getMessage());
        }
        return lista;
    }

    // READ — menores de N años
    /**
     * Lista jugadores menores de una edad dada.
     * 
     * @param edad edad mínima en años
     * @return lista de jugadores menores de esa edad
     */

    public List<Jugador> listarMenoresDe(int edad) {
        List<Jugador> lista = new ArrayList<>();
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_LISTAR_MENORES_DE);
            ps.setInt(1, edad);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                lista.add(mapearResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[JugadorDAO] Error al listar menores de " + edad + ": " + e.getMessage());
        }
        return lista;
    }

    // UPDATE
    /**
     * Actualiza un jugador en la base de datos.
     * 
     * @param jugador jugador a actualizar
     * @return true si la actualización fue exitosa
     */

    public boolean actualizar(Jugador jugador) {
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_ACTUALIZAR);

            ps.setString(1, jugador.getNombre());
            ps.setDate(2, Date.valueOf(jugador.getFechaNacimiento()));
            ps.setString(3, jugador.getPosicion());
            ps.setBigDecimal(4, jugador.getPeso());
            ps.setBigDecimal(5, jugador.getEstatura());
            ps.setBigDecimal(6, jugador.getValorMercado());
            ps.setInt(7, jugador.getIdEquipo());
            ps.setInt(8, jugador.getIdJugador());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[JugadorDAO] Error al actualizar: " + e.getMessage());
        }
        return false;
    }

    // DELETE
    /**
     * Elimina un jugador de la base de datos.
     * 
     * @param idJugador id del jugador a eliminar
     * @return true si la eliminación fue exitosa
     */

    public boolean eliminar(int idJugador) {
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_ELIMINAR);
            ps.setInt(1, idJugador);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[JugadorDAO] Error al eliminar: " + e.getMessage());
        }
        return false;
    }

    // MAPEO ResultSet → POJO

    /**
     * Convierte una fila del ResultSet al POJO Jugador.
     *
     * CONVERSIÓN DE FECHAS:
     * - rs.getDate("fecha_nacimiento") devuelve java.sql.Date
     * - .toLocalDate() lo convierte a java.time.LocalDate (moderno)
     *
     * CONVERSIÓN DE DECIMALES:
     * - rs.getBigDecimal("peso") devuelve directamente BigDecimal
     */
    private Jugador mapearResultSet(ResultSet rs) throws SQLException {
        Jugador j = new Jugador();

        j.setIdJugador(rs.getInt("id_jugador"));
        j.setNombre(rs.getString("nombre"));

        j.setFechaNacimiento(rs.getDate("fecha_nacimiento").toLocalDate());

        j.setPosicion(rs.getString("posicion"));
        j.setPeso(rs.getBigDecimal("peso"));
        j.setEstatura(rs.getBigDecimal("estatura"));
        j.setValorMercado(rs.getBigDecimal("valor_mercado"));
        j.setIdEquipo(rs.getInt("id_equipo"));

        return j;
    }
}
