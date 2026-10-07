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

-”Que es un PR?”
- "Cómo hago un PR en mi propio repositorio"
- "Cómo resuelvo un push rechazado porque el remoto tiene commits que no tengo"
- "Cómo hago merge después de un pull con historiales divergentes"

### 6. README con Mermaid

- "Genera el diagrama Mermaid de la jerarquía de clases CS2"
- "¿Cómo agrego el diagrama al README para que GitHub lo renderice?"


## Reflexión personal

Usé la IA como **guía y asistente** para:

- Aprender el flujo de trabajo con Git (PR, merge, pull, resolución de conflictos)
- Generar documentación clara


## Archivos asistidos

- Clases del dominio: `src/main/java/mj_taller_git_2026/cs2/*.java`
- Controllers: `src/main/java/mj_taller_git_2026/cs2/web/*.java`
- Test unitario: `src/test/java/mj_taller_git_2026/cs2/VanzfranhaitTest.java`
- README con diagrama Mermaid
- Documento de entrega


