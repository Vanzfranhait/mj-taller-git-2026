package py.edu.uc.lp3.rest.dto;

/**
 * Request del escenario "disparo coordinado".
 * Los primeros 5 campos se usan para A2 (Marcelo):
 *   POST /api/reglas/disparo  -> {modelo, carga}
 *   POST /api/reglas/impacto  -> {modelo, distancia, vida, blindaje}
 * Los últimos 6 se usan para A3 (Joaquín):
 *   POST /api/reglas/municion -> {arma, municionCargador, municionReserva, modo}
 *   POST /api/reglas/danio    -> {tirador, equipoTirador, objetivo, equipoObjetivo,
 *                                  vidaObjetivo, blindajeObjetivo, danio}
 * "modelo" se reutiliza como "arma" para A3.
 */
public record EscenarioDisparoRequest(
        // --- A2 (Marcelo) ---
        String modelo,
        Integer carga,
        Integer distancia,
        Integer vida,
        Integer blindaje,

        // --- A3 (Joaquín) ---
        Integer municionReserva,
        String modo,
        String tirador,
        String equipoTirador,
        String objetivo,
        String equipoObjetivo,
        Integer danio
) {
}
