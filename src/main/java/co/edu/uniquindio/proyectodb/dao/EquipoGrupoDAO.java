package co.edu.uniquindio.proyectodb.dao;

import co.edu.uniquindio.proyectodb.db.ConexionDB;
import co.edu.uniquindio.proyectodb.model.Equipo;
import co.edu.uniquindio.proyectodb.model.Grupo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla intermedia Equipo_Grupo.
 *
 * TABLA INTERMEDIA (relación N:M):
 *
 * OPERACIONES:
 * - asignarEquipoAGrupo → INSERT (crear la asociación)
 * - quitarEquipoDeGrupo → DELETE (romper la asociación)
 * - listarEquiposPorGrupo → qué equipos están en un grupo dado
 * - listarGruposPorEquipo → en qué grupo(s) está un equipo dado
 */
public class EquipoGrupoDAO {

    /*
     * JOIN en SQL:
     * Las siguientes consultas usan INNER JOIN para combinar datos de dos
     * tablas en una sola consulta.
     *
     * tabla_A INNER JOIN tabla_B ON tabla_A.columna = tabla_B.columna
     *
     * Solo devuelve filas donde hay coincidencia en AMBAS tablas.
     */

    private static final String SQL_LISTAR_EQUIPOS_POR_GRUPO = "SELECT e.id_equipo, e.nombre, e.pais, e.valor_total_equipo, e.id_confederacion "
            +
            "FROM Equipo e " +
            "INNER JOIN Equipo_Grupo eg ON e.id_equipo = eg.id_equipo " +
            "WHERE eg.id_grupo = ? " +
            "ORDER BY e.nombre";

    private static final String SQL_LISTAR_GRUPOS_POR_EQUIPO = "SELECT g.id_grupo, g.nombre_grupo " +
            "FROM Grupo g " +
            "INNER JOIN Equipo_Grupo eg ON g.id_grupo = eg.id_grupo " +
            "WHERE eg.id_equipo = ?";

    private static final String SQL_ASIGNAR = "INSERT INTO Equipo_Grupo (id_equipo, id_grupo) VALUES (?, ?)";

    private static final String SQL_QUITAR = "DELETE FROM Equipo_Grupo WHERE id_equipo = ? AND id_grupo = ?";

    private static final String SQL_EXISTE = "SELECT COUNT(*) FROM Equipo_Grupo WHERE id_equipo = ? AND id_grupo = ?";

    // ASIGNAR

    /**
     * Crea la asociación entre un equipo y un grupo.
     *
     * @param idEquipo ID del equipo a asignar
     * @param idGrupo  ID del grupo destino
     * @return true si se creó la asociación correctamente
     */
    public boolean asignarEquipoAGrupo(int idEquipo, int idGrupo) {
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_ASIGNAR);
            ps.setInt(1, idEquipo);
            ps.setInt(2, idGrupo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[EquipoGrupoDAO] Error al asignar equipo a grupo: " + e.getMessage());
        }
        return false;
    }

    // QUITAR

    /**
     * Elimina la asociación entre un equipo y un grupo.
     *
     * Nota: el DELETE usa dos condiciones (AND) para identificar exactamente
     * qué fila eliminar, ya que no filtramos por la PK (id_equipo_grupo)
     * sino por los campos que tienen sentido semántico para el usuario.
     *
     * @param idEquipo ID del equipo
     * @param idGrupo  ID del grupo
     * @return true si se eliminó la asociación
     */
    public boolean quitarEquipoDeGrupo(int idEquipo, int idGrupo) {
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_QUITAR);
            ps.setInt(1, idEquipo);
            ps.setInt(2, idGrupo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[EquipoGrupoDAO] Error al quitar equipo de grupo: " + e.getMessage());
        }
        return false;
    }

    // LISTAR equipos de un grupo

    /**
     * Devuelve todos los Equipos asignados a un grupo específico.
     *
     * Esta consulta usa INNER JOIN para "cruzar" Equipo_Grupo con Equipo
     * y traer los datos completos del equipo en una sola consulta,
     * evitando hacer una consulta por cada ID encontrado.
     *
     * @param idGrupo ID del grupo (ej: 1 para Grupo A)
     * @return lista de Equipos en ese grupo
     */
    public List<Equipo> listarEquiposPorGrupo(int idGrupo) {
        List<Equipo> lista = new ArrayList<>();
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_LISTAR_EQUIPOS_POR_GRUPO);
            ps.setInt(1, idGrupo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Equipo e = new Equipo();
                e.setIdEquipo(rs.getInt("id_equipo"));
                e.setNombre(rs.getString("nombre"));
                e.setPais(rs.getString("pais"));
                e.setValorTotalEquipo(rs.getBigDecimal("valor_total_equipo"));
                e.setIdConfederacion(rs.getInt("id_confederacion"));
                lista.add(e);
            }
        } catch (SQLException e) {
            System.err.println("[EquipoGrupoDAO] Error al listar equipos por grupo: " + e.getMessage());
        }
        return lista;
    }

    // LISTAR grupos de un equipo

    /**
     * Devuelve los Grupos a los que pertenece un equipo.
     *
     * @param idEquipo ID del equipo
     * @return lista de Grupos a los que pertenece ese equipo
     */
    public List<Grupo> listarGruposPorEquipo(int idEquipo) {
        List<Grupo> lista = new ArrayList<>();
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_LISTAR_GRUPOS_POR_EQUIPO);
            ps.setInt(1, idEquipo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Grupo g = new Grupo();
                g.setIdGrupo(rs.getInt("id_grupo"));
                g.setNombreGrupo(rs.getString("nombre_grupo"));
                lista.add(g);
            }
        } catch (SQLException e) {
            System.err.println("[EquipoGrupoDAO] Error al listar grupos por equipo: " + e.getMessage());
        }
        return lista;
    }

    // VERIFICAR

    /**
     * Verifica si un equipo ya está asignado a un grupo.
     *
     * COUNT(*) en SQL:
     * Cuenta el número de filas que cumplen la condición.
     * Si COUNT devuelve 0 → no existe. Si devuelve 1 → ya existe.
     * Se usa para validar antes de intentar un INSERT que fallaría
     * por la restricción UNIQUE.
     *
     * @param idEquipo ID del equipo
     * @param idGrupo  ID del grupo
     * @return true si la asociación ya existe
     */
    public boolean existeAsignacion(int idEquipo, int idGrupo) {
        try {
            Connection con = ConexionDB.getInstancia().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_EXISTE);
            ps.setInt(1, idEquipo);
            ps.setInt(2, idGrupo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // columna 1 = resultado del COUNT(*)
            }
        } catch (SQLException e) {
            System.err.println("[EquipoGrupoDAO] Error al verificar asignación: " + e.getMessage());
        }
        return false;
    }
}
