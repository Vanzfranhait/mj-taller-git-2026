package py.edu.uc.lp3.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "jugada")
public class Jugada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String modelo;
    private Integer carga;
    private Integer distancia;
    private Integer vidaAntes;
    private Integer vidaDespues;
    private Integer blindajeAntes;
    private Integer blindajeDespues;

    private String origenA2;   // "A2-http" o "A2-fallback"
    private String origenA3;   // "A3-http" o "A3-fallback"

    private LocalDateTime timestamp;

    protected Jugada() {
        // JPA
    }

    public Jugada(String modelo, Integer carga, Integer distancia,
                  Integer vidaAntes, Integer vidaDespues,
                  Integer blindajeAntes, Integer blindajeDespues,
                  String origenA2, String origenA3,
                  LocalDateTime timestamp) {
        this.modelo = modelo;
        this.carga = carga;
        this.distancia = distancia;
        this.vidaAntes = vidaAntes;
        this.vidaDespues = vidaDespues;
        this.blindajeAntes = blindajeAntes;
        this.blindajeDespues = blindajeDespues;
        this.origenA2 = origenA2;
        this.origenA3 = origenA3;
        this.timestamp = timestamp;
    }

    public Long getId() { return id; }
    public String getModelo() { return modelo; }
    public Integer getCarga() { return carga; }
    public Integer getDistancia() { return distancia; }
    public Integer getVidaAntes() { return vidaAntes; }
    public Integer getVidaDespues() { return vidaDespues; }
    public Integer getBlindajeAntes() { return blindajeAntes; }
    public Integer getBlindajeDespues() { return blindajeDespues; }
    public String getOrigenA2() { return origenA2; }
    public String getOrigenA3() { return origenA3; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
