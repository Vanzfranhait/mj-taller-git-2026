package py.edu.uc.lp3.client;

import org.springframework.stereotype.Component;

/**
 * Fallback local que replica la SEMÁNTICA de aceptación/rechazo de las reglas
 * documentadas por A2 (Marcelo Kropff) en su OpenAPI, para poder operar cuando
 * A2 o A3 no están disponibles.
 *
 * No replica los valores exactos del cálculo de daño (para eso se usa el
 * cliente HTTP contra A2). Replica los casos borde:
 *   - munición: no dispara sin balas;
 *   - vida: no recibe daño un jugador eliminado, la vida queda entre 0 y 100,
 *     el chaleco desvía una parte del daño.
 *
 * Los mensajes se etiquetan con "A2-fallback" o "A3-fallback" según quién
 * invocó el fallback.
 */
@Component
public class FallbackMunicionVida implements ReglaFallback {

    @Override
    public ResultadoRegla validarDisparo(DisparoRequest req, String etiqueta) {
        if (req == null || req.modelo() == null || req.modelo().isBlank()) {
            return ResultadoRegla.fallbackRechazado(
                    etiqueta + "-fallback: falta el modelo del arma");
        }
        if (req.carga() == null || req.carga() < 1) {
            int balas = req.carga() == null ? 0 : req.carga();
            return ResultadoRegla.fallbackRechazado(
                    String.format("%s-fallback: %s no puede disparar: el cargador tiene %d balas y hacen falta 1",
                            etiqueta, req.modelo(), balas));
        }
        return ResultadoRegla.fallbackAceptado(
                String.format("%s-fallback: %s puede disparar 1 vez", etiqueta, req.modelo()));
    }

    @Override
    public ResultadoRegla validarImpacto(ImpactoRequest req, String etiqueta) {
        if (req == null || req.modelo() == null || req.modelo().isBlank()) {
            return ResultadoRegla.fallbackRechazado(
                    etiqueta + "-fallback: falta el modelo del arma");
        }
        if (req.vida() == null || req.vida() <= 0) {
            return ResultadoRegla.fallbackRechazado(
                    etiqueta + "-fallback: el jugador ya está eliminado y no puede recibir más daño");
        }

        int distancia = req.distancia() == null ? 0 : req.distancia();
        int vida = req.vida();
        int blindaje = req.blindaje() == null ? 0 : req.blindaje();

        int danoSinBlindaje = calcularDanoSinBlindaje(req.modelo(), distancia);

        // El chaleco desvía ~1/10 del daño cuando hay blindaje.
        int danoDesviado = blindaje > 0 ? Math.max(1, danoSinBlindaje / 10) : 0;
        int vidaPerdida = danoSinBlindaje - danoDesviado;

        int vidaDespues = Math.max(0, Math.min(100, vida - vidaPerdida));
        int blindajePerdido = Math.min(blindaje, danoDesviado);
        int blindajeDespues = Math.max(0, blindaje - blindajePerdido);

        boolean eliminado = vidaDespues == 0;

        String detalle = String.format(
                "%s-fallback: %s impacto aceptado: vidaPerdida=%d, vidaDespues=%d, blindajeDespues=%d, eliminado=%s",
                etiqueta, req.modelo(), vidaPerdida, vidaDespues, blindajeDespues, eliminado);

        return ResultadoRegla.fallbackAceptado(detalle);
    }

    // Daño base simplificado: cae con la distancia, mínimo 1.
    // (Aproximación: los valores exactos los define A2 por HTTP.)
    private int calcularDanoSinBlindaje(String modelo, int distancia) {
        int danoBase = switch (modelo) {
            case "AK-47"  -> 35;
            case "M4A4"   -> 33;
            case "AWP"    -> 115;
            case "SSG 08" -> 88;
            default       -> 30;
        };
        int reduccion = distancia / 10;
        return Math.max(1, danoBase - reduccion);
    }
}
