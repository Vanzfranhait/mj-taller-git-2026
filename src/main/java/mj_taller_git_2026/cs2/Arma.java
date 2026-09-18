package mj_taller_git_2026.cs2;

/**
 * Clase base abstracta que representa un arma de Counter-Strike 2.
 *
 * <p>Contiene únicamente lo que es común a TODAS las armas: identificación,
 * daño, precio, munición y equipo. Los comportamientos específicos
 * (cadencia, zoom, explosión, etc.) viven en las subclases.</p>
 *
 * <p><b>Invariantes protegidos:</b></p>
 * <ul>
 *   <li>{@code daño} siempre &gt;= 0</li>
 *   <li>{@code precio} siempre &gt;= 0</li>
 *   <li>{@code municionActual} siempre entre 0 y {@code municionMax}</li>
 *   <li>{@code precision} siempre entre 0.0 y 1.0</li>
 * </ul>
 *
 * @author Matías Jara (Vanzfranhait)
 */
public abstract class Arma {

    // ------------------------------------------------------------------
    // Atributos privados (ocultamiento: nadie de afuera los toca)
    // ------------------------------------------------------------------
    private final String nombre;
    private final int daño;
    private final int precio;
    private final int municionMax;
    private int municionActual;
    private final float precision;
    private final String equipo;

    // ------------------------------------------------------------------
    // Constructor protegido: solo las subclases pueden invocarlo
    // ------------------------------------------------------------------
    protected Arma(String nombre, int daño, int precio, int municionMax,
                   float precision, String equipo) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        if (daño < 0) {
            throw new IllegalArgumentException("El daño no puede ser negativo");
        }
        if (precio < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        if (municionMax <= 0) {
            throw new IllegalArgumentException("La munición máxima debe ser positiva");
        }
        if (precision < 0.0f || precision > 1.0f) {
            throw new IllegalArgumentException("La precisión debe estar entre 0.0 y 1.0");
        }
        if (equipo == null || equipo.isBlank()) {
            throw new IllegalArgumentException("El equipo no puede estar vacío");
        }

        this.nombre = nombre;
        this.daño = daño;
        this.precio = precio;
        this.municionMax = municionMax;
        this.municionActual = municionMax;   // arranca con cargador lleno
        this.precision = precision;
        this.equipo = equipo;
    }

    // ------------------------------------------------------------------
    // Comportamientos comunes (mensajes que cambian el estado)
    // ------------------------------------------------------------------

    /**
     * Dispara un proyectil. Verifica que haya munición antes de disparar.
     * @return el daño infligido, o 0 si no había munición
     */
    public int disparar() {
        if (municionActual <= 0) {
            return 0;
        }
        municionActual--;
        return daño;
    }

    /**
     * Recarga el arma al máximo. No hace nada si ya está llena.
     */
    public void recargar() {
        this.municionActual = this.municionMax;
    }

    /**
     * Indica si el arma puede disparar.
     */
    public boolean puedeDisparar() {
        return municionActual > 0;
    }

    // ------------------------------------------------------------------
    // Getters (solo lectura; no hay setters públicos)
    // ------------------------------------------------------------------
    public String getNombre()       { return nombre; }
    public int getDaño()            { return daño; }
    public int getPrecio()          { return precio; }
    public int getMunicionMax()     { return municionMax; }
    public int getMunicionActual()  { return municionActual; }
    public float getPrecision()     { return precision; }
    public String getEquipo()       { return equipo; }

    // ------------------------------------------------------------------
    // Comportamiento que las subclases DEBEN implementar
    // ------------------------------------------------------------------

    /**
     * Devuelve una descripción específica del tipo de arma.
     * Cada subclase decide cómo comportarse.
     */
    public abstract String describir();

    /**
     * Devuelve el tipo de arma en texto (pistola, rifle, etc.).
     */
    public abstract String getTipo();
}
