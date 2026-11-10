package py.edu.uc.lp3.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import py.edu.uc.lp3.domain.Arma;

/**
 * Repositorio de persistencia para la familia de armas.
 *
 * <p>Spring Data JPA genera la implementación en tiempo de ejecución
 * a partir de esta interfaz. No hay que escribir SQL ni implementar
 * los métodos a mano.</p>
 *
 * <p>El tipo {@code Arma} es el padre de la familia, así que este
 * repositorio puede manejar {@code Francotirador}, {@code Granada},
 * etc., gracias al polimorfismo de JPA.</p>
 *
 * @author Matías Jara (Vanzfranhait)
 */
@Repository
public interface ArmaRepository extends JpaRepository<Arma, Long> {

    /**
     * Busca armas por su nombre exacto.
     * Spring Data genera la consulta a partir del nombre del método.
     */
    List<Arma> findByNombre(String nombre);

    /**
     * Busca armas por equipo (ej. "CT", "T" o "Ambos").
     */
    List<Arma> findByEquipo(String equipo);
}
