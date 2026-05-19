package co.edu.uniquindio.proyectodb.service;

import co.edu.uniquindio.proyectodb.dao.DirectorTecnicoDAO;
import co.edu.uniquindio.proyectodb.dao.EquipoDAO;
import co.edu.uniquindio.proyectodb.model.DirectorTecnico;

import java.util.List;

/**
 * Service para la entidad DirectorTecnico.
 * - Validar datos antes de insertar o actualizar.
 * - Garantizar que un equipo no tenga más de un DT.
 * - Delegar consultas especializadas al DirectorTecnicoDAO.
 */
public class DirectorTecnicoService {

    private final DirectorTecnicoDAO dtDAO;
    private final EquipoDAO equipoDAO;

    public DirectorTecnicoService() {
        this.dtDAO = new DirectorTecnicoDAO();
        this.equipoDAO = new EquipoDAO();
    }

    // CREATE

    /**
     * Registra un nuevo Director Técnico.
     *
     * Reglas de negocio:
     * - Nombre y nacionalidad obligatorios.
     * - Fecha de nacimiento obligatoria.
     * - El equipo debe existir.
     * - El equipo no debe tener ya un DT asignado.
     *
     * @param dt objeto DirectorTecnico con todos los campos completos
     * @return true si el registro fue exitoso
     */
    public boolean registrar(DirectorTecnico dt) {
        if (!validar(dt))
            return false;

        if (equipoDAO.buscarPorId(dt.getIdEquipo()) == null) {
            System.out.println("[DTService] No existe el equipo con id=" + dt.getIdEquipo());
            return false;
        }

        // Un equipo solo puede tener un DT.
        List<DirectorTecnico> actuales = dtDAO.listarPorEquipo(dt.getIdEquipo());
        if (!actuales.isEmpty()) {
            System.out.println("[DTService] El equipo id=" + dt.getIdEquipo()
                    + " ya tiene un Director Técnico asignado.");
            return false;
        }

        boolean exito = dtDAO.insertar(dt);
        if (exito)
            System.out.println("[DTService] Director Técnico '" + dt.getNombre() + "' registrado.");
        return exito;
    }

    // READ

    public DirectorTecnico buscarPorId(int idDt) {
        return dtDAO.buscarPorId(idDt);
    }

    public List<DirectorTecnico> listarTodos() {
        return dtDAO.listarTodos();
    }

    public List<DirectorTecnico> listarPorEquipo(int idEquipo) {
        return dtDAO.listarPorEquipo(idEquipo);
    }

    public List<DirectorTecnico> listarPorNacionalidad(String nacionalidad) {
        if (nacionalidad == null || nacionalidad.isBlank()) {
            System.out.println("[DTService] La nacionalidad no puede estar vacía.");
            return List.of();
        }
        return dtDAO.listarPorNacionalidad(nacionalidad);
    }

    public List<DirectorTecnico> listarMayoresDe(int edad) {
        if (edad < 0) {
            System.out.println("[DTService] La edad no puede ser negativa.");
            return List.of();
        }
        return dtDAO.listarMayoresDe(edad);
    }

    public List<DirectorTecnico> listarMenoresDe(int edad) {
        if (edad < 0) {
            System.out.println("[DTService] La edad no puede ser negativa.");
            return List.of();
        }
        return dtDAO.listarMenoresDe(edad);
    }

    // UPDATE

    /**
     * Actualiza un Director Técnico existente.
     * Si cambia de equipo, valida que el nuevo equipo no tenga ya un DT.
     *
     * @param dt objeto con los datos actualizados
     * @return true si la actualización fue exitosa
     */
    public boolean actualizar(DirectorTecnico dt) {
        if (!validar(dt))
            return false;

        DirectorTecnico anterior = dtDAO.buscarPorId(dt.getIdDt());
        if (anterior == null) {
            System.out.println("[DTService] No existe DT con id=" + dt.getIdDt());
            return false;
        }
        if (equipoDAO.buscarPorId(dt.getIdEquipo()) == null) {
            System.out.println("[DTService] No existe el equipo con id=" + dt.getIdEquipo());
            return false;
        }

        // Si cambió de equipo, verificar que el nuevo equipo no tenga DT
        if (anterior.getIdEquipo() != dt.getIdEquipo()) {
            List<DirectorTecnico> actuales = dtDAO.listarPorEquipo(dt.getIdEquipo());
            if (!actuales.isEmpty()) {
                System.out.println("[DTService] El equipo id=" + dt.getIdEquipo()
                        + " ya tiene un Director Técnico asignado.");
                return false;
            }
        }

        boolean exito = dtDAO.actualizar(dt);
        if (exito)
            System.out.println("[DTService] Director Técnico '" + dt.getNombre() + "' actualizado.");
        return exito;
    }

    // DELETE

    /**
     * Elimina un Director Técnico.
     *
     * @param idDt id del DT a eliminar
     * @return true si la eliminación fue exitosa
     */
    public boolean eliminar(int idDt) {
        if (dtDAO.buscarPorId(idDt) == null) {
            System.out.println("[DTService] No existe DT con id=" + idDt);
            return false;
        }
        boolean exito = dtDAO.eliminar(idDt);
        if (exito)
            System.out.println("[DTService] Director Técnico id=" + idDt + " eliminado.");
        return exito;
    }

    // HELPERS

    private boolean validar(DirectorTecnico dt) {
        if (dt.getNombre() == null || dt.getNombre().isBlank()) {
            System.out.println("[DTService] El nombre es obligatorio.");
            return false;
        }
        if (dt.getNacionalidad() == null || dt.getNacionalidad().isBlank()) {
            System.out.println("[DTService] La nacionalidad es obligatoria.");
            return false;
        }
        if (dt.getFechaNacimiento() == null) {
            System.out.println("[DTService] La fecha de nacimiento es obligatoria.");
            return false;
        }
        return true;
    }
}
