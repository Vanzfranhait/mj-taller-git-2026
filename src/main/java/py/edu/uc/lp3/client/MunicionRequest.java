package py.edu.uc.lp3.client;

/**
 * JSON que espera Joaquín (A3) en POST /api/reglas/municion.
 * Ejemplo aceptado:
 * {"arma":"AK-47","municionCargador":30,"municionReserva":90,"modo":"AUTOMATICO"}
 */
public record MunicionRequest(
        String arma,
        Integer municionCargador,
        Integer municionReserva,
        String modo
) {
    // modo: SEMI, RAFAGA o AUTOMATICO. Si es null, Joaquín asume SEMI.
}
