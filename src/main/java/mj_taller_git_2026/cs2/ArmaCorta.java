package mj_taller_git_2026.cs2;

/**
 * Clase de nivel 2 que representa una pistola o arma corta
 * (ej: Desert Eagle, USP-S, Glock-18).
 *
 * <p>Hereda directamente de {@link Arma} porque las pistolas NO comparten
 * los atributos de las armas largas (alcance, modo ráfaga, etc.).</p>
 *
 * <p><b>Invariantes protegidos:</b></p>
 * <ul>
 *   <li>{@code cadencia} entre 1 y 1500 RPM</li>
 *   <li>{@code tipoMunicion} no vacío</li>
 *   <li>{@code cooldownMs} siempre &gt; 0</li>
 * </ul>
 *
 * @author Matías Jara (Vanzfranhait)
 */
public class ArmaCorta extends Arma {

    private final int cadencia;          // RPM
    private final long cooldownMs;       // milisegundos entre disparos
    private final String tipoMunicion;
    private final boolean puedeRafaga;

    /**
     * Constructor de arma corta.
     *
     * @param nombre        nombre del arma
     * @param daño          daño por disparo
     * @param precio        precio en la tienda
     * @param municionMax   capacidad del cargador
     * @param precision     precisión base (0.0 a 1.0)
     * @param equipo        "CT", "T" o "Ambos"
     * @param cadencia      disparos por minuto (1 a 1500)
     * @param tipoMunicion  tipo de munición (ej: "9mm")
     * @param puedeRafaga   si puede disparar en ráfaga
     */
    public ArmaCorta(String nombre, int daño, int precio, int municionMax,
                     float precision, String equipo,
                     int cadencia, String tipoMunicion, boolean puedeRafaga) {
        super(nombre, daño, precio, municionMax, precision, equipo);

        if (cadencia < 1 || cadencia > 1500) {
            throw new IllegalArgumentException("La cadencia debe estar entre 1 y 1500 RPM");
        }
        if (tipoMunicion == null || tipoMunicion.isBlank()) {
            throw new IllegalArgumentException("El tipo de munición no puede estar vacío");
        }

        this.cadencia = cadencia;
        this.cooldownMs = 60000L / cadencia;   // conversión RPM -> ms
        this.tipoMunicion = tipoMunicion;
        this.puedeRafaga = puedeRafaga;
    }

    /**
     * Dispara en modo rápido. Si el arma no puede ráfaga, lanza excepción.
     *
     * @return cantidad de disparos efectivamente realizados
     */
    public int dispararRapido() {
        if (!puedeRafaga) {
            throw new IllegalStateException(
                "El arma " + getNombre() + " no puede disparar en ráfaga");
        }
        int disparos = 0;
        for (int i = 0; i < 3 && puedeDisparar(); i++) {
            disparar();
            disparos++;
        }
        return disparos;
    }

    /**
     * Apuntar preciso: en un juego real aumentaría temporalmente la precisión.
     * Aquí lo dejamos como mensaje informativo (no modifica el estado).
     */
    public void apuntarPreciso() {
        // En una implementación real modificaría un atributo de precisión temporal
        // (por eso la precisión base es final; la temporal iría aparte)
    }

    // ------------------------------------------------------------------
    // Getters (solo lectura)
    // ------------------------------------------------------------------
    public int getCadencia()           { return cadencia; }
    public long getCooldownMs()        { return cooldownMs; }
    public String getTipoMunicion()    { return tipoMunicion; }
    public boolean isPuedeRafaga()     { return puedeRafaga; }

    // ------------------------------------------------------------------
    // Implementación de métodos abstractos heredados
    // ------------------------------------------------------------------

    @Override
    public String getTipo() {
        return "Arma Corta";
    }

    @Override
    public String describir() {
        return String.format(
            "%s [%s] - Daño: %d | Precio: $%d | Cadencia: %d RPM (%d ms) | Munición: %s | Ráfaga: %s",
            getNombre(), getTipo(), getDaño(), getPrecio(),
            cadencia, cooldownMs, tipoMunicion, puedeRafaga ? "Sí" : "No"
        );
    }
}
