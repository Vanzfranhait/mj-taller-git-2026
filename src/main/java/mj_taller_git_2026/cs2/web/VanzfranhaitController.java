package mj_taller_git_2026.cs2.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mj_taller_git_2026.cs2.Arma;
import mj_taller_git_2026.cs2.ArmaCorta;
import mj_taller_git_2026.cs2.Escopeta;
import mj_taller_git_2026.cs2.Francotirador;
import mj_taller_git_2026.cs2.Granada;
import mj_taller_git_2026.cs2.RifleAsalto;
import mj_taller_git_2026.cs2.Subfusil;

/**
 * Controlador REST que expone el inventario de armas de CS2.
 *
 * <p><b>Principio de diseño:</b> el controller NUNCA accede a campos
 * directamente ni usa setters. Solo habla con el dominio a través
 * de mensajes públicos ({@code disparar()}, {@code recargar()},
 * {@code describir()}, etc.).</p>
 *
 * <p>Si el objeto se pudiera invalidar desde HTTP, el diseño estaría
 * mal. Aquí todas las validaciones viven dentro del dominio.</p>
 *
 * @author Matías Jara (Vanzfranhait)
 */
@RestController
@RequestMapping("/api")
public class VanzfranhaitController {

    /** Inventario de armas: nombre -> Arma */
    private final Map<String, Arma> inventario = new HashMap<>();

    /**
     * Constructor: carga el inventario inicial.
     * Cada instancia se construye con datos coherentes.
     */
    public VanzfranhaitController() {
        inventario.put("AWP", new Francotirador(
            "AWP", 115, 4750, 10, 1.0f, "Ambos", 100, 41, 10));

        inventario.put("AK-47", new RifleAsalto(
            "AK-47", 36, 2700, 30, 0.8f, "T", 80, 600, 8, 0.6f));

        inventario.put("MP9", new Subfusil(
            "MP9", 26, 1250, 30, 0.7f, "CT", 50, 857, 1.0f, 0.3f));

        inventario.put("Nova", new Escopeta(
            "Nova", 26, 1050, 8, 0.6f, "Ambos", 30, 70, 9, 0.4f, 15));

        inventario.put("USP-S", new ArmaCorta(
            "USP-S", 35, 200, 12, 0.9f, "CT", 350, "9mm", false));

        inventario.put("Glock-18", new ArmaCorta(
            "Glock-18", 30, 200, 20, 0.8f, "T", 400, "9mm", true));

        inventario.put("HE", new Granada(
            "HE", 98, 300, "Ambos", "frag", 8, 1.6f));

        inventario.put("Flashbang", new Granada(
            "Flashbang", 0, 200, "Ambos", "flash", 10, 1.6f));
    }

    // ------------------------------------------------------------------
    // Endpoints de lectura
    // ------------------------------------------------------------------

    /**
     * Lista todas las armas del inventario con su descripción.
     */
    @GetMapping("/armas")
    public Map<String, String> listarArmas() {
        Map<String, String> respuesta = new HashMap<>();
        for (Map.Entry<String, Arma> entrada : inventario.entrySet()) {
            respuesta.put(entrada.getKey(), entrada.getValue().describir());
        }
        return respuesta;
    }

    /**
     * Devuelve info detallada de un arma específica.
     */
    @GetMapping("/armas/{nombre}")
    public ResponseEntity<String> verArma(@PathVariable String nombre) {
        Arma arma = inventario.get(nombre);
        if (arma == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(arma.describir());
    }

    // ------------------------------------------------------------------
    // Endpoints de acción (usan mensajes del dominio)
    // ------------------------------------------------------------------

    /**
     * Dispara un arma. Devuelve el daño infligido.
     */
    @PostMapping("/armas/{nombre}/disparar")
    public ResponseEntity<String> disparar(@PathVariable String nombre) {
        Arma arma = inventario.get(nombre);
        if (arma == null) {
            return ResponseEntity.notFound().build();
        }
        int daño = arma.disparar();
        if (daño == 0) {
            return ResponseEntity.ok(
                "El arma " + nombre + " no pudo disparar (sin munición o dispersión).");
        }
        return ResponseEntity.ok(
            nombre + " disparó e infligió " + daño + " de daño. " +
            "Munición restante: " + arma.getMunicionActual() + "/" + arma.getMunicionMax());
    }

    /**
     * Recarga un arma. Devuelve el estado después de recargar.
     */
    @PostMapping("/armas/{nombre}/recargar")
    public ResponseEntity<String> recargar(@PathVariable String nombre) {
        Arma arma = inventario.get(nombre);
        if (arma == null) {
            return ResponseEntity.notFound().build();
        }
        arma.recargar();
        return ResponseEntity.ok(
            nombre + " recargada. Munición: " +
            arma.getMunicionActual() + "/" + arma.getMunicionMax());
    }

    /**
     * Lanza una granada (si el arma es una granada).
     */
    @PostMapping("/granadas/{nombre}/lanzar")
    public ResponseEntity<String> lanzarGranada(@PathVariable String nombre) {
        Arma arma = inventario.get(nombre);
        if (!(arma instanceof Granada granada)) {
            return ResponseEntity.badRequest()
                .body(nombre + " no es una granada o no existe.");
        }
        if (granada.isLanzada()) {
            return ResponseEntity.badRequest()
                .body("La granada " + nombre + " ya fue lanzada.");
        }
        granada.lanzar();
        return ResponseEntity.ok(
            nombre + " lanzada. Explotará en " + granada.getTiempoActivacion() + "s.");
    }

    /**
     * Explota una granada previamente lanzada.
     */
    @PostMapping("/granadas/{nombre}/explotar")
    public ResponseEntity<String> explotarGranada(@PathVariable String nombre) {
        Arma arma = inventario.get(nombre);
        if (!(arma instanceof Granada granada)) {
            return ResponseEntity.badRequest()
                .body(nombre + " no es una granada o no existe.");
        }
        if (!granada.isLanzada()) {
            return ResponseEntity.badRequest()
                .body("No se puede explotar una granada que no fue lanzada.");
        }
        int daño = granada.explotar();
        return ResponseEntity.ok(
            nombre + " explotó. Daño en el centro: " + daño +
            " | Radio: " + granada.getRadio() + "m");
    }

    /**
     * Cambia el zoom de un francotirador.
     */
    @PostMapping("/francotiradores/{nombre}/zoom/{nivel}")
    public ResponseEntity<String> cambiarZoom(@PathVariable String nombre,
                                              @PathVariable int nivel) {
        Arma arma = inventario.get(nombre);
        if (!(arma instanceof Francotirador francotirador)) {
            return ResponseEntity.badRequest()
                .body(nombre + " no es un francotirador o no existe.");
        }
        try {
            francotirador.usarZoom(nivel);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(
            nombre + " ahora tiene zoom x" + francotirador.getZoom());
    }
}
