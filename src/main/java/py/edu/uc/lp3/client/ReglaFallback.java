package py.edu.uc.lp3.client;

public interface ReglaFallback {

    ResultadoRegla validarDisparo(DisparoRequest req, String etiqueta);

    ResultadoRegla validarImpacto(ImpactoRequest req, String etiqueta);

    // --- Reglas de Joaquín (A3) ---
    ResultadoRegla validarMunicion(MunicionRequest req, String etiqueta);

    ResultadoRegla validarDanio(DanioRequest req, String etiqueta);
}
