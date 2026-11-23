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
 * Mismo contrato que A2Client: POST /api/reglas/disparo y POST /api/reglas/impacto.
 * Hasta que Joaquín publique su OpenAPI, se asume ese contrato. Si difiere,
 * se ajustan las rutas acá y en el fallback.
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

    @Override
    public ResultadoRegla validarDisparo(DisparoRequest req) {
        if (!habilitado) {
            return fallback.validarDisparo(req, nombre);
        }
        try {
            Map<String, Object> body = restClient.post()
                    .uri("/api/reglas/disparo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(req)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});
            Object detalle = (body == null) ? "aceptado" : body.getOrDefault("detalle", "aceptado");
            return ResultadoRegla.aceptado(String.valueOf(detalle), OrigenResultado.HTTP);
        } catch (RestClientResponseException ex) {
            return traducirRechazo(ex);
        } catch (ResourceAccessException ex) {
            log.warn("{} no responde ({}). Uso fallback local.", nombre, ex.getMessage());
            return fallback.validarDisparo(req, nombre);
        }
    }

    @Override
    public ResultadoRegla validarImpacto(ImpactoRequest req) {
        if (!habilitado) {
            return fallback.validarImpacto(req, nombre);
        }
        try {
            Map<String, Object> body = restClient.post()
                    .uri("/api/reglas/impacto")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(req)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});
            Object detalle = (body == null) ? "aceptado" : body.getOrDefault("detalle", "aceptado");
            return ResultadoRegla.aceptado(String.valueOf(detalle), OrigenResultado.HTTP);
        } catch (RestClientResponseException ex) {
            return traducirRechazo(ex);
        } catch (ResourceAccessException ex) {
            log.warn("{} no responde ({}). Uso fallback local.", nombre, ex.getMessage());
            return fallback.validarImpacto(req, nombre);
        }
    }

    private ResultadoRegla traducirRechazo(RestClientResponseException ex) {
        String cuerpo = ex.getResponseBodyAsString();
        String excepcion = extraerExcepcion(cuerpo);
        return ResultadoRegla.rechazado(
                nombre + " rechazó: " + cuerpo,
                OrigenResultado.HTTP,
                ex.getStatusCode().value(),
                excepcion);
    }

    private String extraerExcepcion(String cuerpo) {
        if (cuerpo == null) return null;
        int idx = cuerpo.indexOf("\"excepcion\"");
        if (idx < 0) return null;
        int start = cuerpo.indexOf('"', idx + 11);
        int end = cuerpo.indexOf('"', start + 1);
        if (start < 0 || end < 0) return null;
        return cuerpo.substring(start + 1, end);
    }
}
