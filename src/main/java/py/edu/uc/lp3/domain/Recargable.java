package py.edu.uc.lp3.domain;

/**
 * Protocolo del dominio: cualquier arma que se pueda recargar.
 * 
 * <p>Es una interfaz porque distintas familias de armas (cortas y largas)
 * comparten esta capacidad sin necesidad de heredar del mismo padre
 * concreto.</p>
 */
public interface Recargable {

    /**
     * Recarga el arma al máximo.
     */
    void recargar();

    /**
     * Devuelve la munición actual.
     */
    int getMunicionActual();

    /**
     * Devuelve la munición máxima.
     */
    int getMunicionMax();
}
