package co.edu.uniquindio.proyectodb.service;

import co.edu.uniquindio.proyectodb.dao.ConfederacionDAO;
import co.edu.uniquindio.proyectodb.dao.EquipoDAO;
import co.edu.uniquindio.proyectodb.dao.EquipoGrupoDAO;
import co.edu.uniquindio.proyectodb.dao.GrupoDAO;
import co.edu.uniquindio.proyectodb.model.Equipo;
import co.edu.uniquindio.proyectodb.model.Grupo;

import java.util.List;

/**
 * Service para la entidad Equipo.
 *
 * 1. CRUD de equipos con validaciones.
 * 2. Gestionar la asignación de equipos a grupos
 */
public class EquipoService {

    private final EquipoDAO equipoDAO;
    private final ConfederacionDAO confederacionDAO;
    private final EquipoGrupoDAO equipoGrupoDAO;
    private final GrupoDAO grupoDAO;

    public EquipoService() {
        this.equipoDAO = new EquipoDAO();
        this.confederacionDAO = new ConfederacionDAO();
        this.equipoGrupoDAO = new EquipoGrupoDAO();
        this.grupoDAO = new GrupoDAO();
    }

    // CREATE

    /**
     * Registra un nuevo equipo.
     * - Nombre y país obligatorios.
     * - La confederación indicada debe existir.
     *
     * @param equipo objeto Equipo con todos los campos completos
     * @return true si el registro fue exitoso
     */
    public boolean registrar(Equipo equipo) {
        if (!validar(equipo))
            return false;

        if (confederacionDAO.buscarPorId(equipo.getIdConfederacion()) == null) {
            System.out.println("[EquipoService] La confederación con id="
                    + equipo.getIdConfederacion() + " no existe.");
            return false;
        }

        boolean exito = equipoDAO.insertar(equipo);
        if (exito)
            System.out.println("[EquipoService] Equipo '" + equipo.getNombre() + "' registrado.");
        return exito;
    }

    // READ

    public Equipo buscarPorId(int idEquipo) {
        return equipoDAO.buscarPorId(idEquipo);
    }

    public List<Equipo> listarTodos() {
        return equipoDAO.listarTodos();
    }

    public List<Equipo> listarPorConfederacion(int idConfederacion) {
        return equipoDAO.listarPorConfederacion(idConfederacion);
    }

    // UPDATE

    /**
     * Actualiza un equipo existente.
     *
     * @param equipo objeto con los datos actualizados
     * @return true si la actualización fue exitosa
     */
    public boolean actualizar(Equipo equipo) {
        if (!validar(equipo))
            return false;

        if (equipoDAO.buscarPorId(equipo.getIdEquipo()) == null) {
            System.out.println("[EquipoService] No existe equipo con id=" + equipo.getIdEquipo());
            return false;
        }
        if (confederacionDAO.buscarPorId(equipo.getIdConfederacion()) == null) {
            System.out.println("[EquipoService] La confederación con id="
                    + equipo.getIdConfederacion() + " no existe.");
            return false;
        }

        boolean exito = equipoDAO.actualizar(equipo);
        if (exito)
            System.out.println("[EquipoService] Equipo '" + equipo.getNombre() + "' actualizado.");
        return exito;
    }

    // DELETE

    /**
     * Elimina un equipo.
     * La BD rechazará la operación si el equipo tiene jugadores, partidos
     * o asignaciones de grupo activas.
     *
     * @param idEquipo id del equipo a eliminar
     * @return true si la eliminación fue exitosa
     */
    public boolean eliminar(int idEquipo) {
        if (equipoDAO.buscarPorId(idEquipo) == null) {
            System.out.println("[EquipoService] No existe equipo con id=" + idEquipo);
            return false;
        }
        boolean exito = equipoDAO.eliminar(idEquipo);
        if (exito)
            System.out.println("[EquipoService] Equipo id=" + idEquipo + " eliminado.");
        return exito;
    }

    // GESTIÓN DE GRUPOS

    /**
     * Asigna un equipo a un grupo, validando que ambos existan
     * y que la asignación no esté ya registrada.
     *
     * @param idEquipo id del equipo
     * @param idGrupo  id del grupo
     * @return true si la asignación fue exitosa
     */
    public boolean asignarAGrupo(int idEquipo, int idGrupo) {
        if (equipoDAO.buscarPorId(idEquipo) == null) {
            System.out.println("[EquipoService] No existe equipo con id=" + idEquipo);
            return false;
        }
        if (grupoDAO.buscarPorId(idGrupo) == null) {
            System.out.println("[EquipoService] No existe grupo con id=" + idGrupo);
            return false;
        }
        if (equipoGrupoDAO.existeAsignacion(idEquipo, idGrupo)) {
            System.out.println("[EquipoService] El equipo ya está asignado a ese grupo.");
            return false;
        }
        boolean exito = equipoGrupoDAO.asignarEquipoAGrupo(idEquipo, idGrupo);
        if (exito)
            System.out.println("[EquipoService] Equipo id=" + idEquipo
                    + " asignado al grupo id=" + idGrupo);
        return exito;
    }

    /**
     * Quita la asignación de un equipo a un grupo.
     *
     * @param idEquipo id del equipo
     * @param idGrupo  id del grupo
     * @return true si se eliminó la asignación
     */
    public boolean quitarDeGrupo(int idEquipo, int idGrupo) {
        if (!equipoGrupoDAO.existeAsignacion(idEquipo, idGrupo)) {
            System.out.println("[EquipoService] No existe esa asignación equipo-grupo.");
            return false;
        }
        boolean exito = equipoGrupoDAO.quitarEquipoDeGrupo(idEquipo, idGrupo);
        if (exito)
            System.out.println("[EquipoService] Asignación eliminada.");
        return exito;
    }

    /**
     * Lista los equipos que pertenecen a un grupo.
     */
    public List<Equipo> listarEquiposPorGrupo(int idGrupo) {
        return equipoGrupoDAO.listarEquiposPorGrupo(idGrupo);
    }

    /**
     * Lista los grupos a los que pertenece un equipo.
     */
    public List<Grupo> listarGruposDeEquipo(int idEquipo) {
        return equipoGrupoDAO.listarGruposPorEquipo(idEquipo);
    }

    // HELPERS PRIVADOS

    private boolean validar(Equipo e) {
        if (e.getNombre() == null || e.getNombre().isBlank()) {
            System.out.println("[EquipoService] El nombre del equipo es obligatorio.");
            return false;
        }
        if (e.getPais() == null || e.getPais().isBlank()) {
            System.out.println("[EquipoService] El país del equipo es obligatorio.");
            return false;
        }
        return true;
    }
}
