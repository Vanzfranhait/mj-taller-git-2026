package mj_taller_git_2026.cs2.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Portero de la API: confirma que el servicio está vivo.
 *
 * <p>GET / → mensaje de bienvenida. NO construye armas ni accede
 * al dominio: solo confirma quién lo hizo y qué dominio usa.</p>
 *
 * @author Matías Jara (Vanzfranhait)
 */
@RestController
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "API CS2 viva. Autor: Matías Jara (Vanzfranhait). " +
               "Dominio: Counter-Strike 2. " +
               "Endpoints disponibles: " +
               "/api/armas, " +
               "/api/armas/{nombre}, " +
               "/api/armas/{nombre}/disparar, " +
               "/api/armas/{nombre}/recargar, " +
               "/api/granadas/{nombre}/lanzar, " +
               "/api/granadas/{nombre}/explotar, " +
               "/api/francotiradores/{nombre}/zoom/{nivel}, " +
               "/api/construir/arma";
    }
}
