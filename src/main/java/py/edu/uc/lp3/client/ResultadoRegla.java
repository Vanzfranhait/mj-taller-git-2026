package py.edu.uc.lp3.client;

public record ResultadoRegla(
        boolean permitido,
        String detalle,
        OrigenResultado origen,
        Integer codigoHttp,
        String excepcionOriginal
) {
    public static ResultadoRegla aceptado(String detalle, OrigenResultado origen) {
        return new ResultadoRegla(true, detalle, origen, 200, null);
    }

    public static ResultadoRegla rechazado(String detalle, OrigenResultado origen,
                                           Integer codigoHttp, String excepcionOriginal) {
        return new ResultadoRegla(false, detalle, origen, codigoHttp, excepcionOriginal);
    }

    public static ResultadoRegla fallbackAceptado(String detalle) {
        return new ResultadoRegla(true, detalle, OrigenResultado.FALLBACK, null, null);
    }

    public static ResultadoRegla fallbackRechazado(String detalle) {
        return new ResultadoRegla(false, detalle, OrigenResultado.FALLBACK, null, null);
    }
}
