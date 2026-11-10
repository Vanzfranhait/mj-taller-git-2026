package py.edu.uc.lp3.exception;

/**
 * Excepción base de todas las reglas de negocio del juego.
 * Es una excepción no comprobada (RuntimeException) para no obligar
 * a los controllers a llenarse de try-catch.
 */
public class JuegoException extends RuntimeException {

    public JuegoException(String message) {
        super(message);
    }
}
