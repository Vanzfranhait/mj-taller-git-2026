package py.edu.uc.lp3.exception;

/**
 * Se lanza cuando se rompe una regla de negocio del juego
 * (por ejemplo: intentar explotar algo que no es una granada,
 * o usar un francotirador que no está estabilizado).
 */
public class ReglaDeJuegoException extends JuegoException {

    public ReglaDeJuegoException(String message) {
        super(message);
    }
}
