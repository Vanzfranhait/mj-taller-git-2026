package py.edu.uc.lp3.rest.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.domain.ArmaCorta;

/**
 * Controller de construcción: recibe parámetros por URL (query string)
 * y los pasa al constructor del dominio.
 *
 * <p><b>Principio clave:</b> el controller NO "arregla" el estado.
 * Si el valor es ilegal, la clase del dominio lo rechaza y aquí
 * solo se devuelve el mensaje de error en formato JSON.</p>
 *
 * <p><b>Respuesta JSON:</b> se devuelve un {@code Map<String,Object>}
 * que Spring serializa automáticamente. En caso de éxito incluye los
 * datos del arma creada; en caso de error, el mensaje del dominio.</p>
 *
 * @author Matías Jara (Vanzfranhait)
 */
@RestController
public class ConstruccionController {

    @GetMapping("/api/construir/arma")
    public ResponseEntity<Map<String, Object>> construirArma(
            @RequestParam String nombre,
            @RequestParam int dano,
            @RequestParam int precio,
            @RequestParam int municion,
            @RequestParam float precision,
            @RequestParam String equipo,
            @RequestParam int cadencia,
            @RequestParam String municionTipo) {

        try {
            ArmaCorta arma = new ArmaCorta(
                nombre, dano, precio, municion, precision,
                equipo, cadencia, municionTipo, false);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("exito", true);
            respuesta.put("tipo", arma.getTipo());
            respuesta.put("descripcion", arma.describir());
            respuesta.put("nombre", arma.getNombre());
            respuesta.put("danio", arma.getDaño());
            respuesta.put("precio", arma.getPrecio());
            respuesta.put("municionActual", arma.getMunicionActual());
            respuesta.put("municionMax", arma.getMunicionMax());
            respuesta.put("cadencia", arma.getCadencia());
            respuesta.put("tipoMunicion", arma.getTipoMunicion());

            return ResponseEntity.ok(respuesta);

        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("exito", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}