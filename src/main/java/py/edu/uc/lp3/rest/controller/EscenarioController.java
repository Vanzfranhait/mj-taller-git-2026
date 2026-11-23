package py.edu.uc.lp3.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp3.domain.Jugada;
import py.edu.uc.lp3.rest.dto.EscenarioDisparoRequest;
import py.edu.uc.lp3.service.EscenarioService;

import java.util.List;

@Tag(name = "Escenario POO-09", description = "Disparo coordinado con validación cruzada A2 + A3")
@RestController
@RequestMapping("/api/escenario")
public class EscenarioController {

    private final EscenarioService escenarioService;

    public EscenarioController(EscenarioService escenarioService) {
        this.escenarioService = escenarioService;
    }

    @Operation(
            summary = "Disparo coordinado (escenario POO-09)",
            description = """
                    Escenario que combina las reglas de dos compañeros:
                    1) A2 valida munición: POST /api/reglas/disparo con {modelo, carga}.
                    2) A3 valida vida/impacto: POST /api/reglas/impacto con {modelo, distancia, vida, blindaje}.
                    Si AMBAS aceptan, la jugada se persiste en el H2 propio (GET /api/escenario/jugadas la muestra).
                    Si alguna rechaza, la base queda igual y el mensaje indica de quién fue el rechazo.
                    Si A2/A3 no están disponibles, se usa un fallback local y el campo origenA2/origenA3 queda en FALLBACK.
                    """
    )
    @ApiResponse(responseCode = "201", description = "Ambas validaciones aceptaron; la jugada quedó persistida")
    @ApiResponse(responseCode = "400", description = "A2 o A3 rechazaron la jugada (ReglaDeJuegoException)",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(example = "{\"error\":\"regla_de_juego\",\"mensaje\":\"A2 rechazó: ...\"}")))
    @PostMapping("/disparo")
    public ResponseEntity<Jugada> disparoCoordinado(@RequestBody EscenarioDisparoRequest req) {
        Jugada jugada = escenarioService.ejecutarDisparoCoordinado(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(jugada);
    }

    @Operation(summary = "Listar jugadas persistidas",
            description = "Devuelve todas las jugadas que pasaron las dos validaciones y quedaron en H2.")
    @ApiResponse(responseCode = "200", description = "Lista de jugadas")
    @GetMapping("/jugadas")
    public List<Jugada> listarJugadas() {
        return escenarioService.listarJugadas();
    }
}
