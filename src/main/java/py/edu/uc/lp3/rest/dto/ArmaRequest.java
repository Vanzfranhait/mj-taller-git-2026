package py.edu.uc.lp3.rest.dto;

import py.edu.uc.lp3.domain.Equipo;

/**
 * DTO de entrada para crear un arma.
 *
 * <p>Campos planos: el service decide qué subtipo construir según
 * {@code tipo}. Esto evita que Jackson tenga que lidiar con la
 * herencia de {@code Arma} al deserializar, y permite validar en el
 * service (Criterio 3) en vez de en el dominio.</p>
 */
public class ArmaRequest {

    private String tipo;          // "francotirador" | "granada"
    private String nombre;
    private Integer daño;         // wrapper: acepta null si no viene
    private Integer precio;
    private Integer municionMax;
    private Float precision;
    private Equipo equipo;

    // Francotirador / ArmaLarga
    private Integer alcance;
    private Integer cadencia;
    private Integer zoom;

    // Granada
    private String tipoGranada;
    private Integer radioExplosion;
    private Float tiempoActivacion;

    // getters y setters
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Integer getDaño() { return daño; }
    public void setDaño(Integer daño) { this.daño = daño; }
    public Integer getPrecio() { return precio; }
    public void setPrecio(Integer precio) { this.precio = precio; }
    public Integer getMunicionMax() { return municionMax; }
    public void setMunicionMax(Integer municionMax) { this.municionMax = municionMax; }
    public Float getPrecision() { return precision; }
    public void setPrecision(Float precision) { this.precision = precision; }
    public Equipo getEquipo() { return equipo; }
    public void setEquipo(Equipo equipo) { this.equipo = equipo; }
    public Integer getAlcance() { return alcance; }
    public void setAlcance(Integer alcance) { this.alcance = alcance; }
    public Integer getCadencia() { return cadencia; }
    public void setCadencia(Integer cadencia) { this.cadencia = cadencia; }
    public Integer getZoom() { return zoom; }
    public void setZoom(Integer zoom) { this.zoom = zoom; }
    public String getTipoGranada() { return tipoGranada; }
    public void setTipoGranada(String tipoGranada) { this.tipoGranada = tipoGranada; }
    public Integer getRadioExplosion() { return radioExplosion; }
    public void setRadioExplosion(Integer radioExplosion) { this.radioExplosion = radioExplosion; }
    public Float getTiempoActivacion() { return tiempoActivacion; }
    public void setTiempoActivacion(Float tiempoActivacion) { this.tiempoActivacion = tiempoActivacion; }
}
