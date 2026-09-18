package mj_taller_git_2026.cs2.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mj_taller_git_2026.cs2.ArmaCorta;

/**
 * Controller de construcción: recibe parámetros por URL (query string)
 * y los pasa al constructor del dominio.
 *
 * <p><b>Principio clave:</b> el controller NO "arregla" el estado.
 * Si el valor es ilegal, la clase del dominio lo rechaza y aquí
 * solo se devuelve el mensaje de error.</p>
 *
 * @author Matías Jara (Vanzfranhait)
 */
@RestController
public class ConstruccionController {

    @GetMapping("/api/construir/arma")
    public String construirArma(
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

            return "Arma creada exitosamente:\n" + arma.describir();

        } catch (IllegalArgumentException e) {
            return "Error del dominio: " + e.getMessage();
        }
    }
}
