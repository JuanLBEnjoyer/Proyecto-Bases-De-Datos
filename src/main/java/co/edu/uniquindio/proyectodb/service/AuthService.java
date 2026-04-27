package co.edu.uniquindio.proyectodb.service;

import co.edu.uniquindio.proyectodb.dao.BitacoraDAO;
import co.edu.uniquindio.proyectodb.dao.UsuarioDAO;
import co.edu.uniquindio.proyectodb.model.Usuario;
import co.edu.uniquindio.proyectodb.model.valueobjects.TipoUsuario;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Service de autenticación y gestión de usuarios.
 *
 * RESPONSABILIDADES:
 * 1. Hashear contraseñas con SHA-256 antes de guardarlas o compararlas.
 * 2. Autenticar usuarios y registrar el ingreso en la bitácora.
 * 3. Cerrar sesión y registrar la salida en la bitácora.
 * 4. Registrar nuevos usuarios con validaciones previas.
 *
 */
public class AuthService {

    // DAOs que este service necesita para acceder a la BD
    private final UsuarioDAO usuarioDAO;
    private final BitacoraDAO bitacoraDAO;

    /*
     * ESTADO DE SESIÓN
     * Estas variables guardan la información del usuario autenticado.
     * Son null cuando no hay nadie conectado.
     *
     * usuarioActual → el objeto Usuario que hizo login
     * idRegistroBitacora → el ID del registro de ingreso en Bitacora,
     * necesario para actualizar la fecha de salida al logout
     */
    private Usuario usuarioActual;
    private int idRegistroBitacora = -1;

    // CONSTRUCTOR

    /**
     * El Service crea sus propios DAOs al instanciarse.
     */
    public AuthService() {
        this.usuarioDAO = new UsuarioDAO();
        this.bitacoraDAO = new BitacoraDAO();
    }

    // HASHING DE CONTRASEÑAS

    /**
     * Convierte una contraseña en texto plano a su hash SHA-256.
     *
     * Por ejemplo:
     * hashear("admin123") →
     * "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918"
     *
     * @param contrasenaPlana contraseña como la escribe el usuario
     * @return hash SHA-256 en formato hexadecimal (64 caracteres)
     */
    public String hashear(String contrasenaPlana) {
        try {
            /*
             * MessageDigest es la clase de Java para algoritmos de hash.
             * getInstance("SHA-256") selecciona el algoritmo.
             */
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            /*
             * digest() recibe bytes. Convertimos el String a bytes usando
             * UTF-8 para garantizar consistencia.
             */
            byte[] hashBytes = digest.digest(
                    contrasenaPlana.getBytes(StandardCharsets.UTF_8));

            /*
             * Convertir bytes a hexadecimal:
             * En hexadecimal, cada byte se representa con exactamente 2 caracteres.
             *
             * String.format("%02x", b & 0xff):
             * - %02x = formato hexadecimal con mínimo 2 dígitos
             * - b & 0xff = convierte el byte con signo a entero sin signo (0-255)
             */
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b & 0xff));
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 no disponible en esta JVM", e);
        }
    }

    // LOGIN

    /**
     * Autentica un usuario y registra su ingreso en la bitácora.
     *
     * 1. Valida que los campos no estén vacíos.
     * 2. Hashea la contraseña ingresada.
     * 3. Busca en la BD un usuario con ese nombre Y ese hash.
     * 4. Si existe → guarda en sesión y registra en bitácora.
     * 5. Si no existe → retorna null.
     *
     * @param nombreUsuario   nombre de usuario ingresado
     * @param contrasenaPlana contraseña en texto plano ingresada por el usuario
     * @return el Usuario autenticado, o null si las credenciales son incorrectas
     */
    public Usuario login(String nombreUsuario, String contrasenaPlana) {

        // Validación básica de campos no vacíos
        if (nombreUsuario == null || nombreUsuario.isBlank() ||
                contrasenaPlana == null || contrasenaPlana.isBlank()) {
            System.out.println("[Auth] Usuario y contraseña son obligatorios.");
            return null;
        }

        // Hashea la contraseña antes de compararla con la BD
        String hash = hashear(contrasenaPlana);

        // Busca en la BD un usuario con ese nombre y ese hash.
        Usuario usuario = usuarioDAO.buscarPorCredenciales(nombreUsuario, hash);

        if (usuario == null) {
            System.out.println("[Auth] Credenciales incorrectas.");
            return null;
        }

        // Guarda el usuario en sesión
        this.usuarioActual = usuario;

        /*
         * Registra el ingreso en la bitácora.
         * Guarda el ID del registro para poder actualizarlo al hacer logout.
         * Si registrarIngreso falla (-1), la sesión igual es válida,
         * solo no quedará registrada la hora de entrada.
         */
        this.idRegistroBitacora = bitacoraDAO.registrarIngreso(usuario.getIdUsuario());

        System.out.println("[Auth] Bienvenido, " + usuario.getNombreUsuario() +
                " (" + usuario.getTipoUsuario() + ")");
        return usuario;
    }

    // LOGOUT

    /**
     * Cierra la sesión del usuario actual y registra su salida en la bitácora.
     *
     * 1. Verifica que haya una sesión activa.
     * 2. Registra la hora de salida en la bitácora.
     * 3. Limpia las variables de sesión (usuarioActual = null).
     */
    public void logout() {
        if (usuarioActual == null) {
            System.out.println("[Auth] No hay sesión activa.");
            return;
        }

        // Registra la salida en la bitácora si tenemos un ID de registro válido
        if (idRegistroBitacora != -1) {
            bitacoraDAO.registrarSalida(idRegistroBitacora);
        }

        System.out.println("[Auth] Sesión cerrada para: " + usuarioActual.getNombreUsuario());

        // Limpia el estado de sesión
        this.usuarioActual = null;
        this.idRegistroBitacora = -1;
    }

    // REGISTRO DE NUEVOS USUARIOS

    /**
     * Registra un nuevo usuario en el sistema con validaciones previas.
     *
     * Reglas de negocio:
     * 1. Nombre de usuario no puede estar vacío.
     * 2. Contraseña debe tener al menos 6 caracteres.
     * 3. El nombre de usuario no puede estar ya registrado.
     * 4. Solo un Administrador puede crear usuarios de tipo Administrador.
     *
     * @param nombreUsuario   nombre de usuario deseado
     * @param contrasenaPlana contraseña en texto plano
     * @param tipo            tipo de usuario a crear
     * @return true si el registro fue exitoso
     */
    public boolean registrarUsuario(String nombreUsuario, String contrasenaPlana, TipoUsuario tipo) {

        // 1: campos no vacíos
        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            System.out.println("[Auth] El nombre de usuario no puede estar vacío.");
            return false;
        }

        // 2: longitud mínima de contraseña
        if (contrasenaPlana == null || contrasenaPlana.length() < 6) {
            System.out.println("[Auth] La contraseña debe tener al menos 6 caracteres.");
            return false;
        }

        // 3: nombre de usuario único
        if (usuarioDAO.buscarPorNombre(nombreUsuario) != null) {
            System.out.println("[Auth] El nombre de usuario '" + nombreUsuario + "' ya está registrado.");
            return false;
        }

        /*
         * 4: solo un Administrador puede crear otro Administrador.
         *
         * hayAdminActivo() verifica si la sesión actual es de un Administrador.
         * Si no hay sesión activa o el usuario no es Admin, no puede crear Admins.
         */
        if (tipo == TipoUsuario.Administrador && !hayAdminActivo()) {
            System.out.println("[Auth] Solo un Administrador puede crear otros Administradores.");
            return false;
        }

        // Hashear la contraseña antes de guardarla
        String hash = hashear(contrasenaPlana);

        // Crear el objeto y persistirlo en la BD
        Usuario nuevo = new Usuario();
        nuevo.setNombreUsuario(nombreUsuario);
        nuevo.setContrasenaHash(hash);
        nuevo.setTipoUsuario(tipo);

        boolean exito = usuarioDAO.insertar(nuevo);
        if (exito) {
            System.out.println("[Auth] Usuario '" + nombreUsuario + "' registrado exitosamente.");
        }
        return exito;
    }

    // MÉTODOS DE CONSULTA DE SESIÓN

    /**
     * Retorna el usuario que tiene sesión activa actualmente.
     *
     * @return el Usuario autenticado, o null si no hay sesión
     */
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    /**
     * Indica si hay algún usuario con sesión activa.
     *
     * @return true si hay sesión activa
     */
    public boolean haySesionActiva() {
        return usuarioActual != null;
    }

    /**
     * Indica si el usuario actual es Administrador.
     * Útil para controlar acceso a funciones restringidas en la UI.
     *
     * @return true si hay sesión activa Y el usuario es Administrador
     */
    public boolean hayAdminActivo() {
        return haySesionActiva() &&
                usuarioActual.getTipoUsuario() == TipoUsuario.Administrador;
    }

    /**
     * Verifica si el usuario actual tiene al menos un tipo de acceso dado.
     *
     * JERARQUÍA DE ACCESO:
     * Administrador > Tradicional > Esporadico
     *
     * @param tipoRequerido tipo mínimo requerido para acceder
     * @return true si el usuario actual tiene ese nivel de acceso o superior
     */
    public boolean tieneAcceso(TipoUsuario tipoRequerido) {
        if (!haySesionActiva())
            return false;

        /*
         * Comparamos usando el ordinal del enum.
         * En el enum TipoUsuario: Administrador=0, Tradicional=1, Esporadico=2
         *
         * Un ordinal MENOR significa MAYOR privilegio.
         * Si el ordinal del usuario es <= al requerido, tiene acceso.
         */
        return usuarioActual.getTipoUsuario().ordinal() <= tipoRequerido.ordinal();
    }
}
