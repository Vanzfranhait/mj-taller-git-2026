package py.edu.uc.lp3.rest.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import py.edu.uc.lp3.exception.ArmaNoEncontradaException;
import py.edu.uc.lp3.exception.ReglaDeJuegoException;

/**
 * Handler global de excepciones.
 *
 * <p>Atrapa las excepciones del dominio y las convierte en JSON con
 * el código HTTP adecuado. El cliente distingue un error de "no encontrado"
 * (404) de una regla de juego rota (400) por el código y por el campo
 * "error".</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Arma no encontrada -> 404 Not Found
     */
    @ExceptionHandler(ArmaNoEncontradaException.class)
    public ResponseEntity<Map<String, Object>> handleArmaNoEncontrada(ArmaNoEncontradaException e) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "not_found");
        body.put("mensaje", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Regla de juego rota -> 400 Bad Request
     */
    @ExceptionHandler(ReglaDeJuegoException.class)
    public ResponseEntity<Map<String, Object>> handleReglaDeJuego(ReglaDeJuegoException e) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "regla_de_juego");
        body.put("mensaje", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
