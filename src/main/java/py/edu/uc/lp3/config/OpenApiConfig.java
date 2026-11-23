package py.edu.uc.lp3.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("mj-taller-git-2026 — CS2 API")
                        .version("1.0.0")
                        .description("""
                                API de Counter-Strike 2 con:
                                - CRUD persistido en H2 (/api/db/armas)
                                - Regla 1: explosión de granada (la munición de las armas
                                  dentro del radio baja a la mitad)
                                - Regla 2: recarga rápida de francotirador (requiere
                                  estabilidad > 0.7; deja estabilidad = 0.5)
                                - Escenario POO-09: disparo coordinado con validación
                                  en A2 (munición) y A3 (vida) antes de persistir la jugada
                                """)
                        .contact(new Contact()
                                .name("Matías Jara")
                                .email("matiazjara@gmail.com")));
    }
}
