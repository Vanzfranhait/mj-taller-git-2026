package py.edu.uc.lp3.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests unitarios que verifican el comportamiento del dominio CS2
 * despues de la refactorizacion (sin excepciones en el dominio).
 */
class VanzfranhaitTest {

    // ------------------------------------------------------------------
    // Test 1: Francotirador ajusta zoom dentro de rango
    // ------------------------------------------------------------------
    @Test
    void francotirador_ajustaZoomDentroDeRango() {
        Francotirador awp = new Francotirador(
            "AWP", 115, 4750, 10, 1.0f, Equipo.AMBOS, 100, 41, 10);

        // El zoom ahora se limita internamente sin lanzar excepcion
        awp.usarZoom(0);
        assertEquals(1, awp.getZoom()); // Se ajustó al mínimo

        awp.usarZoom(15);
        assertEquals(10, awp.getZoom()); // Se ajustó al máximo

        awp.usarZoom(5);
        assertEquals(5, awp.getZoom());
    }

    // ------------------------------------------------------------------
    // Test 2: Arma no dispara sin munición
    // ------------------------------------------------------------------
    @Test
    void armaCorta_noDisparaSinMunicion() {
        ArmaCorta usp = new ArmaCorta(
            "USP-S", 35, 200, 12, 0.9f, Equipo.CT, 350, "9mm", false);

        for (int i = 0; i < 12; i++) {
            assertTrue(usp.disparar() > 0, "Debería disparar con munición");
        }

        assertEquals(0, usp.disparar());
        assertEquals(0, usp.getMunicionActual());
    }

    // ------------------------------------------------------------------
    // Test 3: Granada cambia su estado al lanzarse
    // ------------------------------------------------------------------
    @Test
    void granada_cambiaEstadoAlLanzar() {
        Granada he = new Granada("HE", 98, 300, Equipo.AMBOS, "frag", 8, 1.6f);

        assertFalse(he.isLanzada());
        he.lanzar();
        assertTrue(he.isLanzada());
    }

    // ------------------------------------------------------------------
    // Test 4: Granada explota (la validacion de doble uso va en el Service)
    // ------------------------------------------------------------------
    @Test
    void granada_explotaYDevuelveDano() {
        Granada flash = new Granada(
            "Flashbang", 10, 200, Equipo.AMBOS, "flash", 10, 1.6f);

        int dano = flash.explotar();
        assertEquals(10, dano);
    }

    // ------------------------------------------------------------------
    // Test 5: Aguantar respiracion sube la estabilidad
    // ------------------------------------------------------------------
    @Test
    void francotirador_aguantarRespiracionSubeEstabilidad() {
        Francotirador awp = new Francotirador(
            "AWP", 115, 4750, 10, 1.0f, Equipo.AMBOS, 100, 41, 10);

        assertEquals(0.5f, awp.getEstabilidad());
        awp.aguantarRespiracion();
        assertEquals(1.0f, awp.getEstabilidad());
    }

    // ------------------------------------------------------------------
    // Test 6: El enum Equipo acepta los valores del juego
    // ------------------------------------------------------------------
    @Test
    void equipo_aceptaValoresValidos() {
        Francotirador awp = new Francotirador(
            "AWP", 115, 4750, 10, 1.0f, Equipo.CT, 100, 41, 10);
        
        assertEquals(Equipo.CT, awp.getEquipo());
    }
}
