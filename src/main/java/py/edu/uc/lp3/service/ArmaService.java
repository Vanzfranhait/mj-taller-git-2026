package py.edu.uc.lp3.service;

import java.util.List;
import org.springframework.stereotype.Service;
import py.edu.uc.lp3.domain.Arma;
import py.edu.uc.lp3.domain.Recargable;
import py.edu.uc.lp3.exception.ArmaNoEncontradaException;
import py.edu.uc.lp3.exception.ReglaDeJuegoException;
import py.edu.uc.lp3.repository.ArmaRepository;

import py.edu.uc.lp3.domain.Francotirador;
import py.edu.uc.lp3.domain.Granada;
import py.edu.uc.lp3.rest.dto.ArmaRequest;

@Service
public class ArmaService {

        private void validarArma(Arma arma) {
        if (arma.getNombre() == null || arma.getNombre().isBlank()) {
            throw new ReglaDeJuegoException("El nombre no puede estar vacío");
        }
        if (arma.getDaño() < 0) {
            throw new ReglaDeJuegoException("El daño no puede ser negativo");
        }
        if (arma.getPrecio() < 0) {
            throw new ReglaDeJuegoException("El precio no puede ser negativo");
        }
        if (arma.getPrecision() < 0.0f || arma.getPrecision() > 1.0f) {
            throw new ReglaDeJuegoException("La precisión debe estar entre 0.0 y 1.0");
        }
        if (arma.getEquipo() == null) {
            throw new ReglaDeJuegoException("El equipo no puede ser nulo");
        }
    }

    public Arma crearArma(Arma arma) {
        if (arma.getClass() == Arma.class) {
            throw new ReglaDeJuegoException("No se puede crear una instancia directa de Arma");
        }
        validarArma(arma);
        return repository.save(arma);
    }
    private final ArmaRepository repository;

    public ArmaService(ArmaRepository repository) {
        this.repository = repository;
    }


    public List<Arma> listarArmas() {
        return repository.findAll();
    }

    public Arma buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ArmaNoEncontradaException(id));
    }

    public Arma recargarArma(Long id) {
        Arma arma = buscarPorId(id);
        arma.recargar();
        return repository.save(arma);
    }

    public void borrarArma(Long id) {
        if (!repository.existsById(id)) {
            throw new ArmaNoEncontradaException(id);
        }
        repository.deleteById(id);
    }

    public List<Recargable> listarRecargables() {
        return repository.findAll().stream()
                .filter(a -> a instanceof Recargable)
                .map(a -> (Recargable) a)
                .toList();
    }
    public Arma crearDesdeRequest(ArmaRequest req) {
    if (req.getTipo() == null || req.getTipo().isBlank()) {
        throw new ReglaDeJuegoException("Falta el campo 'tipo'");
    }

    Arma arma;
    switch (req.getTipo().toLowerCase()) {
        case "francotirador" -> arma = new Francotirador(
            req.getNombre(),
            req.getDaño() != null ? req.getDaño() : 0,
            req.getPrecio() != null ? req.getPrecio() : 0,
            req.getMunicionMax() != null ? req.getMunicionMax() : 1,
            req.getPrecision() != null ? req.getPrecision() : 1.0f,
            req.getEquipo(),
            req.getAlcance() != null ? req.getAlcance() : 1,
            req.getCadencia() != null ? req.getCadencia() : 60,
            req.getZoom() != null ? req.getZoom() : 1
        );
        case "granada" -> arma = new Granada(
            req.getNombre(),
            req.getDaño() != null ? req.getDaño() : 0,
            req.getPrecio() != null ? req.getPrecio() : 0,
            req.getEquipo(),
            req.getTipoGranada(),
            req.getRadioExplosion() != null ? req.getRadioExplosion() : 1,
            req.getTiempoActivacion() != null ? req.getTiempoActivacion() : 1.0f
        );
        default -> throw new ReglaDeJuegoException(
            "Tipo de arma desconocido: " + req.getTipo());
    }

    validarArma(arma);
    return repository.save(arma);
}
}

