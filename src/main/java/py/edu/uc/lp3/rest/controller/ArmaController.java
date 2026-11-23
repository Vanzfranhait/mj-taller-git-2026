package py.edu.uc.lp3.rest.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.domain.Arma;
import py.edu.uc.lp3.domain.Recargable;
import py.edu.uc.lp3.service.ArmaService;

import py.edu.uc.lp3.rest.dto.ArmaRequest;

@RestController
@RequestMapping("/api/db/armas")
public class ArmaController {

    private final ArmaService armaService;

    public ArmaController(ArmaService armaService) {
        this.armaService = armaService;
    }

    @PostMapping
    public ResponseEntity<Arma> crearArma(@RequestBody ArmaRequest req) {
    Arma guardada = armaService.crearDesdeRequest(req);
    return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    @GetMapping
    public List<Arma> listarArmas() {
        return armaService.listarArmas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Arma> buscarPorId(@PathVariable Long id) {
        Arma arma = armaService.buscarPorId(id);
        return ResponseEntity.ok(arma);
    }

    @PatchMapping("/{id}/recargar")
    public ResponseEntity<Arma> recargarArma(@PathVariable Long id) {
        Arma arma = armaService.recargarArma(id);
        return ResponseEntity.ok(arma);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrarArma(@PathVariable Long id) {
        armaService.borrarArma(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/recargables")
    public List<Recargable> listarRecargables() {
        return armaService.listarRecargables();
    }
}
