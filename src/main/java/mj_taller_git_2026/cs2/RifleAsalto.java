package mj_taller_git_2026.cs2;

/**
 * Especialización de {@link ArmaLarga} que representa un rifle
 * de asalto (ej: AK-47, M4A1-S, FAMAS).
 *
 * <p>Añade comportamientos propios: control del retroceso y
 * precisión en modo ráfaga.</p>
 *
 * <p><b>Invariantes protegidos:</b></p>
 * <ul>
 *   <li>{@code retroceso} entre 1 y 10</li>
 *   <li>{@code precisionRafaga} entre 0.0 y 1.0</li>
 * </ul>
 *
 * @author Matías Jara (Vanzfranhait)
 */
public class RifleAsalto extends ArmaLarga {

    private int retroceso;             // 1 a 10 (mayor = más patada)
    private final int retrocesoBase;   // valor original
    private float precisionRafaga;     // 0.0 a 1.0

    /**
     * Constructor de rifle de asalto.
     *
     * @param nombre           nombre del arma
     * @param daño             daño por disparo
     * @param precio           precio en la tienda
     * @param municionMax      capacidad del cargador
     * @param precision        precisión base (0.0 a 1.0)
     * @param equipo           "CT", "T" o "Ambos"
     * @param alcance          alcance efectivo en metros
     * @param cadencia         disparos por minuto (RPM)
     * @param retroceso        nivel de retroceso inicial (1 a 10)
     * @param precisionRafaga  precisión en ráfaga (0.0 a 1.0)
     */
    public RifleAsalto(String nombre, int daño, int precio, int municionMax,
                       float precision, String equipo,
                       int alcance, int cadencia,
                       int retroceso, float precisionRafaga) {
        super(nombre, daño, precio, municionMax, precision, equipo, alcance, cadencia);

        if (retroceso < 1 || retroceso > 10) {
            throw new IllegalArgumentException("El retroceso debe estar entre 1 y 10");
        }
        if (precisionRafaga < 0.0f || precisionRafaga > 1.0f) {
            throw new IllegalArgumentException("La precisión en ráfaga debe estar entre 0.0 y 1.0");
        }

        this.retroceso = retroceso;
        this.retrocesoBase = retroceso;
        this.precisionRafaga = precisionRafaga;
    }

    /**
     * Técnica del jugador para controlar el retroceso.
     * Reduce el retroceso actual en 2 puntos (mínimo 1).
     */
    public void controlarRetroceso() {
        if (retroceso > 1) {
            retroceso = Math.max(1, retroceso - 2);
        }
    }

    /**
     * Descansar el arma: vuelve el retroceso a su valor base.
     */
    public void descansar() {
        this.retroceso = retrocesoBase;
    }

    // ------------------------------------------------------------------
    // Getters (solo lectura)
    // ------------------------------------------------------------------
    public int getRetroceso()           { return retroceso; }
    public int getRetrocesoBase()       { return retrocesoBase; }
    public float getPrecisionRafaga()   { return precisionRafaga; }

    // ------------------------------------------------------------------
    // Implementación de métodos abstractos heredados
    // ------------------------------------------------------------------

    @Override
    public String getTipo() {
        return "Rifle de Asalto";
    }

    @Override
    public String describir() {
        return String.format(
            "%s [%s] - Daño: %d | Precio: $%d | Alcance: %dm | Retroceso: %d/10 | Precisión ráfaga: %.1f",
            getNombre(), getTipo(), getDaño(), getPrecio(),
            getAlcance(), retroceso, precisionRafaga
        );
    }

    // ------------------------------------------------------------------
    // Sobrescritura: la ráfaga aquí se ve afectada por el retroceso
    // ------------------------------------------------------------------
    @Override
    public int dispararRafaga() {
        int disparos = super.dispararRafaga();
        // Cada ráfaga aumenta el retroceso (simula la patada del arma)
        if (disparos > 0) {
            retroceso = Math.min(10, retroceso + 1);
        }
        return disparos;
    }
}
