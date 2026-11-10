package py.edu.uc.lp3.exception;

/**
 * Se lanza cuando se busca un arma por ID y no existe en la base de datos.
 */
public class ArmaNoEncontradaException extends JuegoException {

    public ArmaNoEncontradaException(Long id) {
        super("Arma no encontrada con ID: " + id);
    }
}
