package py.edu.uc.lp3.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.time.Duration;
import java.util.Map;

/**
 * Cliente HTTP hacia A3 (Joaquín Benegas, CS2, puerto 8083).
 *
 * Contrato real de Joaquín:
 *   POST /api/reglas/municion -> {arma, municionCargador, municionReserva, modo}
 *       200 = permitido; 409 = rechazo (MUNICION_AGOTADA); 400 = dato ilegal
 *   POST /api/reglas/danio    -> {tirador, equipoTirador, objetivo, equipoObjetivo,
 *                                 vidaObjetivo, blindajeObjetivo, danio}
 *       200 = permitido; 422 = rechazo (DISPARO_NO_PERMITIDO); 400 = dato ilegal
 *
 * Si Joaquín no responde (timeout / conexión rechazada) o está deshabilitado por
 * configuración, delega en el fallback local.
 */
@Component
public class A3Client implements ReglaClient {

    private static final Logger log = LoggerFactory.getLogger(A3Client.class);

    private final ReglaFallback fallback;
    private final RestClient restClient;
    private final String nombre;
    private final boolean habilitado;

    public A3Client(ReglaFallback fallback,
                    @Value("${api.a3.nombre:A3}") String nombre,
                    @Value("${api.a3.base-url:http://localhost:8083}") String baseUrl,
                    @Value("${api.a3.habilitado:true}") boolean habilitado,
                    @Value("${api.a3.timeout-ms:3000}") int timeoutMs) {
        this.fallback = fallback;
        this.nombre = nombre;
        this.habilitado = habilitado;

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofMillis(timeoutMs));
        factory.setReadTimeout(Duration.ofMillis(timeoutMs));

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    /**
     * Regla de munición de Joaquín. El escenario no la usa hoy (usa /danio),
     * pero queda implementada para completar el contrato de ReglaClient.
     */
    @Override
    public ResultadoRegla validarDisparo(DisparoRequest req) {
        // Joaquín usa /api/reglas/municion con otro DTO. Este método queda para
        // compatibilidad con la interfaz; el escenario llama a validarDanio(...).
        // Se traduce lo mínimo: modelo + carga (1 bala).
        MunicionRequest peticion = new MunicionRequest(
                req.modelo(),
                req.carga(),
                90,
                "SEMI");
        return llamarMunicion(peticion);
    }

    /**
     * Regla de daño de Joaquín: es la que usa el escenario como validación de A3.
     */
    public ResultadoRegla validarDanio(DanioRequest req) {
        return llamarDanio(req);
    }

    /**
     * Regla de impacto (heredada de la interfaz): no aplica al contrato de Joaquín.
     * Se traduce a /api/reglas/danio con valores mínimos para no romper la interfaz.
     */
    @Override
    public ResultadoRegla validarImpacto(ImpactoRequest req) {
        DanioRequest danio = new DanioRequest(
                "Sombra", "TERRORISTAS",
                "Halcón", "ANTITERRORISTAS",
                req.vida(), req.blindaje(),
                180);
        return llamarDanio(danio);
    }

    // --- Llamadas HTTP ---

    private ResultadoRegla llamarMunicion(MunicionRequest req) {
        if (!habilitado) {
            return fallback.validarMunicion(req, nombre);
        }
        try {
            Map<String, Object> body = restClient.post()
                    .uri("/api/reglas/municion")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(req)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});
            return ResultadoRegla.aceptado(
                    String.valueOf(body == null ? "aceptado" : body.getOrDefault("regla", "aceptado")),
                    OrigenResultado.HTTP);
        } catch (RestClientResponseException ex) {
            return traducirRechazo(ex, "municion");
        } catch (ResourceAccessException ex) {
            log.warn("{} no responde ({}). Uso fallback local.", nombre, ex.getMessage());
            return fallback.validarMunicion(req, nombre);
        }
    }

    private ResultadoRegla llamarDanio(DanioRequest req) {
        if (!habilitado) {
            return fallback.validarDanio(req, nombre);
        }
        try {
            Map<String, Object> body = restClient.post()
                    .uri("/api/reglas/danio")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(req)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});
            return ResultadoRegla.aceptado(
                    String.valueOf(body == null ? "aceptado" : body.getOrDefault("regla", "aceptado")),
                    OrigenResultado.HTTP);
        } catch (RestClientResponseException ex) {
            return traducirRechazo(ex, "danio");
        } catch (ResourceAccessException ex) {
            log.warn("{} no responde ({}). Uso fallback local.", nombre, ex.getMessage());
            return fallback.validarDanio(req, nombre);
        }
    }

    private ResultadoRegla traducirRechazo(RestClientResponseException ex, String tipo) {
        String cuerpo = ex.getResponseBodyAsString();
        String excepcion = extraerCampo(cuerpo, "excepcion");
        String codigo = extraerCampo(cuerpo, "codigo");
        return ResultadoRegla.rechazado(
                nombre + " rechazó (" + tipo + "): " + cuerpo,
                OrigenResultado.HTTP,
                ex.getStatusCode().value(),
                excepcion != null ? excepcion : codigo);
    }

    private String extraerCampo(String cuerpo, String campo) {
        if (cuerpo == null) return null;
        String clave = "\"" + campo + "\"";
        int idx = cuerpo.indexOf(clave);
        if (idx < 0) return null;
        int start = cuerpo.indexOf('"', idx + clave.length());
        int end = cuerpo.indexOf('"', start + 1);
        if (start < 0 || end < 0) return null;
        return cuerpo.substring(start + 1, end);
    }
}
