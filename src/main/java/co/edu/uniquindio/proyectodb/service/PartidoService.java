package co.edu.uniquindio.proyectodb.service;

import co.edu.uniquindio.proyectodb.dao.EquipoDAO;
import co.edu.uniquindio.proyectodb.dao.EquipoGrupoDAO;
import co.edu.uniquindio.proyectodb.dao.EstadioDAO;
import co.edu.uniquindio.proyectodb.dao.GrupoDAO;
import co.edu.uniquindio.proyectodb.dao.PartidoDAO;
import co.edu.uniquindio.proyectodb.model.Partido;

import java.util.List;

/**
 * Service para la entidad Partido.
 *
 * - Registrar partidos validando que los equipos existan,
 * pertenezcan al mismo grupo y el estadio exista.
 * - Registrar resultados con validaciones básicas.
 * - Delegar consultas al PartidoDAO.
 */
public class PartidoService {

    private final PartidoDAO partidoDAO;
    private final EquipoDAO equipoDAO;
    private final EstadioDAO estadioDAO;
    private final GrupoDAO grupoDAO;
    private final EquipoGrupoDAO equipoGrupoDAO;

    public PartidoService() {
        this.partidoDAO = new PartidoDAO();
        this.equipoDAO = new EquipoDAO();
        this.estadioDAO = new EstadioDAO();
        this.grupoDAO = new GrupoDAO();
        this.equipoGrupoDAO = new EquipoGrupoDAO();
    }

    // CREATE

    /**
     * Registra un nuevo partido con las siguientes validaciones:
     *
     * - Los dos equipos deben ser distintos.
     * - Los equipos deben existir.
     * - El estadio debe existir.
     * - El grupo debe existir.
     * - Los equipos deben estar asignados al mismo grupo indicado.
     * - La fecha es obligatoria.
     *
     * @param partido objeto Partido con todos los campos requeridos
     * @return true si el partido fue registrado
     */
    public boolean registrar(Partido partido) {

        if (partido.getFechaHora() == null) {
            System.out.println("[PartidoService] La fecha y hora del partido son obligatorias.");
            return false;
        }

        if (partido.getIdEquipoLocal() == partido.getIdEquipoVisitante()) {
            System.out.println("[PartidoService] El equipo local y visitante no pueden ser el mismo.");
            return false;
        }

        if (equipoDAO.buscarPorId(partido.getIdEquipoLocal()) == null) {
            System.out.println("[PartidoService] No existe el equipo local con id="
                    + partido.getIdEquipoLocal());
            return false;
        }
        if (equipoDAO.buscarPorId(partido.getIdEquipoVisitante()) == null) {
            System.out.println("[PartidoService] No existe el equipo visitante con id="
                    + partido.getIdEquipoVisitante());
            return false;
        }
        if (estadioDAO.buscarPorId(partido.getIdEstadio()) == null) {
            System.out.println("[PartidoService] No existe el estadio con id=" + partido.getIdEstadio());
            return false;
        }
        if (grupoDAO.buscarPorId(partido.getIdGrupo()) == null) {
            System.out.println("[PartidoService] No existe el grupo con id=" + partido.getIdGrupo());
            return false;
        }

        // Los equipos deben estar en el grupo indicado
        if (!equipoGrupoDAO.existeAsignacion(partido.getIdEquipoLocal(), partido.getIdGrupo())) {
            System.out.println("[PartidoService] El equipo local no pertenece al grupo indicado.");
            return false;
        }
        if (!equipoGrupoDAO.existeAsignacion(partido.getIdEquipoVisitante(), partido.getIdGrupo())) {
            System.out.println("[PartidoService] El equipo visitante no pertenece al grupo indicado.");
            return false;
        }

        boolean exito = partidoDAO.insertar(partido);
        if (exito)
            System.out.println("[PartidoService] Partido registrado con id=" + partido.getIdPartido());
        return exito;
    }

    // READ

    public Partido buscarPorId(int idPartido) {
        return partidoDAO.buscarPorId(idPartido);
    }

    public List<Partido> listarTodos() {
        return partidoDAO.listarTodos();
    }

    public List<Partido> listarPorGrupo(int idGrupo) {
        return partidoDAO.listarPorGrupo(idGrupo);
    }

    public List<Partido> listarPorEquipo(int idEquipo) {
        return partidoDAO.listarPorEquipo(idEquipo);
    }

    // UPDATE — resultado

    /**
     * Registra o actualiza el resultado de un partido.
     *
     * Reglas de negocio:
     * - El partido debe existir.
     * - Los goles no pueden ser negativos.
     *
     * @param idPartido      id del partido
     * @param golesLocal     goles del equipo local
     * @param golesVisitante goles del equipo visitante
     * @return true si la actualización fue exitosa
     */
    public boolean registrarResultado(int idPartido, int golesLocal, int golesVisitante) {
        if (partidoDAO.buscarPorId(idPartido) == null) {
            System.out.println("[PartidoService] No existe partido con id=" + idPartido);
            return false;
        }
        if (golesLocal < 0 || golesVisitante < 0) {
            System.out.println("[PartidoService] Los goles no pueden ser negativos.");
            return false;
        }

        boolean exito = partidoDAO.actualizarResultado(idPartido, golesLocal, golesVisitante);
        if (exito)
            System.out.println("[PartidoService] Resultado actualizado: "
                    + golesLocal + " - " + golesVisitante);
        return exito;
    }

    // DELETE

    /**
     * Elimina un partido.
     *
     * @param idPartido id del partido a eliminar
     * @return true si la eliminación fue exitosa
     */
    public boolean eliminar(int idPartido) {
        if (partidoDAO.buscarPorId(idPartido) == null) {
            System.out.println("[PartidoService] No existe partido con id=" + idPartido);
            return false;
        }
        boolean exito = partidoDAO.eliminar(idPartido);
        if (exito)
            System.out.println("[PartidoService] Partido id=" + idPartido + " eliminado.");
        return exito;
    }
}