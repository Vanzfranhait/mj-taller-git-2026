package py.edu.uc.lp3.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp3.exception.ReglaDeJuegoException;
import py.edu.uc.lp3.rest.dto.DisparoRapidoRequest;
import py.edu.uc.lp3.rest.dto.DisparoRapidoResponse;

@Tag(name = "Reglas (consulta pura)", description = "Validaciones sin efectos secundarios sobre la base")
@RestController
@RequestMapping("/api/reglas")
public class ReglasController {

    @Operation(summary = "Regla 2: ¿puede hacer un disparo rápido?",
            description = "Consulta pura, no modifica la base. Acepta si estabilidad > 0.7 y municionActual >= 2.")
    @ApiResponse(responseCode = "200", description = "La regla acepta el disparo rápido")
    @ApiResponse(responseCode = "400", description = "La regla rechaza",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(example = "{\"error\":\"regla_de_juego\",\"mensaje\":\"El francotirador no está estabilizado\"}")))
    @PostMapping("/disparo-rapido")
    public ResponseEntity<DisparoRapidoResponse> disparoRapido(@RequestBody DisparoRapidoRequest req) {
        if (req.estabilidad() == null || req.estabilidad() <= 0.7) {
            throw new ReglaDeJuegoException("El francotirador no está estabilizado");
        }
        if (req.municionActual() == null || req.municionActual() < 2) {
            throw new ReglaDeJuegoException("No hay munición suficiente para el disparo rápido");
        }
        return ResponseEntity.ok(new DisparoRapidoResponse(true, req.municionActual() - 2, 0.5));
    }
}
