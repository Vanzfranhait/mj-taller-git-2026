package mj_taller_git_2026.cs2;

/**
 * Clase de nivel 2 que representa una granada
 * (ej: HE, Flashbang, Smoke, Molotov, Incendiary).
 *
 * <p>Hereda directamente de {@link Arma} porque las granadas NO disparan
 * como las armas de fuego; se lanzan y explotan.</p>
 *
 * <p><b>Invariantes protegidos:</b></p>
 * <ul>
 *   <li>{@code tipo} debe ser uno de los tipos válidos</li>
 *   <li>{@code radioExplosion} &gt; 0</li>
 *   <li>{@code tiempoActivacion} &gt; 0</li>
 *   <li>No se puede lanzar dos veces seguidas sin recargar</li>
 * </ul>
 *
 * @author Matías Jara (Vanzfranhait)
 */
public class Granada extends Arma {

    private static final String[] TIPOS_VALIDOS = {
        "flash", "humo", "frag", "incendio", "molotov"
    };

    private final String tipo;
    private final int radioExplosion;       // metros
    private final float tiempoActivacion;   // segundos
    private boolean lanzada;                // true si ya fue lanzada

    /**
     * Constructor de granada.
     *
     * @param nombre             nombre del arma
     * @param daño               daño en el centro de la explosión
     * @param precio             precio en la tienda
     * @param equipo             "CT", "T" o "Ambos"
     * @param tipo               "flash", "humo", "frag", "incendio", "molotov"
     * @param radioExplosion     radio de efecto en metros
     * @param tiempoActivacion   segundos hasta explotar
     */
    public Granada(String nombre, int daño, int precio, String equipo,
                   String tipo, int radioExplosion, float tiempoActivacion) {
        // Una granada tiene "municionMax = 1" porque solo se lanza una vez por compra
        super(nombre, daño, precio, 1, 1.0f, equipo);

        if (tipo == null || !esTipoValido(tipo)) {
            throw new IllegalArgumentException(
                "Tipo de granada inválido. Válidos: flash, humo, frag, incendio, molotov");
        }
        if (radioExplosion <= 0) {
            throw new IllegalArgumentException("El radio de explosión debe ser positivo");
        }
        if (tiempoActivacion <= 0) {
            throw new IllegalArgumentException("El tiempo de activación debe ser positivo");
        }

        this.tipo = tipo;
        this.radioExplosion = radioExplosion;
        this.tiempoActivacion = tiempoActivacion;
        this.lanzada = false;
    }

    /**
     * Lanza la granada. Solo se puede lanzar una vez.
     *
     * @throws IllegalStateException si ya fue lanzada
     */
    public void lanzar() {
        if (lanzada) {
            throw new IllegalStateException("La granada ya fue lanzada");
        }
        lanzada = true;
        // En un juego real, aquí empezaría el temporizador
    }

    /**
     * Simula la explosión. Solo puede explotar si ya fue lanzada.
     *
     * @return el daño infligido en el centro de la explosión
     * @throws IllegalStateException si la granada no fue lanzada
     */
    public int explotar() {
        if (!lanzada) {
            throw new IllegalStateException("No se puede explotar una granada que no fue lanzada");
        }
        return getDaño();
    }

    /**
     * Indica si la granada ya fue lanzada.
     */
    public boolean isLanzada() {
        return lanzada;
    }

    // ------------------------------------------------------------------
    // Getters (solo lectura)
    // ------------------------------------------------------------------
    public String getTipoGranada()      { return tipo; }
    public int getRadio()               { return radioExplosion; }
    public float getTiempoActivacion()  { return tiempoActivacion; }

    // ------------------------------------------------------------------
    // Implementación de métodos abstractos heredados
    // ------------------------------------------------------------------

    @Override
    public String getTipo() {
        return "Granada";
    }

    @Override
    public String describir() {
        return String.format(
            "%s [%s - %s] - Daño: %d | Precio: $%d | Radio: %dm | Activación: %.1fs",
            getNombre(), getTipo(), tipo, getDaño(), getPrecio(),
            radioExplosion, tiempoActivacion
        );
    }

    // ------------------------------------------------------------------
    // Método auxiliar privado
    // ------------------------------------------------------------------
    private static boolean esTipoValido(String t) {
        for (String valido : TIPOS_VALIDOS) {
            if (valido.equalsIgnoreCase(t)) {
                return true;
            }
        }
        return false;
    }
}
