package py.edu.uc.lp3.rest.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.domain.Arma;
import py.edu.uc.lp3.domain.Francotirador;
import py.edu.uc.lp3.service.SimulacionService;

/**
 * Controller REST para disparar las simulaciones de reglas de negocio.
 * 
 * <p>Ya no tiene try-catch: las excepciones del dominio son atrapadas
 * por el GlobalExceptionHandler.</p>
 */
@RestController
@RequestMapping("/api/db/simular")
public class SimulacionController {

    private final SimulacionService simulacionService;

    public SimulacionController(SimulacionService simulacionService) {
        this.simulacionService = simulacionService;
    }

    /**
     * REGLA 1: Explosión de Granada.
     * POST /api/db/simular/explosion/{granadaId}
     */
    @PostMapping("/explosion/{granadaId}")
    public ResponseEntity<List<Arma>> explosion(@PathVariable Long granadaId) {
        List<Arma> resultado = simulacionService.simularExplosion(granadaId);
        return ResponseEntity.ok(resultado);
    }

    /**
     * REGLA 2: Recarga rápida de Francotirador.
     * POST /api/db/simular/recarga/{francotiradorId}
     */
    @PostMapping("/recarga/{francotiradorId}")
    public ResponseEntity<Francotirador> recargaRapida(@PathVariable Long francotiradorId) {
        Francotirador resultado = simulacionService.simularRecargaRapida(francotiradorId);
        return ResponseEntity.ok(resultado);
    }
}
