package mj_taller_git_2026.cs2;

/**
 * Especialización de {@link ArmaLarga} que representa una escopeta
 * (ej: MAG-7, Nova, XM1014).
 *
 * <p>Añade comportamientos propios: múltiples perdigones por disparo
 * y dispersión que reduce el daño a distancia.</p>
 *
 * <p><b>Invariantes protegidos:</b></p>
 * <ul>
 *   <li>{@code numeroBalas} entre 1 y 20</li>
 *   <li>{@code dispersionBalas} entre 0.0 y 1.0</li>
 *   <li>{@code alcanceEfectivo} &gt; 0 y &lt;= alcance</li>
 * </ul>
 *
 * @author Matías Jara (Vanzfranhait)
 */
public class Escopeta extends ArmaLarga {

    private final int numeroBalas;        // cantidad de perdigones
    private final float dispersionBalas;  // 0.0 a 1.0
    private final int alcanceEfectivo;    // metros

    /**
     * Constructor de escopeta.
     *
     * @param nombre            nombre del arma
     * @param daño              daño por perdigón
     * @param precio            precio en la tienda
     * @param municionMax       capacidad del cargador
     * @param precision         precisión base (0.0 a 1.0)
     * @param equipo            "CT", "T" o "Ambos"
     * @param alcance           alcance total en metros
     * @param cadencia          disparos por minuto (RPM)
     * @param numeroBalas       cantidad de perdigones por disparo (1 a 20)
     * @param dispersionBalas   dispersión de los perdigones (0.0 a 1.0)
     * @param alcanceEfectivo   alcance útil en metros (menor o igual a alcance)
     */
    public Escopeta(String nombre, int daño, int precio, int municionMax,
                    float precision, String equipo,
                    int alcance, int cadencia,
                    int numeroBalas, float dispersionBalas, int alcanceEfectivo) {
        super(nombre, daño, precio, municionMax, precision, equipo, alcance, cadencia);

        if (numeroBalas < 1 || numeroBalas > 20) {
            throw new IllegalArgumentException("El número de balas debe estar entre 1 y 20");
        }
        if (dispersionBalas < 0.0f || dispersionBalas > 1.0f) {
            throw new IllegalArgumentException("La dispersión debe estar entre 0.0 y 1.0");
        }
        if (alcanceEfectivo <= 0 || alcanceEfectivo > alcance) {
            throw new IllegalArgumentException(
                "El alcance efectivo debe ser positivo y no mayor al alcance total");
        }

        this.numeroBalas = numeroBalas;
        this.dispersionBalas = dispersionBalas;
        this.alcanceEfectivo = alcanceEfectivo;
    }

    /**
     * Dispara una ráfaga corta (2 tiros). Solo si hay munición.
     *
     * @return cantidad de disparos efectivamente realizados
     */
    public int dispararRafagaCorta() {
        int disparos = 0;
        for (int i = 0; i < 2 && puedeDisparar(); i++) {
            disparar();
            disparos++;
        }
        return disparos;
    }

    /**
     * Calcula el daño total si TODOS los perdigones aciertan.
     * (En un juego real, cada perdigón puede acertar o fallar.)
     *
     * @return daño máximo teórico de un disparo
     */
    public int calcularDañoTotal() {
        return getDaño() * numeroBalas;
    }

    // ------------------------------------------------------------------
    // Getters (solo lectura)
    // ------------------------------------------------------------------
    public int getNumeroBalas()       { return numeroBalas; }
    public float getDispersionBalas() { return dispersionBalas; }
    public int getAlcanceEfectivo()   { return alcanceEfectivo; }

    // ------------------------------------------------------------------
    // Implementación de métodos abstractos heredados
    // ------------------------------------------------------------------

    @Override
    public String getTipo() {
        return "Escopeta";
    }

    @Override
    public String describir() {
        return String.format(
            "%s [%s] - Daño: %d x %d perdigones | Precio: $%d | Alcance efectivo: %dm | Dispersión: %.1f",
            getNombre(), getTipo(), getDaño(), numeroBalas, getPrecio(),
            alcanceEfectivo, dispersionBalas
        );
    }

    // ------------------------------------------------------------------
    // Sobrescritura: el daño depende de cuántos perdigones aciertan
    // ------------------------------------------------------------------
    @Override
    public int disparar() {
        if (!puedeDisparar()) {
            return 0;
        }
        // Contamos cuántos perdigones impactan (según dispersión)
        int impactos = 0;
        for (int i = 0; i < numeroBalas; i++) {
            if (Math.random() >= dispersionBalas) {
                impactos++;
            }
        }
        // Reutilizamos la lógica base para descontar la munición
        super.disparar();
        return getDaño() * impactos;
    }
}
