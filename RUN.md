# RUN.md — Instrucciones de ejecución

## Requisitos

- Java 21
- Maven (incluido en el wrapper `./mvnw`)

## Cómo levantar la API

```bash
./mvnw spring-boot:run
```

La API arranca en: **http://localhost:8080**

Para detenerla: `Ctrl + C`

## Cómo correr los tests

```bash
./mvnw test
```

## Estructura del proyecto

```
src/main/java/py/edu/uc/lp3/
├── MjTallerGit2026Application.java   ← clase Application del starter
├── domain/                            ← modelado Counter-Strike 2
│   ├── Arma.java                      ← clase base abstracta
│   ├── ArmaLarga.java                 ← clase abstracta intermedia
│   ├── ArmaCorta.java
│   ├── Francotirador.java
│   ├── Granada.java
│   ├── RifleAsalto.java
│   ├── Subfusil.java
│   ├── Escopeta.java
│   ├── Equipo.java                    ← enum (T, CT, AMBOS)
│   └── Recargable.java                ← interfaz de dominio
├── repository/
│   └── ArmaRepository.java            ← interfaz Spring Data JPA
├── service/
│   ├── ArmaService.java               ← lógica del CRUD
│   └── SimulacionService.java         ← reglas de negocio (@Transactional)
├── exception/
│   ├── JuegoException.java            ← base (RuntimeException)
│   ├── ArmaNoEncontradaException.java ← 404
│   └── ReglaDeJuegoException.java     ← 400
└── rest/
    ├── controller/
    │   ├── ArmaController.java        ← CRUD persistido
    │   ├── SimulacionController.java  ← reglas de negocio
    │   ├── GlobalExceptionHandler.java ← handlers 404/400
    │   ├── IndexController.java
    │   ├── VanzfranhaitController.java
    │   └── ConstruccionController.java
    └── dto/
        └── ArmaRequest.java           ← DTO de entrada del POST
```

## Base de datos H2

La base es **en memoria** (`jdbc:h2:mem:cs2db`). Se crea al arrancar y se borra al detener. La consola está habilitada en:

- URL: **http://localhost:8080/h2-console**
- JDBC URL: `jdbc:h2:mem:cs2db`
- Usuario: `sa`
- Contraseña: (vacía)

Las tablas (`arma`, `arma_larga`, `francotirador`, `granada`) se crean automáticamente con `ddl-auto=create-drop`.

## Contacto

- **Autor:** Matías Jara
- **Email:** matiazjara@gmail.com
- **GitHub:** [@Vanzfranhait](https://github.com/Vanzfranhait)
