package py.edu.uc.lp3.domain;

import jakarta.persistence.Entity;

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
@Entity
public class Francotirador extends ArmaLarga {

    private int zoom;              // 1 a 10
    private float estabilidad;     // 0.0 a 1.0

    /**
     * Constructor vacío requerido por JPA.
     */
    protected Francotirador() {
        super();
        this.zoom = 1;
        this.estabilidad = 0.0f;
    }

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
    @com.fasterxml.jackson.annotation.JsonCreator
    public Francotirador(
            @com.fasterxml.jackson.annotation.JsonProperty("nombre") String nombre,
            @com.fasterxml.jackson.annotation.JsonProperty("daño") int daño,
            @com.fasterxml.jackson.annotation.JsonProperty("precio") int precio,
            @com.fasterxml.jackson.annotation.JsonProperty("municionMax") int municionMax,
            @com.fasterxml.jackson.annotation.JsonProperty("precision") float precision,
            @com.fasterxml.jackson.annotation.JsonProperty("equipo") Equipo equipo,
            @com.fasterxml.jackson.annotation.JsonProperty("alcance") int alcance,
            @com.fasterxml.jackson.annotation.JsonProperty("cadencia") int cadencia,
            @com.fasterxml.jackson.annotation.JsonProperty("zoom") int zoom) {
        super(nombre, daño, precio, municionMax, precision, equipo, alcance, cadencia);

        this.zoom = Math.max(1, Math.min(10, zoom));
        this.estabilidad = 0.5f;
    }
    /**
     * Activa el zoom de la mira.
     */
    @Override
    public void usarZoom(int nivel) {
        this.zoom = Math.max(1, Math.min(10, nivel));
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
        return super.disparar();
    }
}
