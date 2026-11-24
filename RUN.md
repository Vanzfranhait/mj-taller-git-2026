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

## POO-09: correr los tres servicios

El escenario de POO-09 consulta a dos compañeros (A2 y A3). Para probarlo hacen falta los tres servicios arriba. A1 arranca igual sin ellos (el escenario usa fallback), pero para ver `origenA2: HTTP` y `origenA3: HTTP` los tres tienen que estar vivos.

### Repos y commits

| Rol | Repo | Commit | Puerto |
|---|---|---|---|
| A1 (este repo) | `mj-taller-git-2026` | `c2ac475` | 8080 |
| A2 (Marcelo) | `MK-taller-git-2026` | `97bdc6b` | 8082 |
| A3 (Joaquín) | `jb-taller-git-2026` | `8a907b7` | 8083 |

### Arranque (tres terminales)

**Terminal 1 — A1 (este repo, puerto 8080):**

```bash
git clone https://github.com/Vanzfranhait/mj-taller-git-2026.git
cd mj-taller-git-2026
git checkout c2ac475dfcc0db479f93a3579318c399cded2490
./mvnw spring-boot:run
```

**Terminal 2 — A2 (puerto 8082):**

```bash
git clone https://github.com/Pomona06/MK-taller-git-2026.git
cd MK-taller-git-2026
git checkout 97bdc6b9f984f0c22b6a2d880640c7b38edf7ff7
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8082
```

**Terminal 3 — A3 (puerto 8083):**

```bash
git clone https://github.com/joaquinBenegas0/jb-taller-git-2026.git
cd jb-taller-git-2026
git checkout 8a907b750df891c28b42cec8f0eefd01eb44519b
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8083
```

**Orden recomendado:** A2 y A3 primero, A1 después. Si A2/A3 no están, A1 igual funciona con fallback.

### Verificación rápida

```bash
curl http://localhost:8082/v3/api-docs   # A2 publica su OpenAPI
curl http://localhost:8083/v3/api-docs   # A3 publica su OpenAPI
curl http://localhost:8080/v3/api-docs   # A1 publica su OpenAPI
```

### Probar el escenario con curl

**Feliz (A2 y A3 aceptan):**

```bash
curl -X POST http://localhost:8080/api/escenario/disparo \
  -H "Content-Type: application/json" \
  -d '{"modelo":"AK-47","carga":1,"distancia":10,"vida":100,"blindaje":100,"tirador":"Sombra","equipoTirador":"TERRORISTAS","objetivo":"Halcon","equipoObjetivo":"ANTITERRORISTAS","danio":180}'
```

Esperado: `201 Created` con `"origenA2":"HTTP"` y `"origenA3":"HTTP"`.

**Rechazo de A2 (munición insuficiente):**

```bash
curl -X POST http://localhost:8080/api/escenario/disparo \
  -H "Content-Type: application/json" \
  -d '{"modelo":"AK-47","carga":0,"distancia":10,"vida":100,"blindaje":100,"tirador":"Sombra","equipoTirador":"TERRORISTAS","objetivo":"Halcon","equipoObjetivo":"ANTITERRORISTAS","danio":180}'
```

Esperado: `400` con `"A2 rechazó: ..."`.

**Rechazo de A3 (fuego amigo):**

```bash
curl -X POST http://localhost:8080/api/escenario/disparo \
  -H "Content-Type: application/json" \
  -d '{"modelo":"AK-47","carga":1,"distancia":10,"vida":100,"blindaje":100,"tirador":"Sombra","equipoTirador":"TERRORISTAS","objetivo":"Halcon","equipoObjetivo":"TERRORISTAS","danio":180}'
```

Esperado: `400` con `"A3 rechazó: ..."`.

**Verificar persistencia (después del feliz):**

```bash
curl http://localhost:8080/api/escenario/jugadas
```

Después de los dos rechazos, la lista debe seguir teniendo **solo la jugada del feliz**. Los rechazos no persisten nada.

### Fallback (A2/A3 apagados)

Si A2 y A3 no están, el escenario funciona igual: las validaciones se resuelven con un fallback local que replica la semántica de cada regla. La jugada se persiste con `"origenA2":"FALLBACK"` y `"origenA3":"FALLBACK"`, para que se sepa que no hubo HTTP real. El fallback **no reemplaza al cliente HTTP**: es contingencia para que la prueba no dependa de que los compañeros estén en línea.

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
```

---
