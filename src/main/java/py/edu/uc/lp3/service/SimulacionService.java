package py.edu.uc.lp3.service;

import java.util.List;

import org.springframework.stereotype.Service;

import py.edu.uc.lp3.domain.Arma;
import py.edu.uc.lp3.domain.ArmaLarga;
import py.edu.uc.lp3.domain.Francotirador;
import py.edu.uc.lp3.domain.Granada;
import py.edu.uc.lp3.exception.ArmaNoEncontradaException;
import py.edu.uc.lp3.exception.ReglaDeJuegoException;
import py.edu.uc.lp3.repository.ArmaRepository;

/**
 * Servicio que contiene las reglas de negocio del juego.
 *
 * <p>Las reglas afectan el estado de los objetos y se persisten
 * automáticamente porque estamos usando un repositorio JPA.</p>
 *
 * @author Matías Jara (Vanzfranhait)
 */
@Service
public class SimulacionService {

    private final ArmaRepository repository;

    public SimulacionService(ArmaRepository repository) {
        this.repository = repository;
    }

    /**
     * REGLA 1: Explosión de Granada.
     * Si una granada explota, todas las armas cuyo alcance sea menor
     * al radio de la explosión pierden la mitad de su munición actual.
     *
     * @param granadaId ID de la granada que explota.
     * @return Lista de todas las armas (con los estados ya persistidos).
     */
    public List<Arma> simularExplosion(Long granadaId) {
        Arma arma = repository.findById(granadaId)
                .orElseThrow(() -> new ArmaNoEncontradaException(granadaId));

        if (!(arma instanceof Granada granada)) {
            throw new ReglaDeJuegoException("El arma no es una granada");
        }

        // Lanzar (si no se lanzó) y explotar
        if (!granada.isLanzada()) {
            granada.lanzar();
        }
        int radio = granada.getRadio();

        // Reducir munición a las armas dentro del radio
        List<Arma> todas = repository.findAll();
        for (Arma a : todas) {
            if (!a.getId().equals(granadaId)) {
                int alcance = 0;
                if (a instanceof ArmaLarga larga) {
                    alcance = larga.getAlcance();
                }

                if (alcance < radio) {
                    int nuevaMunicion = a.getMunicionActual() / 2;
                    a.setMunicionActualParaSimulacion(nuevaMunicion);
                    repository.save(a); // Persistimos el cambio
                }
            }
        }
        repository.save(granada); // Persistimos la granada (ya lanzada)
        return todas;
    }

    /**
     * REGLA 2: Recarga Rápida de Francotirador.
     * Un francotirador con estabilidad > 0.7 puede hacer un disparo rápido
     * que consume 2 de munición. Luego queda inestable (0.5).
     *
     * @param id ID del francotirador.
     * @return El francotirador con el estado actualizado.
     */
    public Francotirador simularRecargaRapida(Long id) {
        Arma arma = repository.findById(id)
                .orElseThrow(() -> new ArmaNoEncontradaException(id));

        if (!(arma instanceof Francotirador francotirador)) {
            throw new ReglaDeJuegoException("El arma no es un francotirador");
        }

        if (francotirador.getEstabilidad() <= 0.7f) {
            throw new ReglaDeJuegoException("El francotirador no está estabilizado");
        }

        if (francotirador.getMunicionActual() < 2) {
            throw new ReglaDeJuegoException("No hay munición suficiente para el disparo rápido");
        }

        francotirador.disparar(2); // consume 2 municiones
        francotirador.respirar();  // vuelve a estabilidad 0.5

        return repository.save(francotirador);
    }
}
