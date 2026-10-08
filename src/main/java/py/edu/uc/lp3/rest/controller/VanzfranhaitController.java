package py.edu.uc.lp3.rest.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.domain.Arma;
import py.edu.uc.lp3.domain.ArmaCorta;
import py.edu.uc.lp3.domain.Escopeta;
import py.edu.uc.lp3.domain.Francotirador;
import py.edu.uc.lp3.domain.Granada;
import py.edu.uc.lp3.domain.RifleAsalto;
import py.edu.uc.lp3.domain.Subfusil;

/**
 * Controlador REST que expone el inventario de armas de CS2.
 *
 * <p><b>Principio de diseño:</b> el controller NUNCA accede a campos
 * directamente ni usa setters. Solo habla con el dominio a través
 * de mensajes públicos ({@code disparar()}, {@code recargar()},
 * {@code describir()}, {@code lanzar()}, {@code explotar()},
 * {@code usarZoom()}).</p>
 *
 * <p><b>Polimorfismo:</b> el controller no verifica el tipo concreto de cada arma. Cada objeto
 * responde a los mensajes comunes ({@code lanzar}, {@code explotar},
 * {@code usarZoom}) con su propia implementación. Si el arma no
 * soporta el mensaje, la clase base lanza
 * {@link UnsupportedOperationException} y el controller la traduce
 * a un HTTP 400.</p>
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
     * Cada objeto responde su propio {@code describir()} por polimorfismo.
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
     * Lanza una granada. El controller NO verifica el tipo: le pide
     * al objeto que se lance. Si no es una granada, la clase base
     * responde con {@link UnsupportedOperationException}.
     */
    @PostMapping("/granadas/{nombre}/lanzar")
    public ResponseEntity<String> lanzarGranada(@PathVariable String nombre) {
        Arma arma = inventario.get(nombre);
        if (arma == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            arma.lanzar();
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(nombre + " lanzada.");
    }

    /**
     * Explota una granada previamente lanzada. El controller NO verifica
     * el tipo: el polimorfismo decide si el mensaje tiene sentido.
     */
    @PostMapping("/granadas/{nombre}/explotar")
    public ResponseEntity<String> explotarGranada(@PathVariable String nombre) {
        Arma arma = inventario.get(nombre);
        if (arma == null) {
            return ResponseEntity.notFound().build();
        }
        int daño;
        try {
            daño = arma.explotar();
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(nombre + " explotó. Daño en el centro: " + daño);
    }

    /**
     * Cambia el zoom de un francotirador. El controller NO verifica el tipo:
     * el polimorfismo decide si el mensaje tiene sentido.
     */
    @PostMapping("/francotiradores/{nombre}/zoom/{nivel}")
    public ResponseEntity<String> cambiarZoom(@PathVariable String nombre,
                                              @PathVariable int nivel) {
        Arma arma = inventario.get(nombre);
        if (arma == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            arma.usarZoom(nivel);
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(nombre + " ahora tiene zoom x" + nivel);
    }
}