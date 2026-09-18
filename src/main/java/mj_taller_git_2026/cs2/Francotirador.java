package mj_taller_git_2026.cs2;

/**
 * Especialización de {@link ArmaLarga} que representa un rifle
 * de francotirador (ej: AWP, SSG 08).
 *
 * <p>Añade comportamientos propios: zoom de la mira y aguantar
 * la respiración para estabilizar el disparo.</p>
 *
 * <p><b>Invariantes protegidos:</b></p>
 * <ul>
 *   <li>{@code zoom} entre 1 y 10</li>
 *   <li>{@code estabilidad} entre 0.0 y 1.0</li>
 * </ul>
 *
 * @author Matías Jara (Vanzfranhait)
 */
public class Francotirador extends ArmaLarga {

    private int zoom;              // 1 a 10
    private float estabilidad;     // 0.0 a 1.0

    /**
     * Constructor de francotirador.
     *
     * @param nombre      nombre del arma
     * @param daño        daño por disparo
     * @param precio      precio en la tienda
     * @param municionMax capacidad del cargador
     * @param precision   precisión base (0.0 a 1.0)
     * @param equipo      "CT", "T" o "Ambos"
     * @param alcance     alcance efectivo en metros
     * @param cadencia    disparos por minuto (RPM)
     * @param zoom        nivel de aumento inicial (1 a 10)
     */
    public Francotirador(String nombre, int daño, int precio, int municionMax,
                         float precision, String equipo,
                         int alcance, int cadencia, int zoom) {
        super(nombre, daño, precio, municionMax, precision, equipo, alcance, cadencia);

        if (zoom < 1 || zoom > 10) {
            throw new IllegalArgumentException("El zoom debe estar entre 1 y 10");
        }

        this.zoom = zoom;
        this.estabilidad = 0.5f;   // valor inicial medio
    }

    /**
     * Activa el zoom de la mira.
     *
     * @param nivel nivel de zoom deseado (1 a 10)
     */
    public void usarZoom(int nivel) {
        if (nivel < 1 || nivel > 10) {
            throw new IllegalArgumentException("El zoom debe estar entre 1 y 10");
        }
        this.zoom = nivel;
    }

    /**
     * Aguanta la respiración: sube la estabilidad al máximo por un momento.
     * (En un juego real tendría una duración; aquí lo simplificamos.)
     */
    public void aguantarRespiracion() {
        this.estabilidad = 1.0f;
    }

    /**
     * Suelta el aire: vuelve la estabilidad a su valor normal.
     */
    public void respirar() {
        this.estabilidad = 0.5f;
    }

    // ------------------------------------------------------------------
    // Getters (solo lectura)
    // ------------------------------------------------------------------
    public int getZoom()          { return zoom; }
    public float getEstabilidad() { return estabilidad; }

    // ------------------------------------------------------------------
    // Implementación de métodos abstractos heredados
    // ------------------------------------------------------------------

    @Override
    public String getTipo() {
        return "Francotirador";
    }

    @Override
    public String describir() {
        return String.format(
            "%s [%s] - Daño: %d | Precio: $%d | Alcance: %dm | Zoom: x%d | Estabilidad: %.1f",
            getNombre(), getTipo(), getDaño(), getPrecio(),
            getAlcance(), zoom, estabilidad
        );
    }

    // ------------------------------------------------------------------
    // Sobrescritura de disparar() para agregar lógica propia
    // ------------------------------------------------------------------
    @Override
    public int disparar() {
        // Si no está estabilizado, el disparo puede fallar
        // (aquí simulamos: si estabilidad < 0.7, hay 50% de fallar)
        if (estabilidad < 0.7f && Math.random() < 0.5) {
            return 0;   // disparo perdido
        }
        return super.disparar();
    }
}
