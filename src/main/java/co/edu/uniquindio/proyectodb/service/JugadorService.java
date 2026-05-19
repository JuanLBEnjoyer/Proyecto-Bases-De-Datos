package co.edu.uniquindio.proyectodb.service;

import co.edu.uniquindio.proyectodb.dao.EquipoDAO;
import co.edu.uniquindio.proyectodb.dao.JugadorDAO;
import co.edu.uniquindio.proyectodb.model.Equipo;
import co.edu.uniquindio.proyectodb.model.Jugador;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service para la entidad Jugador.
 */
public class JugadorService {

    private final JugadorDAO jugadorDAO;
    private final EquipoDAO equipoDAO;

    public JugadorService() {
        this.jugadorDAO = new JugadorDAO();
        this.equipoDAO = new EquipoDAO();
    }

    // CREATE

    /**
     * Registra un nuevo jugador con validaciones previas.
     * - Nombre obligatorio.
     * - Posición obligatoria.
     * - Peso entre 0 y 200 kg (exclusivo).
     * - Estatura entre 0 y 2.50 m (exclusivo).
     * - Valor de mercado >= 0.
     * - El equipo destino debe existir.
     *
     * @param jugador objeto con todos los campos completos
     * @return true si el jugador fue registrado correctamente
     */
    public boolean registrar(Jugador jugador) {
        if (!validar(jugador))
            return false;

        if (equipoDAO.buscarPorId(jugador.getIdEquipo()) == null) {
            System.out.println("[JugadorService] El equipo con id=" + jugador.getIdEquipo() + " no existe.");
            return false;
        }

        boolean exito = jugadorDAO.insertar(jugador);
        if (exito) {
            recalcularValorEquipo(jugador.getIdEquipo());
            System.out.println("[JugadorService] Jugador '" + jugador.getNombre() + "' registrado.");
        }
        return exito;
    }

    // READ

    public Jugador buscarPorId(int idJugador) {
        return jugadorDAO.buscarPorId(idJugador);
    }

    public List<Jugador> listarTodos() {
        return jugadorDAO.listarTodos();
    }

    public List<Jugador> listarPorEquipo(int idEquipo) {
        return jugadorDAO.listarPorEquipo(idEquipo);
    }

    public List<Jugador> listarPorPosicion(String posicion) {
        if (posicion == null || posicion.isBlank()) {
            System.out.println("[JugadorService] La posición no puede estar vacía.");
            return List.of();
        }
        return jugadorDAO.listarPorPosicion(posicion);
    }

    public List<Jugador> listarPorRangoValor(BigDecimal valorMinimo, BigDecimal valorMaximo) {
        if (valorMinimo == null || valorMaximo == null || valorMinimo.compareTo(valorMaximo) > 0) {
            System.out.println("[JugadorService] Rango de valor inválido.");
            return List.of();
        }
        return jugadorDAO.listarPorRangoValor(valorMinimo, valorMaximo);
    }

    public List<Jugador> listarMayoresDe(int edad) {
        if (edad < 0) {
            System.out.println("[JugadorService] La edad no puede ser negativa.");
            return List.of();
        }
        return jugadorDAO.listarMayoresDe(edad);
    }

    public List<Jugador> listarMenoresDe(int edad) {
        if (edad < 0) {
            System.out.println("[JugadorService] La edad no puede ser negativa.");
            return List.of();
        }
        return jugadorDAO.listarMenoresDe(edad);
    }

    // UPDATE

    /**
     * Actualiza un jugador existente.
     * Si cambió de equipo, recalcula el valor total de ambos equipos
     * 
     * @param jugador objeto con los datos actualizados y el id correcto
     * @return true si la actualización fue exitosa
     */
    public boolean actualizar(Jugador jugador) {
        if (!validar(jugador))
            return false;

        Jugador anterior = jugadorDAO.buscarPorId(jugador.getIdJugador());
        if (anterior == null) {
            System.out.println("[JugadorService] No existe un jugador con id=" + jugador.getIdJugador());
            return false;
        }

        if (equipoDAO.buscarPorId(jugador.getIdEquipo()) == null) {
            System.out.println("[JugadorService] El equipo con id=" + jugador.getIdEquipo() + " no existe.");
            return false;
        }

        boolean exito = jugadorDAO.actualizar(jugador);
        if (exito) {
            recalcularValorEquipo(jugador.getIdEquipo());
            if (anterior.getIdEquipo() != jugador.getIdEquipo()) {
                recalcularValorEquipo(anterior.getIdEquipo());
            }
            System.out.println("[JugadorService] Jugador '" + jugador.getNombre() + "' actualizado.");
        }
        return exito;
    }

    // DELETE

    /**
     * Elimina un jugador y recalcula el valor total de su equipo.
     *
     * @param idJugador id del jugador a eliminar
     * @return true si la eliminación fue exitosa
     */
    public boolean eliminar(int idJugador) {
        Jugador jugador = jugadorDAO.buscarPorId(idJugador);
        if (jugador == null) {
            System.out.println("[JugadorService] No existe un jugador con id=" + idJugador);
            return false;
        }

        boolean exito = jugadorDAO.eliminar(idJugador);
        if (exito) {
            recalcularValorEquipo(jugador.getIdEquipo());
            System.out.println("[JugadorService] Jugador eliminado (id=" + idJugador + ").");
        }
        return exito;
    }

    // HELPERS PRIVADOS

    /**
     * Valida las restricciones de negocio de un jugador.
     */
    private boolean validar(Jugador j) {
        if (j.getNombre() == null || j.getNombre().isBlank()) {
            System.out.println("[JugadorService] El nombre es obligatorio.");
            return false;
        }
        if (j.getPosicion() == null || j.getPosicion().isBlank()) {
            System.out.println("[JugadorService] La posición es obligatoria.");
            return false;
        }
        if (j.getFechaNacimiento() == null) {
            System.out.println("[JugadorService] La fecha de nacimiento es obligatoria.");
            return false;
        }
        if (j.getPeso() == null || j.getPeso().compareTo(BigDecimal.ZERO) <= 0
                || j.getPeso().compareTo(new BigDecimal("200")) >= 0) {
            System.out.println("[JugadorService] El peso debe estar entre 0 y 200 kg.");
            return false;
        }
        if (j.getEstatura() == null || j.getEstatura().compareTo(BigDecimal.ZERO) <= 0
                || j.getEstatura().compareTo(new BigDecimal("2.50")) >= 0) {
            System.out.println("[JugadorService] La estatura debe estar entre 0 y 2.50 m.");
            return false;
        }
        if (j.getValorMercado() == null || j.getValorMercado().compareTo(BigDecimal.ZERO) < 0) {
            System.out.println("[JugadorService] El valor de mercado no puede ser negativo.");
            return false;
        }
        return true;
    }

    /**
     * Suma los valores de mercado de todos los jugadores del equipo
     * y actualiza el campo valor_total_equipo en la BD.
     */
    private void recalcularValorEquipo(int idEquipo) {
        Equipo equipo = equipoDAO.buscarPorId(idEquipo);
        if (equipo == null)
            return;

        List<Jugador> jugadores = jugadorDAO.listarPorEquipo(idEquipo);
        BigDecimal total = jugadores.stream()
                .map(Jugador::getValorMercado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        equipo.setValorTotalEquipo(total);
        equipoDAO.actualizar(equipo);
        System.out.println("[JugadorService] Valor total del equipo id=" + idEquipo + " actualizado a " + total);
    }
}
