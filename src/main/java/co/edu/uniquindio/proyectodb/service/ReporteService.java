package co.edu.uniquindio.proyectodb.service;

import co.edu.uniquindio.proyectodb.dao.BitacoraDAO;
import co.edu.uniquindio.proyectodb.dao.DirectorTecnicoDAO;
import co.edu.uniquindio.proyectodb.dao.EquipoDAO;
import co.edu.uniquindio.proyectodb.dao.EquipoGrupoDAO;
import co.edu.uniquindio.proyectodb.dao.JugadorDAO;
import co.edu.uniquindio.proyectodb.dao.PartidoDAO;
import co.edu.uniquindio.proyectodb.model.Bitacora;
import co.edu.uniquindio.proyectodb.model.DirectorTecnico;
import co.edu.uniquindio.proyectodb.model.Equipo;
import co.edu.uniquindio.proyectodb.model.Jugador;
import co.edu.uniquindio.proyectodb.model.Partido;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service de reportes.
 * REPORTES DISPONIBLES:
 * — Jugadores: por equipo, por posición, por rango de valor, por edad.
 * — Directores Técnicos: por nacionalidad, por edad.
 * — Equipos: por confederación, por grupo.
 * — Partidos: por grupo, por equipo.
 * — Bitácora: todas las sesiones, sesiones activas, sesiones por usuario.
 */
public class ReporteService {

    private final JugadorDAO jugadorDAO;
    private final DirectorTecnicoDAO dtDAO;
    private final EquipoDAO equipoDAO;
    private final EquipoGrupoDAO equipoGrupoDAO;
    private final PartidoDAO partidoDAO;
    private final BitacoraDAO bitacoraDAO;

    public ReporteService() {
        this.jugadorDAO = new JugadorDAO();
        this.dtDAO = new DirectorTecnicoDAO();
        this.equipoDAO = new EquipoDAO();
        this.equipoGrupoDAO = new EquipoGrupoDAO();
        this.partidoDAO = new PartidoDAO();
        this.bitacoraDAO = new BitacoraDAO();
    }

    // REPORTES DE JUGADORES

    /**
     * Todos los jugadores del sistema ordenados por nombre.
     */
    public List<Jugador> todosLosJugadores() {
        return jugadorDAO.listarTodos();
    }

    /**
     * Jugadores de un equipo específico.
     *
     * @param idEquipo id del equipo
     */
    public List<Jugador> jugadoresPorEquipo(int idEquipo) {
        return jugadorDAO.listarPorEquipo(idEquipo);
    }

    /**
     * Jugadores filtrando por posición, ordenados por valor de mercado DESC.
     *
     * @param posicion "Delantero", "Defensa", "Centrocampista", "Portero"
     */
    public List<Jugador> jugadoresPorPosicion(String posicion) {
        if (posicion == null || posicion.isBlank())
            return List.of();
        return jugadorDAO.listarPorPosicion(posicion);
    }

    /**
     * Jugadores cuyo valor de mercado esté entre valorMinimo y valorMaximo.
     *
     * @param valorMinimo valor mínimo (inclusive)
     * @param valorMaximo valor máximo (inclusive)
     */
    public List<Jugador> jugadoresPorRangoValor(BigDecimal valorMinimo, BigDecimal valorMaximo) {
        if (valorMinimo == null || valorMaximo == null
                || valorMinimo.compareTo(valorMaximo) > 0)
            return List.of();
        return jugadorDAO.listarPorRangoValor(valorMinimo, valorMaximo);
    }

    /**
     * Jugadores mayores de una edad dada, ordenados de mayor a menor edad.
     *
     * @param edad edad mínima (exclusiva)
     */
    public List<Jugador> jugadoresMayoresDe(int edad) {
        if (edad < 0)
            return List.of();
        return jugadorDAO.listarMayoresDe(edad);
    }

    /**
     * Jugadores menores de una edad dada, ordenados de menor a mayor edad.
     *
     * @param edad edad máxima (exclusiva)
     */
    public List<Jugador> jugadoresMenoresDe(int edad) {
        if (edad < 0)
            return List.of();
        return jugadorDAO.listarMenoresDe(edad);
    }

    // REPORTES DE DIRECTORES TÉCNICOS

    /**
     * Todos los Directores Técnicos ordenados por nombre.
     */
    public List<DirectorTecnico> todosLosDT() {
        return dtDAO.listarTodos();
    }

    /**
     * Director Técnico de un equipo específico.
     *
     * @param idEquipo id del equipo
     */
    public List<DirectorTecnico> dtPorEquipo(int idEquipo) {
        return dtDAO.listarPorEquipo(idEquipo);
    }

    /**
     * Directores Técnicos filtrados por nacionalidad.
     *
     * @param nacionalidad nacionalidad a filtrar
     */
    public List<DirectorTecnico> dtPorNacionalidad(String nacionalidad) {
        if (nacionalidad == null || nacionalidad.isBlank())
            return List.of();
        return dtDAO.listarPorNacionalidad(nacionalidad);
    }

    /**
     * Directores Técnicos mayores de una edad dada.
     *
     * @param edad edad mínima (exclusiva)
     */
    public List<DirectorTecnico> dtMayoresDe(int edad) {
        if (edad < 0)
            return List.of();
        return dtDAO.listarMayoresDe(edad);
    }

    /**
     * Directores Técnicos menores de una edad dada.
     *
     * @param edad edad máxima (exclusiva)
     */
    public List<DirectorTecnico> dtMenoresDe(int edad) {
        if (edad < 0)
            return List.of();
        return dtDAO.listarMenoresDe(edad);
    }

    // REPORTES DE EQUIPOS

    /**
     * Todos los equipos ordenados por nombre.
     */
    public List<Equipo> todosLosEquipos() {
        return equipoDAO.listarTodos();
    }

    /**
     * Equipos de una confederación específica.
     *
     * @param idConfederacion id de la confederación
     */
    public List<Equipo> equiposPorConfederacion(int idConfederacion) {
        return equipoDAO.listarPorConfederacion(idConfederacion);
    }

    /**
     * Equipos asignados a un grupo específico.
     *
     * @param idGrupo id del grupo
     */
    public List<Equipo> equiposPorGrupo(int idGrupo) {
        return equipoGrupoDAO.listarEquiposPorGrupo(idGrupo);
    }

    // REPORTES DE PARTIDOS

    /**
     * Todos los partidos ordenados por fecha.
     */
    public List<Partido> todosLosPartidos() {
        return partidoDAO.listarTodos();
    }

    /**
     * Partidos de un grupo específico ordenados por fecha.
     *
     * @param idGrupo id del grupo
     */
    public List<Partido> partidosPorGrupo(int idGrupo) {
        return partidoDAO.listarPorGrupo(idGrupo);
    }

    /**
     * Partidos en los que participó un equipo (como local o visitante).
     *
     * @param idEquipo id del equipo
     */
    public List<Partido> partidosPorEquipo(int idEquipo) {
        return partidoDAO.listarPorEquipo(idEquipo);
    }

    // REPORTES DE BITÁCORA

    /**
     * Todas las entradas y salidas del sistema, de más reciente a más antigua.
     */
    public List<Bitacora> todasLasSesiones() {
        return bitacoraDAO.listarTodos();
    }

    /**
     * Sesiones actualmente activas (sin fecha de salida registrada).
     */
    public List<Bitacora> sesionesActivas() {
        return bitacoraDAO.listarSesionesActivas();
    }

    /**
     * Historial de sesiones de un usuario específico.
     *
     * @param idUsuario id del usuario
     */
    public List<Bitacora> sesionesDeUsuario(int idUsuario) {
        return bitacoraDAO.listarPorUsuario(idUsuario);
    }
}