# Bitácora de asistencia de IA

## Asistente usado

- **Marca:** DeepSeek (interfaz web)
- **Modelo:** `deepseek-chat` (modo estándar, sin DeepThink/R1)

## Resumen de prompts

Durante el desarrollo del taller usé DeepSeek como asistente para:

### 1. Diseño del modelado CS2

- "Ayúdame a diseñar una jerarquía de clases para las armas de CS2 usando herencia de 3 niveles"
- "¿Qué atributos son comunes a todas las armas y cuáles son específicos de cada tipo?"

### 2. Implementación de clases

- "Genera la clase base Arma con atributos private y métodos abstractos describir() y getTipo()"
- "Implementa Francotirador, RifleAsalto, Subfusil y Escopeta heredando de ArmaLarga"
- "Agrega invariantes para validar que el zoom esté entre 1 y 10"
- "Crea ArmaCorta y Granada heredando directamente de Arma"

### 3. Controllers REST

- "Crea un @RestController que exponga el inventario sin if por tipo"
- "Agrega un IndexController con GET / y un controller de construcción con @RequestParam"
- "¿Cómo hago para que el controller no acceda a los campos del dominio?"

### 4. Sobrecarga y sobreescritura

- "Muéstrame cómo agregar sobrecarga de constructores en Arma y ArmaCorta"
- "Agrega sobrecarga del método disparar(int cantidad)"
- "Explica la diferencia entre sobrecarga y sobreescritura con ejemplos del dominio"

### 5. Git

- "Que es un PR?"
- "Cómo hago un PR en mi propio repositorio"
- "Cómo resuelvo un push rechazado porque el remoto tiene commits que no tengo"
- "Cómo hago merge después de un pull con historiales divergentes"

### 6. README con Mermaid

- "Genera el diagrama Mermaid de la jerarquía de clases CS2"
- "¿Cómo agrego el diagrama al README para que GitHub lo renderice?"

### 7. POO-08 — Persistencia H2, reglas y excepciones

- "¿Cómo configuro H2 en memoria con Spring Data JPA y qué propiedades van en application.properties?"
- "¿Qué estrategia de herencia de JPA conviene para una familia con Arma, ArmaLarga, Francotirador y Granada?"
- "¿Cómo hago que Jackson distinga el subtipo al crear y al listar (tipo: francotirador vs tipo: granada)?"
- "¿Cómo se declara una jerarquía de excepciones con un handler por tipo en Spring Boot?"
- "¿Cómo separo la lógica del controller a un service para que el controller no toque el repositorio directamente?"
- "¿Cómo evito que el dominio lance excepciones y que las lance el service?"
- "¿Cómo armo una colección de Insomnia para que se pueda importar en Insomnia 13 (parentId, variables de entorno)?"
- "¿Por qué el curl con -d '...' falla en Git Bash en Windows y cómo lo soluciono con -d @archivo.json?"

## Reflexión personal

Usé la IA como **guía y asistente** para:

- Aprender el flujo de trabajo con Git (PR, merge, pull, resolución de conflictos)
- Generar documentación clara
- Separar responsabilidades en capas (controller → service → repository)
- Entender las decisiones de diseño de JPA (JOINED, @JsonTypeInfo, @Transactional)
- Preparar la colección de Insomnia y depurar errores de JSON en Windows

## Archivos asistidos

- Clases del dominio: `src/main/java/py/edu/uc/lp3/domain/*.java`
- Controllers: `src/main/java/py/edu/uc/lp3/rest/controller/*.java`
- Services: `src/main/java/py/edu/uc/lp3/service/*.java`
- Repositorio: `src/main/java/py/edu/uc/lp3/repository/ArmaRepository.java`
- Excepciones: `src/main/java/py/edu/uc/lp3/exception/*.java`
- DTO: `src/main/java/py/edu/uc/lp3/rest/dto/ArmaRequest.java`
- Configuración: `src/main/resources/application.properties`
- Test unitario: `src/test/java/py/edu/uc/lp3/domain/VanzfranhaitTest.java`
- README con diagrama Mermaid
- RUN.md
- Colección de Insomnia: `insomnia/coleccion-poo-08.json`
- Documento de entrega

### 8. POO-09 — Challenge de APIs cruzadas

- "¿Cómo habilito springdoc-openapi en Spring Boot 3 para publicar /v3/api-docs?"
- "¿Qué anotaciones (@Operation, @ApiResponse) agrego para que un extraño entienda las reglas desde el documento?"
- "¿Cómo configuro un RestClient de Spring con timeout de conexión y de lectura?"
- "¿Cómo leo el body de un 4xx/422 del compañero sin que RestClient lance excepción?"
- "¿Cómo atribuyo un rechazo a A2 vs A3 en el handler, sin copiar el dominio del compañero?"
- "¿Cómo garantizo que la base no cambie si A2 o A3 rechazan?"
- "¿Cómo implemento un fallback local cuando el servicio del compañero no responde, y cómo lo marco en la respuesta?"
- "¿Cómo exporto una colección de Insomnia con entorno y variables (a1, a2, a3) para que se pueda importar?"
- "¿Cómo pruebo el escenario con curl en Git Bash en Windows (comillas simples, JSON en una línea)?"
- "¿Cómo verifico que la jugada se persistió con un GET posterior y que los rechazos no agregaron nada?"

**Decisiones tomadas con IA y revisadas por mí:**

- Orden de las llamadas: A2 (munición) primero, A3 (daño) después. Si A2 rechaza, no se llama a A3.
- La persistencia (`jugadaRepository.save`) va **después** de que ambas validaciones pasaron, para que un rechazo no toque la base.
- El fallback **solo valida** (aceptar/rechazar), no calcula la aritmética del daño. El cálculo local de `vidaDespues`/`blindajeDespues` lo hace A1 en `EscenarioService`.
- El fallback se marca explícitamente con `origenA2`/`origenA3: FALLBACK` en la jugada persistida, para que no se confunda con HTTP real.

**Qué NO hizo la IA:** el diseño del escenario ("disparo coordinado"), la elección de qué regla de cada compañero usar (disparo de A2, daño de A3), las pruebas manuales con los tres servicios arriba, y la decisión de dejar el fallback como contingencia y no como atajo.

**Pendiente conocido (documentado, no bloquea la entrega):** el cálculo local de daño en `EscenarioService` no descuenta el blindaje de la vida (da `vidaDespues: 0` en lugar de `10` para `danio: 180`, `blindaje: 100`). La validación (aceptar/rechazar) sí replica la regla de A3; la aritmética es una simplificación propia. 
