package py.edu.uc.lp3.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.client.A2Client;
import py.edu.uc.lp3.client.A3Client;
import py.edu.uc.lp3.client.DanioRequest;
import py.edu.uc.lp3.client.DisparoRequest;
import py.edu.uc.lp3.client.ImpactoRequest;
import py.edu.uc.lp3.client.MunicionRequest;
import py.edu.uc.lp3.client.ResultadoRegla;
import py.edu.uc.lp3.domain.Jugada;
import py.edu.uc.lp3.exception.ReglaDeJuegoException;
import py.edu.uc.lp3.repository.JugadaRepository;
import py.edu.uc.lp3.rest.dto.EscenarioDisparoRequest;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Escenario "disparo coordinado":
 *   1) A2 (Marcelo) valida munición: POST /api/reglas/disparo -> {modelo, carga}
 *   2) A3 (Joaquín) valida daño:     POST /api/reglas/danio    -> {tirador, ..., danio}
 *   3) Solo si AMBAS aceptan, se persiste la Jugada en H2.
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
        // 1) A2 (Marcelo): munición
        ResultadoRegla r2 = a2Client.validarDisparo(
                new DisparoRequest(req.modelo(), req.carga()));
        if (!r2.permitido()) {
            throw new ReglaDeJuegoException("A2 rechazó: " + r2.detalle());
        }

        // 2) A3 (Joaquín): daño
        ResultadoRegla r3 = a3Client.validarDanio(
                new DanioRequest(
                        req.tirador(),
                        req.equipoTirador(),
                        req.objetivo(),
                        req.equipoObjetivo(),
                        req.vida(),
                        req.blindaje(),
                        req.danio()));
        if (!r3.permitido()) {
            throw new ReglaDeJuegoException("A3 rechazó: " + r3.detalle());
        }

        // 3) Ambas aceptan -> persistir
        int vidaAntes = req.vida() == null ? 0 : req.vida();
        int blindajeAntes = req.blindaje() == null ? 0 : req.blindaje();
        int dano = req.danio() == null ? 0 : req.danio();
        int vidaDespues = Math.max(0, Math.min(100, vidaAntes - dano));
        int blindajeDespues = Math.max(0, blindajeAntes - Math.min(blindajeAntes, dano / 2));

        Jugada jugada = new Jugada(
                req.modelo(),
                req.carga(),
                req.distancia(),
                vidaAntes,
                vidaDespues,
                blindajeAntes,
                blindajeDespues,
                r2.origen() == null ? "desconocido" : r2.origen().name(),
                r3.origen() == null ? "desconocido" : r3.origen().name(),
                LocalDateTime.now()
        );
        return jugadaRepository.save(jugada);
    }

    @Transactional(readOnly = true)
    public List<Jugada> listarJugadas() {
        return jugadaRepository.findAll();
    }
}
