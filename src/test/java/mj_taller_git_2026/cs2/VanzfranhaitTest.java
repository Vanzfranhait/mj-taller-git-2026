package mj_taller_git_2026.cs2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests unitarios que verifican los invariantes del dominio CS2.
 *
 * <p>Estos tests demuestran que las clases del dominio NO permiten
 * que un objeto quede en un estado inválido, ni siquiera desde fuera.</p>
 *
 * @author Matías Jara (Vanzfranhait)
 */
class VanzfranhaitTest {

    // ------------------------------------------------------------------
    // Test 1: Francotirador rechaza zoom fuera de rango
    // ------------------------------------------------------------------
    @Test
    void francotirador_noAceptaZoomFueraDeRango() {
        Francotirador awp = new Francotirador(
            "AWP", 115, 4750, 10, 1.0f, "Ambos", 100, 41, 10);

        // Invariante: zoom entre 1 y 10
        assertThrows(IllegalArgumentException.class, () -> awp.usarZoom(0));
        assertThrows(IllegalArgumentException.class, () -> awp.usarZoom(11));
        assertThrows(IllegalArgumentException.class, () -> awp.usarZoom(-5));

        // El zoom válido sí funciona
        awp.usarZoom(5);
        assertEquals(5, awp.getZoom());
    }

    // ------------------------------------------------------------------
    // Test 2: Arma no dispara sin munición
    // ------------------------------------------------------------------
    @Test
    void armaCorta_noDisparaSinMunicion() {
        ArmaCorta usp = new ArmaCorta(
            "USP-S", 35, 200, 12, 0.9f, "CT", 350, "9mm", false);

        // Disparar 12 veces (munición completa)
        for (int i = 0; i < 12; i++) {
            assertTrue(usp.disparar() > 0, "Debería disparar con munición");
        }

        // La 13ª vez: sin munición, no dispara
        assertEquals(0, usp.disparar());
        assertEquals(0, usp.getMunicionActual());
    }

    // ------------------------------------------------------------------
    // Test 3: Granada no explota sin lanzar
    // ------------------------------------------------------------------
    @Test
    void granada_noExplotaSinLanzar() {
        Granada he = new Granada("HE", 98, 300, "Ambos", "frag", 8, 1.6f);

        // Invariante: no se puede explotar algo que no fue lanzado
        assertThrows(IllegalStateException.class, he::explotar);
    }

    // ------------------------------------------------------------------
    // Test 4: Granada solo se lanza una vez
    // ------------------------------------------------------------------
    @Test
    void granada_soloSeLanzaUnaVez() {
        Granada flash = new Granada(
            "Flashbang", 0, 200, "Ambos", "flash", 10, 1.6f);

        flash.lanzar();
        assertTrue(flash.isLanzada());

        // Segundo intento: excepción
        assertThrows(IllegalStateException.class, flash::lanzar);
    }

    // ------------------------------------------------------------------
    // Test 5: ArmaCorta sin ráfaga no puede disparar rápido
    // ------------------------------------------------------------------
    @Test
    void armaCorta_sinRafaga_lanzaExcepcion() {
        ArmaCorta usp = new ArmaCorta(
            "USP-S", 35, 200, 12, 0.9f, "CT", 350, "9mm", false);

        assertThrows(IllegalStateException.class, usp::dispararRapido);
    }

    // ------------------------------------------------------------------
    // Test 6 (bonus): Granada rechaza tipo inválido en constructor
    // ------------------------------------------------------------------
    @Test
    void granada_rechazaTipoInvalido() {
        assertThrows(IllegalArgumentException.class,
            () -> new Granada("Bomba", 50, 300, "Ambos", "nuclear", 20, 2.0f));
    }
}
