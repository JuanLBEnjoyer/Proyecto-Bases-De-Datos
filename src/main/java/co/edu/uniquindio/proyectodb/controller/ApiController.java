package co.edu.uniquindio.proyectodb.controller;

import co.edu.uniquindio.proyectodb.dao.ConfederacionDAO;
import co.edu.uniquindio.proyectodb.dao.EstadioDAO;
import co.edu.uniquindio.proyectodb.dao.GrupoDAO;
import co.edu.uniquindio.proyectodb.model.*;
import co.edu.uniquindio.proyectodb.model.valueobjects.TipoUsuario;
import co.edu.uniquindio.proyectodb.service.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE })
public class ApiController {

    private final AuthService authService = new AuthService();
    private final EquipoService equipoService = new EquipoService();
    private final JugadorService jugadorService = new JugadorService();
    private final DirectorTecnicoService dtService = new DirectorTecnicoService();
    private final PartidoService partidoService = new PartidoService();
    private final ReporteService reporteService = new ReporteService();

    private final ConfederacionDAO confederacionDAO = new ConfederacionDAO();
    private final GrupoDAO grupoDAO = new GrupoDAO();
    private final EstadioDAO estadioDAO = new EstadioDAO();

    // ==========================================
    // HELPERS PARA MAPEO
    // ==========================================

    private int obtenerOCrearIdConfederacion(String siglas) {
        if (siglas == null || siglas.isBlank()) {
            return 1;
        }
        List<Confederacion> list = confederacionDAO.listarTodos();
        for (Confederacion c : list) {
            if (c.getSiglas().equalsIgnoreCase(siglas.trim())) {
                return c.getIdConfederacion();
            }
        }
        Confederacion nueva = new Confederacion();
        nueva.setNombre(siglas.trim());
        nueva.setSiglas(siglas.trim().toUpperCase());
        if (confederacionDAO.insertar(nueva)) {
            return nueva.getIdConfederacion();
        }
        return 1;
    }

    private int obtenerOCrearIdGrupo(String nombreGrupo) {
        if (nombreGrupo == null || nombreGrupo.isBlank()) {
            return 1;
        }
        String normalizado = nombreGrupo.trim();
        if (normalizado.length() == 1) {
            normalizado = "Grupo " + normalizado.toUpperCase();
        }
        List<Grupo> list = grupoDAO.listarTodos();
        for (Grupo g : list) {
            if (g.getNombreGrupo().equalsIgnoreCase(normalizado)
                    || g.getNombreGrupo().equalsIgnoreCase(nombreGrupo.trim())) {
                return g.getIdGrupo();
            }
        }
        Grupo nuevo = new Grupo();
        nuevo.setNombreGrupo(normalizado);
        if (grupoDAO.insertar(nuevo)) {
            return nuevo.getIdGrupo();
        }
        return 1;
    }

    private int obtenerOCrearIdEstadio(String nombreEstadio) {
        if (nombreEstadio == null || nombreEstadio.isBlank()) {
            return 1;
        }
        List<Estadio> list = estadioDAO.listarTodos();
        for (Estadio e : list) {
            if (e.getNombre().equalsIgnoreCase(nombreEstadio.trim())) {
                return e.getIdEstadio();
            }
        }
        Estadio nuevo = new Estadio();
        nuevo.setNombre(nombreEstadio.trim());
        nuevo.setCapacidad(50000);
        nuevo.setIdCiudad(1); // Default ciudad ID
        if (estadioDAO.insertar(nuevo)) {
            return nuevo.getIdEstadio();
        }
        return 1;
    }

    private String getConfederacionSiglas(int idConfederacion) {
        Confederacion c = confederacionDAO.buscarPorId(idConfederacion);
        return c != null ? c.getSiglas() : "UEFA";
    }

    private String getGrupoSiglas(int idEquipo) {
        List<Grupo> grupos = equipoService.listarGruposDeEquipo(idEquipo);
        if (grupos != null && !grupos.isEmpty()) {
            return grupos.get(0).getNombreGrupo().replace("Grupo ", "").trim();
        }
        return "A";
    }

    private String getEstadioNombre(int idEstadio) {
        Estadio e = estadioDAO.buscarPorId(idEstadio);
        return e != null ? e.getNombre() : "Estadio";
    }

    private String getGrupoNombreById(int idGrupo) {
        Grupo g = grupoDAO.buscarPorId(idGrupo);
        if (g != null) {
            return g.getNombreGrupo().replace("Grupo ", "").trim();
        }
        return "A";
    }

    // ==========================================
    // AUTHENTICATION ENDPOINTS
    // ==========================================

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> creds) {
        String username = creds.get("username");
        String password = creds.get("password");
        Usuario usuario = authService.login(username, password);
        if (usuario != null) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", usuario.getIdUsuario());
            userMap.put("username", usuario.getNombreUsuario());
            userMap.put("role", usuario.getTipoUsuario().name());
            userMap.put("nombre", usuario.getNombreUsuario());

            resp.put("user", userMap);
            return ResponseEntity.ok(resp);
        }
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", "Credenciales incorrectas");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> data) {
        String username = data.get("username");
        String password = data.get("password");
        String roleStr = data.get("role");

        TipoUsuario tipo = TipoUsuario.Esporadico;
        try {
            if (roleStr != null) {
                tipo = TipoUsuario.valueOf(roleStr);
            }
        } catch (IllegalArgumentException e) {
            // fallback a Esporadico
        }

        // Para simplificar el alcance de registro sin login previo de Admin en el back:
        // Temporalmente permitimos el registro libre.
        boolean exito = authService.registrarUsuario(username, password, tipo);
        if (exito) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("message", "Usuario registrado exitosamente");
            return ResponseEntity.ok(resp);
        }
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", "El usuario ya existe o los datos son inválidos");
        return ResponseEntity.badRequest().body(error);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout() {
        authService.logout();
        return ResponseEntity.ok(Map.of("success", true));
    }

    // ==========================================
    // TEAM ENDPOINTS
    // ==========================================

    @GetMapping("/teams")
    public ResponseEntity<List<Map<String, Object>>> getTeams() {
        List<Equipo> todos = equipoService.listarTodos();
        List<Map<String, Object>> dtos = todos.stream().map(e -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", e.getIdEquipo());
            map.put("nombre", e.getNombre());
            map.put("pais", e.getPais());
            map.put("confederacion", getConfederacionSiglas(e.getIdConfederacion()));
            map.put("valorTotalEquipo", e.getValorTotalEquipo());
            map.put("grupo", getGrupoSiglas(e.getIdEquipo()));
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/teams/{id}")
    public ResponseEntity<?> getTeam(@PathVariable int id) {
        Equipo e = equipoService.buscarPorId(id);
        if (e != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", e.getIdEquipo());
            map.put("nombre", e.getNombre());
            map.put("pais", e.getPais());
            map.put("confederacion", getConfederacionSiglas(e.getIdConfederacion()));
            map.put("valorTotalEquipo", e.getValorTotalEquipo());
            map.put("grupo", getGrupoSiglas(e.getIdEquipo()));
            return ResponseEntity.ok(map);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/teams")
    public ResponseEntity<?> createTeam(@RequestBody Map<String, String> body) {
        Equipo e = new Equipo();
        e.setNombre(body.get("nombre"));
        e.setPais(body.get("pais"));
        e.setIdConfederacion(obtenerOCrearIdConfederacion(body.get("confederacion")));
        e.setValorTotalEquipo(BigDecimal.ZERO);

        boolean exito = equipoService.registrar(e);
        if (exito) {
            // Asignar grupo
            if (body.containsKey("grupo") && body.get("grupo") != null) {
                int idGrupo = obtenerOCrearIdGrupo(body.get("grupo"));
                equipoService.asignarAGrupo(e.getIdEquipo(), idGrupo);
            }
            Map<String, Object> map = new HashMap<>();
            map.put("id", e.getIdEquipo());
            map.put("nombre", e.getNombre());
            map.put("pais", e.getPais());
            map.put("confederacion", getConfederacionSiglas(e.getIdConfederacion()));
            map.put("valorTotalEquipo", e.getValorTotalEquipo());
            map.put("grupo", getGrupoSiglas(e.getIdEquipo()));
            return ResponseEntity.ok(map);
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Error al crear el equipo"));
    }

    @PutMapping("/teams/{id}")
    public ResponseEntity<?> updateTeam(@PathVariable int id, @RequestBody Map<String, String> body) {
        Equipo e = equipoService.buscarPorId(id);
        if (e == null)
            return ResponseEntity.notFound().build();

        e.setNombre(body.get("nombre"));
        e.setPais(body.get("pais"));
        e.setIdConfederacion(obtenerOCrearIdConfederacion(body.get("confederacion")));

        boolean exito = equipoService.actualizar(e);
        if (exito) {
            if (body.containsKey("grupo") && body.get("grupo") != null) {
                int idGrupo = obtenerOCrearIdGrupo(body.get("grupo"));
                // Limpiar previas asignaciones de grupo
                List<Grupo> gruposAnteriores = equipoService.listarGruposDeEquipo(id);
                for (Grupo ga : gruposAnteriores) {
                    equipoService.quitarDeGrupo(id, ga.getIdGrupo());
                }
                equipoService.asignarAGrupo(id, idGrupo);
            }
            Map<String, Object> map = new HashMap<>();
            map.put("id", e.getIdEquipo());
            map.put("nombre", e.getNombre());
            map.put("pais", e.getPais());
            map.put("confederacion", getConfederacionSiglas(e.getIdConfederacion()));
            map.put("valorTotalEquipo", e.getValorTotalEquipo());
            map.put("grupo", getGrupoSiglas(e.getIdEquipo()));
            return ResponseEntity.ok(map);
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Error al actualizar el equipo"));
    }

    @DeleteMapping("/teams/{id}")
    public ResponseEntity<?> deleteTeam(@PathVariable int id) {
        boolean exito = equipoService.eliminar(id);
        if (exito)
            return ResponseEntity.ok().build();
        return ResponseEntity.badRequest().body(
                Map.of("message", "No se puede eliminar el equipo porque tiene jugadores o dependencias activas"));
    }

    // ==========================================
    // PLAYER ENDPOINTS
    // ==========================================

    @GetMapping("/players")
    public ResponseEntity<List<Map<String, Object>>> getPlayers() {
        List<Jugador> todos = jugadorService.listarTodos();
        List<Map<String, Object>> dtos = todos.stream().map(p -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", p.getIdJugador());
            map.put("nombre", p.getNombre());
            map.put("posicion", p.getPosicion());
            map.put("fechaNacimiento", p.getFechaNacimiento() != null ? p.getFechaNacimiento().toString() : "");
            map.put("peso", p.getPeso());
            map.put("estatura", p.getEstatura());
            map.put("valorMercado", p.getValorMercado());
            map.put("idEquipo", p.getIdEquipo());
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/players/{id}")
    public ResponseEntity<?> getPlayer(@PathVariable int id) {
        Jugador p = jugadorService.buscarPorId(id);
        if (p != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", p.getIdJugador());
            map.put("nombre", p.getNombre());
            map.put("posicion", p.getPosicion());
            map.put("fechaNacimiento", p.getFechaNacimiento() != null ? p.getFechaNacimiento().toString() : "");
            map.put("peso", p.getPeso());
            map.put("estatura", p.getEstatura());
            map.put("valorMercado", p.getValorMercado());
            map.put("idEquipo", p.getIdEquipo());
            return ResponseEntity.ok(map);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/players/team/{teamId}")
    public ResponseEntity<List<Map<String, Object>>> getPlayersByTeam(@PathVariable int teamId) {
        List<Jugador> list = jugadorService.listarPorEquipo(teamId);
        List<Map<String, Object>> dtos = list.stream().map(p -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", p.getIdJugador());
            map.put("nombre", p.getNombre());
            map.put("posicion", p.getPosicion());
            map.put("fechaNacimiento", p.getFechaNacimiento() != null ? p.getFechaNacimiento().toString() : "");
            map.put("peso", p.getPeso());
            map.put("estatura", p.getEstatura());
            map.put("valorMercado", p.getValorMercado());
            map.put("idEquipo", p.getIdEquipo());
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/players")
    public ResponseEntity<?> createPlayer(@RequestBody Map<String, Object> body) {
        try {
            Jugador p = new Jugador();
            p.setNombre((String) body.get("nombre"));
            p.setPosicion((String) body.get("posicion"));

            String fnStr = (String) body.get("fechaNacimiento");
            if (fnStr != null && !fnStr.isBlank()) {
                p.setFechaNacimiento(LocalDate.parse(fnStr));
            }

            p.setPeso(new BigDecimal(body.get("peso").toString()));
            p.setEstatura(new BigDecimal(body.get("estatura").toString()));
            p.setValorMercado(new BigDecimal(body.get("valorMercado").toString()));
            p.setIdEquipo(Integer.parseInt(body.get("idEquipo").toString()));

            boolean exito = jugadorService.registrar(p);
            if (exito) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", p.getIdJugador());
                map.put("nombre", p.getNombre());
                map.put("posicion", p.getPosicion());
                map.put("fechaNacimiento", p.getFechaNacimiento().toString());
                map.put("peso", p.getPeso());
                map.put("estatura", p.getEstatura());
                map.put("valorMercado", p.getValorMercado());
                map.put("idEquipo", p.getIdEquipo());
                return ResponseEntity.ok(map);
            }
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Reglas de negocio no válidas para registrar jugador"));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("message", "Datos inválidos: " + ex.getMessage()));
        }
    }

    @PutMapping("/players/{id}")
    public ResponseEntity<?> updatePlayer(@PathVariable int id, @RequestBody Map<String, Object> body) {
        try {
            Jugador p = jugadorService.buscarPorId(id);
            if (p == null)
                return ResponseEntity.notFound().build();

            p.setNombre((String) body.get("nombre"));
            p.setPosicion((String) body.get("posicion"));

            String fnStr = (String) body.get("fechaNacimiento");
            if (fnStr != null && !fnStr.isBlank()) {
                p.setFechaNacimiento(LocalDate.parse(fnStr));
            }

            p.setPeso(new BigDecimal(body.get("peso").toString()));
            p.setEstatura(new BigDecimal(body.get("estatura").toString()));
            p.setValorMercado(new BigDecimal(body.get("valorMercado").toString()));
            p.setIdEquipo(Integer.parseInt(body.get("idEquipo").toString()));

            boolean exito = jugadorService.actualizar(p);
            if (exito) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", p.getIdJugador());
                map.put("nombre", p.getNombre());
                map.put("posicion", p.getPosicion());
                map.put("fechaNacimiento", p.getFechaNacimiento().toString());
                map.put("peso", p.getPeso());
                map.put("estatura", p.getEstatura());
                map.put("valorMercado", p.getValorMercado());
                map.put("idEquipo", p.getIdEquipo());
                return ResponseEntity.ok(map);
            }
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Reglas de negocio no válidas para actualizar jugador"));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("message", "Datos inválidos: " + ex.getMessage()));
        }
    }

    @DeleteMapping("/players/{id}")
    public ResponseEntity<?> deletePlayer(@PathVariable int id) {
        boolean exito = jugadorService.eliminar(id);
        if (exito)
            return ResponseEntity.ok().build();
        return ResponseEntity.badRequest().body(Map.of("message", "Error al eliminar el jugador"));
    }

    // ==========================================
    // MANAGER (DIRECTOR TECNICO) ENDPOINTS
    // ==========================================

    @GetMapping("/directors")
    public ResponseEntity<List<Map<String, Object>>> getDirectors() {
        List<DirectorTecnico> todos = dtService.listarTodos();
        List<Map<String, Object>> dtos = todos.stream().map(d -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", d.getIdDt());
            map.put("nombre", d.getNombre());
            map.put("nacionalidad", d.getNacionalidad());
            map.put("fechaNacimiento", d.getFechaNacimiento() != null ? d.getFechaNacimiento().toString() : "");
            map.put("idEquipo", d.getIdEquipo());
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/directors/{id}")
    public ResponseEntity<?> getDirector(@PathVariable int id) {
        DirectorTecnico d = dtService.buscarPorId(id);
        if (d != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", d.getIdDt());
            map.put("nombre", d.getNombre());
            map.put("nacionalidad", d.getNacionalidad());
            map.put("fechaNacimiento", d.getFechaNacimiento() != null ? d.getFechaNacimiento().toString() : "");
            map.put("idEquipo", d.getIdEquipo());
            return ResponseEntity.ok(map);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/directors")
    public ResponseEntity<?> createDirector(@RequestBody Map<String, Object> body) {
        try {
            DirectorTecnico d = new DirectorTecnico();
            d.setNombre((String) body.get("nombre"));
            d.setNacionalidad((String) body.get("nacionalidad"));

            String fnStr = (String) body.get("fechaNacimiento");
            if (fnStr != null && !fnStr.isBlank()) {
                d.setFechaNacimiento(LocalDate.parse(fnStr));
            }

            if (body.get("idEquipo") != null && !body.get("idEquipo").toString().isBlank()) {
                d.setIdEquipo(Integer.parseInt(body.get("idEquipo").toString()));
            }

            boolean exito = dtService.registrar(d);
            if (exito) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", d.getIdDt());
                map.put("nombre", d.getNombre());
                map.put("nacionalidad", d.getNacionalidad());
                map.put("fechaNacimiento", d.getFechaNacimiento().toString());
                map.put("idEquipo", d.getIdEquipo());
                return ResponseEntity.ok(map);
            }
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Error al registrar DT (el equipo ya tiene DT asignado o datos inválidos)"));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("message", "Datos inválidos: " + ex.getMessage()));
        }
    }

    @PutMapping("/directors/{id}")
    public ResponseEntity<?> updateDirector(@PathVariable int id, @RequestBody Map<String, Object> body) {
        try {
            DirectorTecnico d = dtService.buscarPorId(id);
            if (d == null)
                return ResponseEntity.notFound().build();

            d.setNombre((String) body.get("nombre"));
            d.setNacionalidad((String) body.get("nacionalidad"));

            String fnStr = (String) body.get("fechaNacimiento");
            if (fnStr != null && !fnStr.isBlank()) {
                d.setFechaNacimiento(LocalDate.parse(fnStr));
            }

            if (body.get("idEquipo") != null && !body.get("idEquipo").toString().isBlank()) {
                d.setIdEquipo(Integer.parseInt(body.get("idEquipo").toString()));
            }

            boolean exito = dtService.actualizar(d);
            if (exito) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", d.getIdDt());
                map.put("nombre", d.getNombre());
                map.put("nacionalidad", d.getNacionalidad());
                map.put("fechaNacimiento", d.getFechaNacimiento().toString());
                map.put("idEquipo", d.getIdEquipo());
                return ResponseEntity.ok(map);
            }
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Error al actualizar DT (el equipo ya tiene DT asignado o datos inválidos)"));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("message", "Datos inválidos: " + ex.getMessage()));
        }
    }

    @DeleteMapping("/directors/{id}")
    public ResponseEntity<?> deleteDirector(@PathVariable int id) {
        boolean exito = dtService.eliminar(id);
        if (exito)
            return ResponseEntity.ok().build();
        return ResponseEntity.badRequest().body(Map.of("message", "Error al eliminar DT"));
    }

    // ==========================================
    // MATCH ENDPOINTS
    // ==========================================

    @GetMapping("/matches")
    public ResponseEntity<List<Map<String, Object>>> getMatches() {
        List<Partido> todos = partidoService.listarTodos();
        List<Map<String, Object>> dtos = todos.stream().map(m -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", m.getIdPartido());
            map.put("idGrupo", getGrupoNombreById(m.getIdGrupo()));
            map.put("idEquipoLocal", m.getIdEquipoLocal());
            map.put("idEquipoVisitante", m.getIdEquipoVisitante());
            map.put("idEstadio", getEstadioNombre(m.getIdEstadio()));
            map.put("golesLocal", m.getGolesLocal());
            map.put("golesVisitante", m.getGolesVisitante());
            map.put("fechaPartido", m.getFechaHora() != null ? m.getFechaHora().toLocalDate().toString() : "");
            map.put("etapa", "Fase de Grupos"); // default
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/matches/{id}")
    public ResponseEntity<?> getMatch(@PathVariable int id) {
        Partido m = partidoService.buscarPorId(id);
        if (m != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", m.getIdPartido());
            map.put("idGrupo", getGrupoNombreById(m.getIdGrupo()));
            map.put("idEquipoLocal", m.getIdEquipoLocal());
            map.put("idEquipoVisitante", m.getIdEquipoVisitante());
            map.put("idEstadio", getEstadioNombre(m.getIdEstadio()));
            map.put("golesLocal", m.getGolesLocal());
            map.put("golesVisitante", m.getGolesVisitante());
            map.put("fechaPartido", m.getFechaHora() != null ? m.getFechaHora().toLocalDate().toString() : "");
            map.put("etapa", "Fase de Grupos");
            return ResponseEntity.ok(map);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/matches")
    public ResponseEntity<?> createMatch(@RequestBody Map<String, Object> body) {
        try {
            Partido m = new Partido();
            m.setIdGrupo(obtenerOCrearIdGrupo((String) body.get("idGrupo")));
            m.setIdEquipoLocal(Integer.parseInt(body.get("idEquipoLocal").toString()));
            m.setIdEquipoVisitante(Integer.parseInt(body.get("idEquipoVisitante").toString()));
            m.setIdEstadio(obtenerOCrearIdEstadio((String) body.get("idEstadio")));
            m.setGolesLocal(Integer.parseInt(body.get("golesLocal").toString()));
            m.setGolesVisitante(Integer.parseInt(body.get("golesVisitante").toString()));

            String fpStr = (String) body.get("fechaPartido");
            if (fpStr != null && !fpStr.isBlank()) {
                m.setFechaHora(LocalDate.parse(fpStr).atStartOfDay());
            } else {
                m.setFechaHora(LocalDateTime.now());
            }

            boolean exito = partidoService.registrar(m);
            if (exito) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", m.getIdPartido());
                map.put("idGrupo", getGrupoNombreById(m.getIdGrupo()));
                map.put("idEquipoLocal", m.getIdEquipoLocal());
                map.put("idEquipoVisitante", m.getIdEquipoVisitante());
                map.put("idEstadio", getEstadioNombre(m.getIdEstadio()));
                map.put("golesLocal", m.getGolesLocal());
                map.put("golesVisitante", m.getGolesVisitante());
                map.put("fechaPartido", m.getFechaHora().toLocalDate().toString());
                map.put("etapa", "Fase de Grupos");
                return ResponseEntity.ok(map);
            }
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Error al registrar partido (mismas validaciones de negocio del Service)"));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("message", "Datos inválidos: " + ex.getMessage()));
        }
    }

    @PutMapping("/matches/{id}")
    public ResponseEntity<?> updateMatch(@PathVariable int id, @RequestBody Map<String, Object> body) {
        try {
            Partido m = partidoService.buscarPorId(id);
            if (m == null)
                return ResponseEntity.notFound().build();

            m.setIdGrupo(obtenerOCrearIdGrupo((String) body.get("idGrupo")));
            m.setIdEquipoLocal(Integer.parseInt(body.get("idEquipoLocal").toString()));
            m.setIdEquipoVisitante(Integer.parseInt(body.get("idEquipoVisitante").toString()));
            m.setIdEstadio(obtenerOCrearIdEstadio((String) body.get("idEstadio")));
            m.setGolesLocal(Integer.parseInt(body.get("golesLocal").toString()));
            m.setGolesVisitante(Integer.parseInt(body.get("golesVisitante").toString()));

            String fpStr = (String) body.get("fechaPartido");
            if (fpStr != null && !fpStr.isBlank()) {
                m.setFechaHora(LocalDate.parse(fpStr).atStartOfDay());
            }

            boolean exito = partidoService.actualizar(m);
            if (exito) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", m.getIdPartido());
                map.put("idGrupo", getGrupoNombreById(m.getIdGrupo()));
                map.put("idEquipoLocal", m.getIdEquipoLocal());
                map.put("idEquipoVisitante", m.getIdEquipoVisitante());
                map.put("idEstadio", getEstadioNombre(m.getIdEstadio()));
                map.put("golesLocal", m.getGolesLocal());
                map.put("golesVisitante", m.getGolesVisitante());
                map.put("fechaPartido", m.getFechaHora().toLocalDate().toString());
                map.put("etapa", "Fase de Grupos");
                return ResponseEntity.ok(map);
            }
            return ResponseEntity.badRequest().body(Map.of("message", "Error al actualizar partido"));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("message", "Datos inválidos: " + ex.getMessage()));
        }
    }

    @DeleteMapping("/matches/{id}")
    public ResponseEntity<?> deleteMatch(@PathVariable int id) {
        boolean exito = partidoService.eliminar(id);
        if (exito)
            return ResponseEntity.ok().build();
        return ResponseEntity.badRequest().body(Map.of("message", "Error al eliminar partido"));
    }

    // ==========================================
    // BITACORA ENDPOINTS
    // ==========================================

    @GetMapping("/bitacora")
    public ResponseEntity<List<Map<String, Object>>> getBitacora() {
        List<Bitacora> list = reporteService.todasLasSesiones();
        List<Map<String, Object>> dtos = list.stream().map(b -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", b.getIdRegistro());
            map.put("idUsuario", b.getIdUsuario());
            map.put("nombreUsuario", "Usuario " + b.getIdUsuario()); // fallback o consultar nombre en base
            map.put("fechaIngreso", b.getFechaHoraIngreso() != null ? b.getFechaHoraIngreso().toString() : "");
            map.put("fechaSalida", b.getFechaHoraSalida() != null ? b.getFechaHoraSalida().toString() : null);
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
