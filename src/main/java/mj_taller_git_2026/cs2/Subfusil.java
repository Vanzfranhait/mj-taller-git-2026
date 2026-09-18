package mj_taller_git_2026.cs2;

/**
 * Especialización de {@link ArmaLarga} que representa un subfusil
 * (SMG) (ej: MP9, MAC-10, P90).
 *
 * <p>Añade comportamientos propios: alta movilidad y dispersión
 * al disparar en movimiento.</p>
 *
 * <p><b>Invariantes protegidos:</b></p>
 * <ul>
 *   <li>{@code movilidad} entre 0.0 y 1.0</li>
 *   <li>{@code dispersionMovimiento} entre 0.0 y 1.0</li>
 * </ul>
 *
 * @author Matías Jara (Vanzfranhait)
 */
public class Subfusil extends ArmaLarga {

    private float movilidad;              // 0.0 a 1.0
    private final float movilidadBase;
    private final float dispersionMovimiento;   // 0.0 a 1.0

    /**
     * Constructor de subfusil.
     *
     * @param nombre                nombre del arma
     * @param daño                  daño por disparo
     * @param precio                precio en la tienda
     * @param municionMax           capacidad del cargador
     * @param precision             precisión base (0.0 a 1.0)
     * @param equipo                "CT", "T" o "Ambos"
     * @param alcance               alcance efectivo en metros
     * @param cadencia              disparos por minuto (RPM)
     * @param movilidad             movilidad inicial (0.0 a 1.0)
     * @param dispersionMovimiento  dispersión al moverse (0.0 a 1.0)
     */
    public Subfusil(String nombre, int daño, int precio, int municionMax,
                    float precision, String equipo,
                    int alcance, int cadencia,
                    float movilidad, float dispersionMovimiento) {
        super(nombre, daño, precio, municionMax, precision, equipo, alcance, cadencia);

        if (movilidad < 0.0f || movilidad > 1.0f) {
            throw new IllegalArgumentException("La movilidad debe estar entre 0.0 y 1.0");
        }
        if (dispersionMovimiento < 0.0f || dispersionMovimiento > 1.0f) {
            throw new IllegalArgumentException("La dispersión debe estar entre 0.0 y 1.0");
        }

        this.movilidad = movilidad;
        this.movilidadBase = movilidad;
        this.dispersionMovimiento = dispersionMovimiento;
    }

    /**
     * Dispara mientras el jugador se mueve. Aplica dispersión:
     * si la dispersión es alta, hay probabilidad de fallar.
     *
     * @return el daño efectivo (0 si el disparo se dispersó)
     */
    public int dispararCorriendo() {
        if (Math.random() < dispersionMovimiento) {
            return 0;   // disparo desviado por el movimiento
        }
        return disparar();
    }

    /**
     * Aumenta temporalmente la movilidad al máximo.
     */
    public void aumentarMovilidad() {
        this.movilidad = 1.0f;
    }

    /**
     * Vuelve la movilidad a su valor base.
     */
    public void descansar() {
        this.movilidad = movilidadBase;
    }

    // ------------------------------------------------------------------
    // Getters (solo lectura)
    // ------------------------------------------------------------------
    public float getMovilidad()             { return movilidad; }
    public float getMovilidadBase()         { return movilidadBase; }
    public float getDispersionMovimiento()  { return dispersionMovimiento; }

    // ------------------------------------------------------------------
    // Implementación de métodos abstractos heredados
    // ------------------------------------------------------------------

    @Override
    public String getTipo() {
        return "Subfusil";
    }

    @Override
    public String describir() {
        return String.format(
            "%s [%s] - Daño: %d | Precio: $%d | Alcance: %dm | Movilidad: %.1f | Dispersión: %.1f",
            getNombre(), getTipo(), getDaño(), getPrecio(),
            getAlcance(), movilidad, dispersionMovimiento
        );
    }
}
