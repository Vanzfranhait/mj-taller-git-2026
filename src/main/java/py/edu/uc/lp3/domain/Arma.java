package py.edu.uc.lp3.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

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
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "tipo"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Francotirador.class, name = "francotirador"),
    @JsonSubTypes.Type(value = Granada.class, name = "granada")
})
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Arma implements Recargable {

    // ------------------------------------------------------------------
    // Identificador de persistencia (lo asigna la base de datos)
    // ------------------------------------------------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ------------------------------------------------------------------
    // Atributos privados (ocultamiento: nadie de afuera los toca)
    // ------------------------------------------------------------------
    private String nombre;
    private int daño;
    private int precio;
    private int municionMax;
    private int municionActual;
    private float precision;
    private Equipo equipo;

     //...
    // ------------------------------------------------------------------
    // Constructor protegido: solo las subclases pueden invocarlo
    // ------------------------------------------------------------------
    protected Arma() {
        this.nombre = "Sin nombre";
        this.daño = 0;
        this.precio = 0;
        this.municionMax = 1;
        this.municionActual = 0;
        this.precision = 0.0f;
        this.equipo = Equipo.AMBOS;
    }

    protected Arma(String nombre, int daño, int precio, int municionMax,
                   float precision, Equipo equipo) {
        this.nombre = (nombre == null || nombre.isBlank()) ? "Sin nombre" : nombre;
        this.daño = Math.max(0, daño);
        this.precio = Math.max(0, precio);
        this.municionMax = Math.max(1, municionMax);
        this.municionActual = this.municionMax;
        this.precision = Math.max(0.0f, Math.min(1.0f, precision));
        this.equipo = (equipo == null) ? Equipo.AMBOS : equipo;
    }

    protected Arma(String nombre, int daño) {
        this(nombre, daño, 0, 1, 1.0f, Equipo.AMBOS);
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

    // Método sobrecargado (N disparos)
    public int disparar(int cantidad) {
        int dañoTotal = 0;
        for (int i = 0; i < cantidad && municionActual > 0; i++) {
            dañoTotal += disparar();
        }
        return dañoTotal;
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
    // Mensajes opcionales: la clase base los declara "no soportados"
    // por defecto; las subclases que sí pueden los sobrescriben.
    // ------------------------------------------------------------------

    /**
     * Lanza el arma. Solo las granadas lo soportan.
     *
     * @throws UnsupportedOperationException si el arma no se lanza
     */
    public void lanzar() { }

    /** * Explota el arma. Solo las granadas lo soportan. * * @return el daño infligido en el centro de la explosión * @throws UnsupportedOperationException si 
     el arma no explota */
    public int explotar() { return 0; } 

     /**
     * Cambia el zoom. Solo los francotiradores lo soportan.
     *
     * @param nivel nivel de zoom deseado
     * @throws UnsupportedOperationException si el arma no tiene zoom
     */
    public void usarZoom(int nivel) { } 

    /**
     * Método de simulación: reduce la munición a un valor específico.
     * Solo debe usarse desde reglas de negocio.
     */
    public void setMunicionActualParaSimulacion(int nuevaMunicion) {
            this.municionActual = nuevaMunicion;// El service valida el rango
    }

    // ------------------------------------------------------------------
    // Getters (solo lectura; no hay setters públicos)
    // ------------------------------------------------------------------
    public Long getId()             { return id; }
    public String getNombre()       { return nombre; }
    public int getDaño()            { return daño; }
    public int getPrecio()          { return precio; }
    public int getMunicionMax()     { return municionMax; }
    public int getMunicionActual()  { return municionActual; }
    public float getPrecision()     { return precision; }
    public Equipo getEquipo() { return equipo; }
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
     * Se ignora en JSON para evitar conflictos con el discriminador.
     */
    @JsonIgnore
    public abstract String getTipo();
}
