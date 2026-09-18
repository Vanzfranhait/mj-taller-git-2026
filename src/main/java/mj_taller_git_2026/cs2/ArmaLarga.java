package mj_taller_git_2026.cs2;

/**
 * Clase abstracta intermedia que agrupa las armas largas
 * (rifles, francotiradores, subfusiles, escopetas).
 *
 * <p>Extiende {@link Arma} agregando atributos propios de las armas
 * de dos manos: alcance, modo ráfaga y cooldown entre disparos.</p>
 *
 * <p>Esta clase NO puede instanciarse: se especializa en subclases
 * concretas como {@link Francotirador}, {@link RifleAsalto}, etc.</p>
 *
 * <p><b>Invariantes protegidos:</b></p>
 * <ul>
 *   <li>{@code alcance} siempre &gt; 0</li>
 *   <li>{@code cooldownMs} siempre &gt; 0</li>
 * </ul>
 *
 * @author Matías Jara (Vanzfranhait)
 */
public abstract class ArmaLarga extends Arma {

    private final int alcance;          // metros
    private final long cooldownMs;      // milisegundos entre disparos
    private boolean modoRafaga;         // true = ráfaga, false = tiro a tiro

    /**
     * Constructor para armas largas.
     *
     * @param nombre      nombre del arma (ej: "AWP")
     * @param daño        daño por disparo
     * @param precio      precio en la tienda
     * @param municionMax capacidad del cargador
     * @param precision   precisión (0.0 a 1.0)
     * @param equipo      "CT", "T" o "Ambos"
     * @param alcance     alcance efectivo en metros
     * @param cadencia    disparos por minuto (RPM)
     */
    protected ArmaLarga(String nombre, int daño, int precio, int municionMax,
                        float precision, String equipo,
                        int alcance, int cadencia) {
        super(nombre, daño, precio, municionMax, precision, equipo);

        if (alcance <= 0) {
            throw new IllegalArgumentException("El alcance debe ser positivo");
        }
        if (cadencia <= 0) {
            throw new IllegalArgumentException("La cadencia debe ser positiva");
        }

        this.alcance = alcance;
        // Conversión: RPM -> milisegundos entre disparos
        this.cooldownMs = 60000L / cadencia;
        this.modoRafaga = false;   // por defecto, tiro a tiro
    }

    /**
     * Alterna entre modo tiro a tiro y modo ráfaga.
     */
    public void cambiarModo() {
        this.modoRafaga = !this.modoRafaga;
    }

    /**
     * Dispara una ráfaga corta (3 tiros). Solo funciona si hay munición.
     *
     * @return la cantidad de disparos efectivamente realizados
     */
    public int dispararRafaga() {
        int disparos = 0;
        for (int i = 0; i < 3 && puedeDisparar(); i++) {
            disparar();
            disparos++;
        }
        return disparos;
    }

    // ------------------------------------------------------------------
    // Getters (solo lectura)
    // ------------------------------------------------------------------
    public int getAlcance()       { return alcance; }
    public long getCooldownMs()   { return cooldownMs; }
    public boolean isModoRafaga() { return modoRafaga; }
}
