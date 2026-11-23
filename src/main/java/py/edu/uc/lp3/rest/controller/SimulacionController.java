package py.edu.uc.lp3.rest.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.domain.Arma;
import py.edu.uc.lp3.domain.Francotirador;
import py.edu.uc.lp3.service.SimulacionService;

@Tag(name = "Simulación", description = "Reglas de negocio del juego (POO-08)")
@RestController
@RequestMapping("/api/db/simular")
public class SimulacionController {

    private final SimulacionService simulacionService;

    public SimulacionController(SimulacionService simulacionService) {
        this.simulacionService = simulacionService;
    }

    @Operation(
            summary = "Regla 1: explosión de granada",
            description = "Hace explotar una granada. Las armas con alcance menor al radio de la explosión pierden la mitad de su munición. El cambio se persiste en H2."
    )
    @ApiResponse(responseCode = "200", description = "Explosión aplicada; se devuelve la lista de armas afectadas")
    @ApiResponse(responseCode = "400", description = "El id no corresponde a una granada (ReglaDeJuegoException)",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(example = "{\"error\":\"regla_de_juego\",\"mensaje\":\"El arma indicada no es una granada\"}")))
    @ApiResponse(responseCode = "404", description = "Arma no encontrada",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(example = "{\"error\":\"not_found\",\"mensaje\":\"Arma no encontrada\"}")))
    @PostMapping("/explosion/{granadaId}")
    public ResponseEntity<List<Arma>> explosion(@PathVariable Long granadaId) {
        List<Arma> resultado = simulacionService.simularExplosion(granadaId);
        return ResponseEntity.ok(resultado);
    }

    @Operation(
            summary = "Regla 2: recarga rápida de francotirador",
            description = "Un francotirador con estabilidad > 0.7 puede disparar rápido: consume 2 de munición y queda inestable (estabilidad = 0.5). Si la estabilidad es ≤ 0.7 la regla se rechaza."
    )
    @ApiResponse(responseCode = "200", description = "Recarga rápida aplicada")
    @ApiResponse(responseCode = "400", description = "Estabilidad insuficiente (ReglaDeJuegoException)",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(example = "{\"error\":\"regla_de_juego\",\"mensaje\":\"El francotirador no tiene estabilidad suficiente\"}")))
    @ApiResponse(responseCode = "404", description = "Francotirador no encontrado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(example = "{\"error\":\"not_found\",\"mensaje\":\"Arma no encontrada\"}")))
    @PostMapping("/recarga/{francotiradorId}")
    public ResponseEntity<Francotirador> recargaRapida(@PathVariable Long francotiradorId) {
        Francotirador resultado = simulacionService.simularRecargaRapida(francotiradorId);
        return ResponseEntity.ok(resultado);
    }

    @Operation(
            summary = "Auxiliar de la Regla 2: aguantar la respiración",
            description = "Sube la estabilidad del francotirador a 1.0. Se usa para poder probar la Regla 2 desde HTTP (sin este paso, la estabilidad inicial no llega al umbral)."
    )
    @ApiResponse(responseCode = "200", description = "Estabilidad subida a 1.0")
    @ApiResponse(responseCode = "404", description = "Francotirador no encontrado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(example = "{\"error\":\"not_found\",\"mensaje\":\"Arma no encontrada\"}")))
    @PostMapping("/aguantar/{francotiradorId}")
    public ResponseEntity<Francotirador> aguantarRespiracion(@PathVariable Long francotiradorId) {
        Francotirador resultado = simulacionService.aguantarRespiracion(francotiradorId);
        return ResponseEntity.ok(resultado);
    }
}
