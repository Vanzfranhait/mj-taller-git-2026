package py.edu.uc.lp3.client;

/**
 * JSON que espera Joaquín (A3) en POST /api/reglas/danio.
 * Ejemplo aceptado:
 * {"tirador":"Sombra","equipoTirador":"TERRORISTAS",
 *  "objetivo":"Halcón","equipoObjetivo":"ANTITERRORISTAS",
 *  "vidaObjetivo":100,"blindajeObjetivo":100,"danio":180}
 */
public record DanioRequest(
        String tirador,
        String equipoTirador,
        String objetivo,
        String equipoObjetivo,
        Integer vidaObjetivo,
        Integer blindajeObjetivo,
        Integer danio
) {
    // equipos: TERRORISTAS o ANTITERRORISTAS.
    // Rechaza con 422 si el objetivo está eliminado (vidaObjetivo <= 0)
    // o si es fuego amigo (equipoTirador == equipoObjetivo).
}
