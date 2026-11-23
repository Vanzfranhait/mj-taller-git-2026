package py.edu.uc.lp3.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.client.A2Client;
import py.edu.uc.lp3.client.A3Client;
import py.edu.uc.lp3.client.DisparoRequest;
import py.edu.uc.lp3.client.ImpactoRequest;
import py.edu.uc.lp3.client.ResultadoRegla;
import py.edu.uc.lp3.domain.Jugada;
import py.edu.uc.lp3.exception.ReglaDeJuegoException;
import py.edu.uc.lp3.repository.JugadaRepository;
import py.edu.uc.lp3.rest.dto.EscenarioDisparoRequest;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Escenario "disparo coordinado":
 *   1) A2 valida munición (POST /api/reglas/disparo).
 *   2) A3 valida vida / impacto (POST /api/reglas/impacto).
 *   3) Solo si AMBAS aceptan, se persiste la Jugada en H2.
 *
 * Si alguna rechaza, se lanza ReglaDeJuegoException con el nombre del
 * compañero que rechazó. La base queda intacta.
 *
 * Orden: A2 primero (más barata) y A3 después. Si A2 rechaza, no se llama a A3.
 */
@Service
public class EscenarioService {

    private final A2Client a2Client;
    private final A3Client a3Client;
    private final JugadaRepository jugadaRepository;

    public EscenarioService(A2Client a2Client,
                            A3Client a3Client,
                            JugadaRepository jugadaRepository) {
        this.a2Client = a2Client;
        this.a3Client = a3Client;
        this.jugadaRepository = jugadaRepository;
    }

    @Transactional
    public Jugada ejecutarDisparoCoordinado(EscenarioDisparoRequest req) {
        // 1) A2: regla de munición
        DisparoRequest disparo = new DisparoRequest(req.modelo(), req.carga());
        ResultadoRegla r2 = a2Client.validarDisparo(disparo);
        if (!r2.permitido()) {
            throw new ReglaDeJuegoException("A2 rechazó: " + r2.detalle());
        }

        // 2) A3: regla de vida / impacto
        ImpactoRequest impacto = new ImpactoRequest(
                req.modelo(),
                req.distancia(),
                req.vida(),
                req.blindaje());
        ResultadoRegla r3 = a3Client.validarImpacto(impacto);
        if (!r3.permitido()) {
            throw new ReglaDeJuegoException("A3 rechazó: " + r3.detalle());
        }

        // 3) Ambas aceptan -> persistir la jugada.
        // La vida resultante se calcula con una fórmula simple local (aproximación),
        // porque el resultado exacto de A3 es informativo y no estructurado.
        int vidaAntes = req.vida() == null ? 0 : req.vida();
        int blindajeAntes = req.blindaje() == null ? 0 : req.blindaje();
        int dano = calcularDano(req.modelo(), req.distancia());
        int vidaDespues = Math.max(0, Math.min(100, vidaAntes - dano));
        int blindajeDespues = Math.max(0, blindajeAntes - Math.min(blindajeAntes, dano / 10));

        Jugada jugada = new Jugada(
                req.modelo(),
                req.carga(),
                req.distancia(),
                vidaAntes,
                vidaDespues,
                blindajeAntes,
                blindajeDespues,
                origen(r2),
                origen(r3),
                LocalDateTime.now()
        );
        return jugadaRepository.save(jugada);
    }

    @Transactional(readOnly = true)
    public List<Jugada> listarJugadas() {
        return jugadaRepository.findAll();
    }

    private String origen(ResultadoRegla r) {
        // Si el resultado vino de fallback, el detalle empieza con "A2-fallback" o "A3-fallback".
        // Usamos el origen declarado, más el nombre del cliente para trazabilidad.
        return r.origen() == null ? "desconocido" : r.origen().name();
    }

    private int calcularDano(String modelo, Integer distancia) {
        int d = distancia == null ? 0 : distancia;
        int base = switch (modelo == null ? "" : modelo) {
            case "AK-47"  -> 35;
            case "M4A4"   -> 33;
            case "AWP"    -> 115;
            case "SSG 08" -> 88;
            default       -> 30;
        };
        return Math.max(1, base - (d / 10));
    }
}
