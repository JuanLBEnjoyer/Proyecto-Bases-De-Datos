package co.edu.uniquindio.proyectodb.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import co.edu.uniquindio.proyectodb.db.ConexionDB;
import co.edu.uniquindio.proyectodb.model.Grupo;

/**
 * DAO para la entidad Grupo
 */

public class GrupoDAO {

    // Constantes SQL
    private static final String SQL_INSERTAR = "INSERT INTO Grupo (nombre) VALUES (?)";
    private static final String SQL_BUSCAR_POR_ID = "SELECT id_grupo, nombre FROM Grupo WHERE id_grupo = ?";
    private static final String SQL_LISTAR_TODOS = "SELECT id_grupo, nombre FROM Grupo ORDER BY nombre";

    public boolean insertar(Grupo grupo) {
        try {
            PreparedStatement ps = ConexionDB.getInstancia().getConnection()
                    .prepareStatement(SQL_INSERTAR);
            ps.setString(1, grupo.getNombreGrupo());
            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    grupo.setIdGrupo(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[GrupoDAO] " + e.getMessage());
        }
        return false;
    }

    public Grupo buscarPorId(int idGrupo) {
        try {
            PreparedStatement ps = ConexionDB.getInstancia().getConnection()
                    .prepareStatement(SQL_BUSCAR_POR_ID);
            ps.setInt(1, idGrupo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("[GrupoDAO] " + e.getMessage());
        }
        return null;
    }

    public List<Grupo> listarTodos() {
        List<Grupo> lista = new ArrayList<>();
        try {
            Statement stmt = ConexionDB.getInstancia().getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(SQL_LISTAR_TODOS);
            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("[GrupoDAO] " + e.getMessage());
        }
        return lista;
    }

    private Grupo mapearResultSet(ResultSet rs) throws SQLException {
        return new Grupo(rs.getInt("idGrupo"), rs.getString("nombreGrupo"));
    }

}
