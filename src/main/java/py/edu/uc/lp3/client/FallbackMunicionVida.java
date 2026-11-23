package py.edu.uc.lp3.client;

import org.springframework.stereotype.Component;

@Component
public class FallbackMunicionVida implements ReglaFallback {

    // ===== Reglas de Marcelo (A2) =====

    @Override
    public ResultadoRegla validarDisparo(DisparoRequest req, String etiqueta) {
        if (req == null || req.modelo() == null || req.modelo().isBlank()) {
            return ResultadoRegla.fallbackRechazado(etiqueta + "-fallback: falta el modelo del arma");
        }
        if (req.carga() == null || req.carga() < 1) {
            int balas = req.carga() == null ? 0 : req.carga();
            return ResultadoRegla.fallbackRechazado(String.format(
                    "%s-fallback: %s no puede disparar: el cargador tiene %d balas y hacen falta 1",
                    etiqueta, req.modelo(), balas));
        }
        return ResultadoRegla.fallbackAceptado(
                String.format("%s-fallback: %s puede disparar 1 vez", etiqueta, req.modelo()));
    }

    @Override
    public ResultadoRegla validarImpacto(ImpactoRequest req, String etiqueta) {
        if (req == null || req.modelo() == null || req.modelo().isBlank()) {
            return ResultadoRegla.fallbackRechazado(etiqueta + "-fallback: falta el modelo del arma");
        }
        if (req.vida() == null || req.vida() <= 0) {
            return ResultadoRegla.fallbackRechazado(
                    etiqueta + "-fallback: el jugador ya está eliminado y no puede recibir más daño");
        }
        return ResultadoRegla.fallbackAceptado(etiqueta + "-fallback: impacto aceptado");
    }

    // ===== Reglas de Joaquín (A3) =====

    @Override
    public ResultadoRegla validarMunicion(MunicionRequest req, String etiqueta) {
        if (req == null || req.arma() == null || req.arma().isBlank()) {
            return ResultadoRegla.fallbackRechazado(etiqueta + "-fallback: falta el arma");
        }
        int balasNecesarias = balasPorModo(req.modo());
        int cargador = req.municionCargador() == null ? 0 : req.municionCargador();
        if (cargador < balasNecesarias) {
            return ResultadoRegla.fallbackRechazado(String.format(
                    "%s-fallback: %s tiene el cargador con %d balas y el modo %s necesita %d",
                    etiqueta, req.arma(), cargador, req.modo() == null ? "SEMI" : req.modo(), balasNecesarias));
        }
        return ResultadoRegla.fallbackAceptado(
                String.format("%s-fallback: %s puede disparar en modo %s",
                        etiqueta, req.arma(), req.modo() == null ? "SEMI" : req.modo()));
    }

    @Override
    public ResultadoRegla validarDanio(DanioRequest req, String etiqueta) {
        if (req == null) {
            return ResultadoRegla.fallbackRechazado(etiqueta + "-fallback: falta el request");
        }
        if (req.vidaObjetivo() == null || req.vidaObjetivo() <= 0) {
            return ResultadoRegla.fallbackRechazado(
                    etiqueta + "-fallback: " + req.objetivo() + " ya está eliminado");
        }
        if (req.equipoTirador() != null && req.equipoTirador().equals(req.equipoObjetivo())) {
            return ResultadoRegla.fallbackRechazado(
                    etiqueta + "-fallback: fuego amigo (" + req.equipoTirador() + ")");
        }
        return ResultadoRegla.fallbackAceptado(
                etiqueta + "-fallback: daño aceptado sobre " + req.objetivo());
    }

    private int balasPorModo(String modo) {
        if (modo == null) return 1;
        return switch (modo.toUpperCase()) {
            case "SEMI" -> 1;
            case "RAFAGA" -> 3;
            case "AUTOMATICO" -> 5;
            default -> 1;
        };
    }
}
