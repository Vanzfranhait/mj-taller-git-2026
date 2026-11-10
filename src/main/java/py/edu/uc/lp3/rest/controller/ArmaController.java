package py.edu.uc.lp3.rest.controller;
import py.edu.uc.lp3.domain.Recargable;
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
import py.edu.uc.lp3.exception.ArmaNoEncontradaException;
import py.edu.uc.lp3.repository.ArmaRepository;

/**
 * Controller REST para el CRUD de la familia Arma.
 * Usa ArmaNoEncontradaException para manejar los 404.
 */
@RestController
@RequestMapping("/api/db/armas")
public class ArmaController {

    private final ArmaRepository repository;

    // Inyección de dependencias por constructor
    public ArmaController(ArmaRepository repository) {
        this.repository = repository;
    }

    // 1. CREATE (POST) - Crear un arma desde JSON
    @PostMapping
    public ResponseEntity<Arma> crearArma(@RequestBody Arma arma) {
        if (arma.getClass() == Arma.class) {
            return ResponseEntity.badRequest().build();
        }
        Arma guardada = repository.save(arma);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    // 2. READ ALL (GET) - Listar todas las armas persistidas
    @GetMapping
    public List<Arma> listarArmas() {
        return repository.findAll();
    }

    // 3. READ ONE (GET por ID) - Buscar un arma por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Arma> buscarPorId(@PathVariable Long id) {
        Arma arma = repository.findById(id)
                .orElseThrow(() -> new ArmaNoEncontradaException(id));
        return ResponseEntity.ok(arma);
    }

    // 4. UPDATE (PATCH) - Recargar un arma (usa un mensaje del dominio)
    @PatchMapping("/{id}/recargar")
    public ResponseEntity<Arma> recargarArma(@PathVariable Long id) {
        Arma arma = repository.findById(id)
                .orElseThrow(() -> new ArmaNoEncontradaException(id));
        
        arma.recargar(); 
        return ResponseEntity.ok(repository.save(arma)); 
    }

    // 5. DELETE (DELETE por ID) - Borrar un arma
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrarArma(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new ArmaNoEncontradaException(id);
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    // 6. READ (GET) - Listar solo las armas recargables (usa la interfaz)
    @GetMapping("/recargables")
    public List<Recargable> listarRecargables() {
        // Como todas las armas implementan Recargable, devolvemos todas
        return repository.findAll().stream()
                .filter(a -> a instanceof Recargable)
                .map(a -> (Recargable) a)
                .toList();
    }
}
