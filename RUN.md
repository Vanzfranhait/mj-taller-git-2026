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
├── mj_taller_git_2026/       ← clase Application del starter
├── cs2/                      ← modelado Counter-Strike 2
└── minecraft/                ← (opcional) modelado Minecraft
```
