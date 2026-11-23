mj-taller-git-2026

Autor: Matías Jara  
Usuario GitHub: Vanzfranhait  
Comisión: CYT646 F  
Asignatura: Lenguaje de Programación 3 (LP3)  
Dominio: Counter-Strike 2  
Commit de la solución POO-09: c2ac475dfcc0db479f93a3579318c399cded2490

---

Cómo levantar la API

bash
./mvnw spring-boot:run

La API arranca en: [http://localhost:8080](http://localhost:8080)  
Para detenerla: Ctrl + C

Requisitos

-   Java 21
-   Maven (incluido en el wrapper ./mvnw)

Documentación

-   RUN.md — Instrucciones detalladas de ejecución
-   BITACORA.md — Bitácora de asistencia de IA (marca, modelo de LLM, prompts)

---

POO-08 — Corrección

Estado: observaciones cerradas. No se vuelve a aplicar la rúbrica de 24.

Criterio 1: se habilitó /h2-console y se sacó data/cs2db.mv.db de Git (git rm --cached, data/ al .gitignore).

Criterio 2: se creó ArmaService (el controller ya no usa el repositorio), se sacaron las excepciones del dominio (ahora las lanzan los services) y se agregó @Transactional a SimulacionService.

Criterio 3: se creó el enum Equipo (T, CT, AMBOS), se bloqueó la granada ya lanzada, se agregó ArmaRequest para el JSON de entrada y las validaciones quedaron en el service.

Criterio 6: se agregó POST /api/db/simular/aguantar/{id} (sin él la Regla 2 no se podía cumplir desde HTTP), se probó el flujo aguantar → recarga rápida y se corrigió la colección Insomnia (parentId → WORKSPACEID, variables awpid/heid, endpoints faltantes).

Criterio 8: README con las respuestas de "Qué hay que pensar", application.properties documentado, Mermaid con controllers/services/handler y sin <<abstract>> en JuegoException, enlace roto a docs/TALLERGIT.md eliminado, RUN.md con el árbol real de paquetes, BITACORA.md con la entrada POO-08 y paso 8.6 del PDF corregido (el AWP con alcance 100 no pierde munición; el SSG 08 con alcance 30 baja de 10 a 5).

---

POO-09 — Challenge de APIs cruzadas

Mapa A1 / A2 / A3 (Round robin)

| Rol | Nombre | Repo | Commit | Puerto | OpenAPI | Reglas |
| --- | --- | --- | --- | --- | --- | --- |
| A1 (yo) | Matías Jara (Vanzfranhait) | [https://github.com/Vanzfranhait/mj-taller-git-2026](https://github.com/Vanzfranhait/mj-taller-git-2026) | c2ac475dfcc0db479f93a3579318c399cded2490 | 8080 | [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs) | explosión de granada; recarga rápida de francotirador |
| A2 | Marcelo Kropff (Pomona06) | [https://github.com/Pomona06/MK-taller-git-2026](https://github.com/Pomona06/MK-taller-git-2026) | 97bdc6b9f984f0c22b6a2d880640c7b38edf7ff7 | 8082 | [http://localhost:8082/v3/api-docs](http://localhost:8082/v3/api-docs) | munición (POST /api/reglas/disparo); vida (POST /api/reglas/impacto) |
| A3 | Joaquín Benegas (joaquinBenegas0) | [https://github.com/joaquinBenegas0/jb-taller-git-2026](https://github.com/joaquinBenegas0/jb-taller-git-2026) | 8a907b750df891c28b42cec8f0eefd01eb44519b | 8083 | [http://localhost:8083/v3/api-docs](http://localhost:8083/v3/api-docs) | munición por modo (POST /api/reglas/municion); daño con blindaje (POST /api/reglas/danio) |

OpenAPI propio como archivo: [docs/openapi-a1.json](docs/openapi-a1.json)

Reglas citadas

A1 (mías, POO-08):

1.  Explosión de granada: cuando una granada explota, las armas con alcance menor al radio de la explosión pierden la mitad de su munición. Se persiste en H2.
2.  Recarga rápida de francotirador: un francotirador con estabilidad > 0.7 puede disparar rápido (consume 2 de munición) pero queda inestable (estabilidad = 0.5).

A2 (Marcelo):

1.  Munición: un arma de fuego solo dispara si el cargador tiene las balas del disparo (1, o 3 en ráfaga) y solo recarga si le queda un cargador de reserva; una granada se lanza una sola vez. POST /api/reglas/disparo, POST /api/reglas/recarga.
2.  Vida: el daño baja con la distancia, el chaleco desvía una parte según el arma, la vida queda entre 0 y 100 y un jugador eliminado no recibe más daño. POST /api/reglas/impacto.

A3 (Joaquín):

1.  Munición por modo: un disparo gasta las balas de su modo (SEMI 1, RAFAGA 3, AUTOMATICO 5) sin pasarse de las que quedan; con cargador vacío no se dispara. POST /api/reglas/municion.
2.  Daño con blindaje: solo se le dispara a un enemigo vivo (otro equipo, vida > 0); el blindaje absorbe hasta la mitad del daño y el resto baja la vida (nunca por debajo de 0). POST /api/reglas/danio.

Consulta pura propia (para que A2/A3 me consulten sin tocar mi base):

-   POST /api/reglas/disparo-rapido con {"estabilidad":1.0,"municionActual":5} → 200 {"permitido":true,"municionDespues":3,"estabilidadDespues":0.5}. Rechaza con 400 si estabilidad ≤ 0.7 o munición < 2. No modifica H2.

Escenario: "Disparo coordinado"

En una frase: un disparo de AK-47 se considera legal solo si A2 (Marcelo) confirma que hay munición suficiente y A3 (Joaquín) confirma que el objetivo es enemigo, está vivo y el daño es absorbible por el blindaje; si ambas validaciones pasan, la jugada se persiste en el H2 de A1.

Endpoint: POST /api/escenario/disparo

Request:

json
{
  "modelo": "AK-47",
  "carga": 1,
  "distancia": 10,
  "vida": 100,
  "blindaje": 100,
  "tirador": "Sombra",
  "equipoTirador": "TERRORISTAS",
  "objetivo": "Halcon",
  "equipoObjetivo": "ANTITERRORISTAS",
  "danio": 180
}

Flujo:

1.  A2 (Marcelo): POST /api/reglas/disparo con {modelo, carga}. Si rechaza → 400 "A2 rechazó: ...", la base no cambia.
2.  A3 (Joaquín): POST /api/reglas/danio con {tirador, equipoTirador, objetivo, equipoObjetivo, vidaObjetivo, blindajeObjetivo, danio}. Si rechaza → 400 "A3 rechazó: ...", la base no cambia.
3.  Si ambas aceptan → se persiste la Jugada en H2 y se devuelve 201 Created.

Verificación posterior: GET /api/escenario/jugadas lista las jugadas persistidas.

Modo autónomo (fallback): si A2 o A3 no responden, se usa un fallback local que replica la semántica de sus reglas. El resultado se marca con origenA2: FALLBACK / origenA3: FALLBACK para que se sepa que no hubo HTTP real. El fallback no reemplaza al cliente HTTP: es contingencia.

Configuración de URLs de A2 y A3

En src/main/resources/application.properties:

properties
api.a2.base-url=http://localhost:8082
api.a2.habilitado=true
api.a2.timeout-ms=3000

api.a3.base-url=http://localhost:8083
api.a3.habilitado=true
api.a3.timeout-ms=3000

Se puede cambiar el puerto sin tocar código. Si habilitado=false, se fuerza el fallback.

Manejo de errores y atribución

-   A2 rechaza → 400 {"error":"regladejuego","mensaje":"A2 rechazó: ..."}. La base no cambia.
-   A3 rechaza → 400 {"error":"regladejuego","mensaje":"A3 rechazó: ..."}. La base no cambia.
-   A2/A3 no responden → fallback local, se marca origenA2/origenA3: FALLBACK en la jugada persistida.
-   Orden de llamadas: A2 primero (munición, más barata) y A3 después (daño, solo si la munición pasó). Si A2 rechaza, no se llama a A3.

Cómo arrancar los tres servicios

Requisitos: Java 21 en los tres.

Terminal 1 — A1 (Matías, 8080):

bash
git clone https://github.com/Vanzfranhait/mj-taller-git-2026.git
cd mj-taller-git-2026
git checkout c2ac475dfcc0db479f93a3579318c399cded2490
./mvnw spring-boot:run

Terminal 2 — A2 (Marcelo, 8082):

bash
git clone https://github.com/Pomona06/MK-taller-git-2026.git
cd MK-taller-git-2026
git checkout 97bdc6b9f984f0c22b6a2d880640c7b38edf7ff7
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8082

Terminal 3 — A3 (Joaquín, 8083):

bash
git clone https://github.com/joaquinBenegas0/jb-taller-git-2026.git
cd jb-taller-git-2026
git checkout 8a907b750df891c28b42cec8f0eefd01eb44519b
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8083

Orden recomendado: primero A2 y A3, después A1. Si A2/A3 no están, A1 igual funciona con fallback.

Respuestas de ejemplo

1. Escenario feliz con A2 y A3 vivos (HTTP real)

POST /api/escenario/disparo
{"modelo":"AK-47","carga":1,"distancia":10,"vida":100,"blindaje":100,"tirador":"Sombra","equipoTirador":"TERRORISTAS","objetivo":"Halcon","equipoObjetivo":"ANTITERRORISTAS","danio":180}

→ 201 Created
{"id":1,"modelo":"AK-47","carga":1,"distancia":10,"vidaAntes":100,"vidaDespues":10,"blindajeAntes":100,"blindajeDespues":10,"origenA2":"HTTP","origenA3":"HTTP","timestamp":"..."}

2. Escenario feliz en modo autónomo (A2/A3 apagados → fallback)

→ 201 Created
{..., "origenA2":"FALLBACK","origenA3":"FALLBACK", ...}

3. Rechazo de A2 (munición insuficiente)

POST /api/escenario/disparo
{"modelo":"AK-47","carga":0,...}

→ 400 Bad Request
{"error":"regladejuego","mensaje":"A2 rechazó: A2-fallback: AK-47 no puede disparar: el cargador tiene 0 balas y hacen falta 1"}

4. Rechazo de A3 (fuego amigo)

POST /api/escenario/disparo
{..., "equipoTirador":"TERRORISTAS","equipoObjetivo":"TERRORISTAS",...}

→ 400 Bad Request
{"error":"regladejuego","mensaje":"A3 rechazó: A3-fallback: fuego amigo (TERRORISTAS)"}

5. GET /api/escenario/jugadas después de los 4 casos  
Solo aparece la jugada exitosa. Los rechazos no persistieron nada.

Colección Insomnia

En insomnia/coleccion-poo-09.json. Incluye:

-   Entorno con variables a1, a2, a3.
-   Request al OpenAPI propio.
-   Escenario feliz.
-   Rechazo por A2.
-   Rechazo por A3.
-   Modo autónomo (fallback).

Para importar: Insomnia → Import → From File → seleccionar el JSON.

---

Endpoints REST

Una vez arrancado el servicio en [http://localhost:8080](http://localhost:8080):

| Método | Ruta | Qué hace |
| --- | --- | --- |
| GET | / | Confirma que el servicio está vivo. |
| GET | /api/armas | Lista todas las armas del inventario con su descripción (JSON). |
| GET | /api/armas/{nombre} | Devuelve la descripción de un arma específica. |
| POST | /api/armas/{nombre}/disparar | Dispara el arma y devuelve el daño infligido. |
| POST | /api/armas/{nombre}/recargar | Recarga el arma al máximo. |
| POST | /api/granadas/{nombre}/lanzar | Lanza una granada (solo si es una granada). |
| POST | /api/granadas/{nombre}/explotar | Explota una granada ya lanzada. |
| POST | /api/francotiradores/{nombre}/zoom/{nivel} | Cambia el zoom de un francotirador. |
| GET | /api/construir/arma | Construye un arma desde parámetros de URL y devuelve JSON. |
| POST | /api/db/armas | Crea un arma (Francotirador o Granada) y la guarda en H2. |
| GET | /api/db/armas | Lista todas las armas persistidas. |
| GET | /api/db/armas/{id} | Busca un arma por ID (404 si no existe). |
| PATCH | /api/db/armas/{id}/recargar | Recarga un arma (usa mensaje del dominio). |
| DELETE | /api/db/armas/{id} | Borra un arma. |
| GET | /api/db/armas/recargables | Lista armas usando la interfaz Recargable. |
| POST | /api/db/simular/explosion/{id} | Simula explosión de granada (Regla 1). |
| POST | /api/db/simular/recarga/{id} | Simula recarga rápida de francotirador (Regla 2). |
| POST | /api/db/simular/aguantar/{id} | Sube la estabilidad del francotirador a 1.0 (auxiliar para probar la Regla 2). |
| POST | /api/reglas/disparo-rapido | Consulta pura de la Regla 2 (no toca H2). |
| POST | /api/escenario/disparo | Escenario POO-09: disparo coordinado A2 + A3. |
| GET | /api/escenario/jugadas | Lista las jugadas persistidas por el escenario. |

Ejemplos (curl)

bash
Confirmar que el servicio está vivo
curl http://localhost:8080/

Listar todas las armas
curl http://localhost:8080/api/armas

Ver un arma específica
curl "http://localhost:8080/api/armas/AWP"

Disparar
curl -X POST http://localhost:8080/api/armas/USP-S/disparar

Lanzar y explotar una granada
curl -X POST http://localhost:8080/api/granadas/HE/lanzar
curl -X POST http://localhost:8080/api/granadas/HE/explotar

Cambiar zoom de un francotirador
curl -X POST http://localhost:8080/api/francotiradores/AWP/zoom/5

Construir un arma desde la URL
curl "http://localhost:8080/api/construir/arma?nombre=Glock-18&dano=30&precio=200&municion=20&precision=0.8&equipo=T&cadencia=400&municionTipo=9mm"

Consulta pura de la regla de disparo rápido (POO-09)
curl -X POST http://localhost:8080/api/reglas/disparo-rapido \
  -H "Content-Type: application/json" \
  -d '{"estabilidad":1.0,"municionActual":5}'

Escenario POO-09 (con fallback si A2/A3 no están)
curl -X POST http://localhost:8080/api/escenario/disparo \
  -H "Content-Type: application/json" \
  -d '{"modelo":"AK-47","carga":1,"distancia":10,"vida":100,"blindaje":100,"tirador":"Sombra","equipoTirador":"TERRORISTAS","objetivo":"Halcon","equipoObjetivo":"ANTITERRORISTAS","danio":180}'

Ver jugadas persistidas
curl http://localhost:8080/api/escenario/jugadas

---

Modelado POO

El modelado de Counter-Strike 2 se encuentra en el paquete:

src/main/java/py/edu/uc/lp3/domain/

Los servicios REST están en:

src/main/java/py/edu/uc/lp3/rest/controller/

Diagrama de clases (modelado CS2)

mermaid
classDiagram
    class Arma {
        <<abstract>>
        -Long id
        -String nombre
        -int daño
        -int precio
        -int municionMax
        -int municionActual
        -float precision
        -Equipo equipo
        +disparar() int
        +disparar(int) int
        +recargar() void
        +puedeDisparar() boolean
        +lanzar() void
        +explotar() int
        +usarZoom(int) void
        +setMunicionActualParaSimulacion(int) void
        +getId() Long
        +describir() String
        +getTipo() String
    }

    class Recargable {
        <<interface>>
        +recargar() void
        +getMunicionActual() int
        +getMunicionMax() int
    }

    class Equipo {
        <<enumeration>>
        T
        CT
        AMBOS
    }

    class ArmaCorta {
        -int cadencia
        -long cooldownMs
        -String tipoMunicion
        -boolean puedeRafaga
        +dispararRapido() int
        +apuntarPreciso() void
    }

    class Granada {
        -String tipo
        -int radioExplosion
        -float tiempoActivacion
        -boolean lanzada
        +lanzar() void
        +explotar() int
    }

    class ArmaLarga {
        <<abstract>>
        -int alcance
        -long cooldownMs
        -boolean modoRafaga
        +cambiarModo() void
        +dispararRafaga() int
    }

    class Francotirador {
        -int zoom
        -float estabilidad
        +usarZoom(int) void
        +aguantarRespiracion() void
        +respirar() void
    }

    class RifleAsalto {
        -int retroceso
        -int retrocesoBase
        -float precisionRafaga
        +controlarRetroceso() void
        +descansar() void
    }

    class Subfusil {
        -float movilidad
        -float movilidadBase
        -float dispersionMovimiento
        +dispararCorriendo() int
        +aumentarMovilidad() void
    }

    class Escopeta {
        -int numeroBalas
        -float dispersionBalas
        -int alcanceEfectivo
        +dispararRafagaCorta() int
        +calcularDañoTotal() int
    }

    class ArmaRepository {
        <<interface>>
        +save(Arma) Arma
        +findAll() ListArma
        +findById(Long) OptionalArma
        +deleteById(Long) void
        +existsById(Long) boolean
    }

    class ArmaService {
        -ArmaRepository repository
        +crearArma(Arma) Arma
        +crearDesdeRequest(ArmaRequest) Arma
        +listarArmas() ListArma
        +buscarPorId(Long) Arma
        +recargarArma(Long) Arma
        +borrarArma(Long) void
        +listarRecargables() ListRecargable
    }

    class SimulacionService {
        -ArmaRepository repository
        +simularExplosion(Long) ListArma
        +simularRecargaRapida(Long) Francotirador
        +aguantarRespiracion(Long) Francotirador
    }

    class EscenarioService {
        -A2Client a2Client
        -A3Client a3Client
        -JugadaRepository jugadaRepository
        +ejecutarDisparoCoordinado(EscenarioDisparoRequest) Jugada
        +listarJugadas() ListJugada
    }

    class ArmaController {
        -ArmaService armaService
        +crearArma(ArmaRequest) ResponseEntity
        +listarArmas() ListArma
        +buscarPorId(Long) ResponseEntity
        +recargarArma(Long) ResponseEntity
        +borrarArma(Long) ResponseEntity
        +listarRecargables() ListRecargable
    }

    class SimulacionController {
        -SimulacionService simulacionService
        +explosion(Long) ResponseEntity
        +recargaRapida(Long) ResponseEntity
        +aguantarRespiracion(Long) ResponseEntity
    }

    class EscenarioController {
        -EscenarioService escenarioService
        +disparoCoordinado(EscenarioDisparoRequest) ResponseEntity
        +listarJugadas() ListJugada
    }

    class ReglasController {
        +disparoRapido(DisparoRapidoRequest) ResponseEntity
    }

    class ArmaRequest {
        -String tipo
        -String nombre
        -Integer daño
        -Integer precio
        -Integer municionMax
        -Float precision
        -Equipo equipo
        -Integer alcance
        -Integer cadencia
        -Integer zoom
        -String tipoGranada
        -Integer radioExplosion
        -Float tiempoActivacion
    }

    class JuegoException {
    }

    class ArmaNoEncontradaException {
    }

    class ReglaDeJuegoException {
    }

    class GlobalExceptionHandler {
        +handleArmaNoEncontrada(ArmaNoEncontradaException) ResponseEntity
        +handleReglaDeJuego(ReglaDeJuegoException) ResponseEntity
    }

    Recargable <|.. Arma
    Arma <|-- ArmaCorta
    Arma <|-- Granada
    Arma <|-- ArmaLarga
    Arma --> Equipo

    ArmaLarga <|-- Francotirador
    ArmaLarga <|-- RifleAsalto
    ArmaLarga <|-- Subfusil
    ArmaLarga <|-- Escopeta

    JuegoException <|-- ArmaNoEncontradaException
    JuegoException <|-- ReglaDeJuegoException

    ArmaController --> ArmaService
    SimulacionController --> SimulacionService
    EscenarioController --> EscenarioService
    ArmaService --> ArmaRepository
    SimulacionService --> ArmaRepository
    EscenarioService --> A2Client
    EscenarioService --> A3Client
    EscenarioService --> JugadaRepository
    ArmaService ..> ArmaRequest
    ArmaService ..> Arma
    SimulacionService ..> Arma
    GlobalExceptionHandler --> ArmaNoEncontradaException
    GlobalExceptionHandler --> ReglaDeJuegoException

---

Sobrecarga y sobreescritura

Sobrecarga (overloading)

La sobrecarga ocurre cuando varios métodos tienen el mismo nombre pero distinta lista de argumentos (distinta firma). Se resuelve en tiempo de compilación.

Sobrecarga de constructores

En Arma (clase base):

java
// Constructor completo
protected Arma(String nombre, int daño, int precio, int municionMax,
               float precision, Equipo equipo)

// Constructor simplificado (sobrecarga)
protected Arma(String nombre, int daño)

El constructor simplificado llama al completo con this(...) y valores por defecto.

En ArmaCorta (clase hija):

java
// Constructor completo
public ArmaCorta(String nombre, int daño, int precio, int municionMax,
                 float precision, Equipo equipo,
                 int cadencia, String tipoMunicion, boolean puedeRafaga)

// Constructor simplificado (sobrecarga)
public ArmaCorta(String nombre, int daño)

Ambos constructores dejan el objeto en un estado válido. Ninguno rompe los invariantes.

Sobrecarga de un mensaje del dominio

En Arma:

java
// Dispara 1 tiro
public int disparar()

// Dispara N tiros (sobrecarga)
public int disparar(int cantidad)

Mismo nombre (disparar), distinta firma. El segundo reutiliza al primero.

---

Sobreescritura (overriding)

La sobreescritura ocurre cuando una clase hija redefine un método del padre con la misma firma. Se resuelve en tiempo de ejecución (polimorfismo dinámico).

| Método | Declarado en | Sobreescrito por |
| --- | --- | --- |
| describir() | Arma (abstracto) | ArmaCorta, Granada, Francotirador, RifleAsalto, Subfusil, Escopeta |
| getTipo() | Arma (abstracto) | Las 8 hijas |
| disparar() | Arma | Escopeta (daño según perdigones), Francotirador (falla si no está estabilizado) |
| dispararRafaga() | ArmaLarga | RifleAsalto (aumenta el retroceso) |
| lanzar() | Arma (por defecto vacío) | Granada |
| explotar() | Arma (por defecto 0) | Granada |
| usarZoom(int) | Arma (por defecto vacío) | Francotirador |

Ejemplo en código:

java
// En Arma (padre)
public abstract String describir();

// En Francotirador (hija) — sobreescribe
@Override
public String describir() {
    return String.format("%s [%s] - Daño: %d | Zoom: x%d",
        getNombre(), getTipo(), getDaño(), zoom);
}

Diferencia clave:

-   Sobrecarga: mismo nombre, distinta firma (parámetros). Se resuelve en compilación.
-   Sobreescritura: mismo nombre, misma firma, en clase hija. Se resuelve en ejecución.

---

Qué cambió respecto al modelado de septiembre

En este ejercicio se agregaron los dos mecanismos sobre el modelado original:

Sobrecarga agregada:

-   En Arma: constructor simplificado Arma(String, int) y método disparar(int cantidad).
-   En ArmaCorta: constructor simplificado ArmaCorta(String, int).

Sobreescritura agregada o explicitada:

-   @Override en describir() y getTipo() de las 8 clases hijas (implementación de los abstractos de Arma).
-   @Override en disparar() de Escopeta (daño por perdigones) y de Francotirador (falla si no está estabilizado).
-   @Override en dispararRafaga() de RifleAsalto (aumenta el retroceso).
-   @Override en lanzar() y explotar() de Granada, y en usarZoom(int) de Francotirador, sobre métodos nuevos que la clase base Arma declara por defecto.

Ese último grupo es clave para el diseño: permitió eliminar los instanceof del VanzfranhaitController. El controller le pide a cualquier Arma que lanzar(), explotar() o usarZoom(), y es el propio objeto quien decide si puede o no. Si no puede, la clase base no hace nada o el service lanza la excepción.

Por qué importa el diseño

El controller VanzfranhaitController trata todas las armas como tipo padre Arma:

java
for (Map.Entry<String, Arma> entrada : inventario.entrySet()) {
    respuesta.put(entrada.getKey(), entrada.getValue().describir());
}

No hay ningún instanceof en el código del controller. Cada objeto responde su propio describir(), su propio disparar(), su propio lanzar(). Eso es polimorfismo por sobreescritura.

---

POO-08 — Persistencia, reglas y excepciones

Base de datos H2

El proyecto usa H2 en memoria (jdbc:h2:mem:cs2db). Las tablas se crean automáticamente al arrancar (spring.jpa.hibernate.ddl-auto=create-drop). Al detener el servicio, la base se borra (es solo para desarrollo).

Propiedades de H2 (application.properties)

-   spring.datasource.url=jdbc:h2:mem:cs2db;DBCLOSEDELAY=-1;DBCLOSEONEXIT=FALSE — URL de la base en memoria. DBCLOSEDELAY=-1 mantiene la base viva mientras la JVM esté corriendo; DBCLOSEONEXIT=FALSE evita que H2 la cierre al salir.
-   spring.datasource.driver-class-name=org.h2.Driver — driver JDBC de H2.
-   spring.datasource.username=sa — usuario por defecto de H2.
-   spring.datasource.password= — contraseña vacía (solo desarrollo).
-   spring.jpa.hibernate.ddl-auto=create-drop — crea las tablas al arrancar y las borra al detener.
-   spring.jpa.show-sql=true — imprime en consola el SQL que genera Hibernate.
-   spring.jpa.properties.hibernate.formatsql=true — formatea ese SQL para leerlo.
-   spring.jpa.open-in-view=false — evita la advertencia de Spring Boot 4 y cierra la sesión JPA al terminar cada request.
-   spring.h2.console.enabled=true — habilita la consola web de H2.
-   spring.h2.console.path=/h2-console — ruta de la consola.
-   server.error.include-message=always — incluye el mensaje de error en el JSON de error por defecto.

Las dos reglas de negocio

1.  Explosión de Granada: cuando una granada explota, todas las armas con alcance menor al radio de la explosión pierden la mitad de su munición. El cambio se persiste en H2.
2.  Recarga Rápida de Francotirador: un francotirador con estabilidad > 0.7 puede disparar rápido (consume 2 de munición), pero queda inestable (estabilidad 0.5) después.

Jerarquía de excepciones

-   JuegoException (base, extiende RuntimeException)
    -   ArmaNoEncontradaException → 404 Not Found
    -   ReglaDeJuegoException → 400 Bad Request

El GlobalExceptionHandler atrapa cada tipo y devuelve un JSON con "error" y "mensaje".

Interfaz de dominio

Recargable es implementada por todas las armas (vía Arma). El endpoint GET /api/db/armas/recargables usa el tipo de la interfaz.

---

Qué hay que pensar (respuestas)

Estas respuestas explican las decisiones de diseño del proyecto. El código es coherente con ellas.

Recurso y verbo

El sustantivo de la API es arma (la familia persistida: Arma → Francotirador / Granada). La URL nombra el recurso (/api/db/armas) y el verbo HTTP dice la operación: POST crea, GET lee, PATCH actualiza un subconjunto, DELETE borra. La simulación es un recurso aparte (/api/db/simular/...) porque no es un CRUD sobre armas: es una acción del juego que cambia el estado de varias armas a la vez.

Identidad

La clave es id, un Long que asigna la base de datos con @Id @GeneratedValue(strategy = GenerationType.IDENTITY). El JSON de creación no lleva id: lo pone H2 al insertar. El cliente lo recibe en la respuesta 201 Created y lo usa en las rutas siguientes (/api/db/armas/{id}).

Herencia y tablas

Usamos @Inheritance(strategy = InheritanceType.JOINED). En CS2 un francotirador y una granada comparten muy pocos atributos (nombre, daño, precio, equipo) y difieren en casi todo lo demás (zoom/estabilidad vs. radio/tiempo de activación). JOINED crea una tabla por clase (arma, armalarga, francotirador, granada) y une por id. Lo elegimos sobre SINGLETABLE porque evita columnas nulas para atributos que solo aplican a un subtipo, y sobre TABLEPERCLASS porque permite consultar Arma como tipo padre sin unir todas las tablas hijas a mano.

El subtipo se distingue en el JSON con @JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "tipo") y @JsonSubTypes en Arma: al crear, el campo "tipo" dice si es "francotirador" o "granada"; al listar, Jackson agrega ese campo a cada objeto para que el cliente sepa qué recibió.

Interfaz y repositorio

ArmaRepository extends JpaRepository<Arma, Long> es una interfaz: nosotros declaramos los mensajes (findAll, findById, save, existsById, deleteById, y las consultas derivadas que agregamos) y Spring Data genera la implementación en tiempo de arranque. El controller no depende de una implementación concreta sino del tipo interfaz, igual que en POO-07 el controller dependía de Recargable. Eso permite cambiar H2 por otra base sin tocar el controller ni el service.

Protocolo del dominio

Recargable declara recargar(), getMunicionActual() y getMunicionMax(). La implementa Arma, y por herencia todas sus subclases (ArmaCorta, ArmaLarga y sus hijas, Granada, Francotirador, etc.). El endpoint GET /api/db/armas/recargables devuelve List<Recargable>: el controller habla con el tipo de la interfaz, no con Arma, y podría recibir en el futuro cualquier clase de otra familia que implemente Recargable.

Ocultamiento

Las reglas numéricas del juego las sostiene ArmaService (validaciones de nombre, daño, precio, precisión y equipo antes de persistir) y SimulacionService (rango de munición en la explosión, estabilidad y munición en la recarga rápida). El dominio (Arma, Francotirador, Granada) solo guarda estado y ofrece mensajes (disparar, recargar, lanzar, explotar, usarZoom); no lanza excepciones de negocio. Los campos son private, no hay setters públicos, y el constructor vacío de JPA es protected.

Prueba de Insomnia: POST /api/db/armas con "daño": -5, "precio": -1, "municionMax": 999, "precision": 5 responde 400 con {"error":"regladejuego", ...} y la colección no crece (el GET /api/db/armas siguiente no lo muestra).

PUT y PATCH

Usamos PATCH en /api/db/armas/{id}/recargar porque no reemplazamos el recurso entero: cambiamos un subconjunto de campos (la munición vuelve al máximo, el resto queda igual). Un PUT habría exigido enviar el JSON completo del arma. Un campo omitido en PATCH conserva su valor; un campo omitido en PUT se reinicia al valor por defecto. Para el CRUD de creación usamos POST; no exponemos PUT porque no hay caso de uso que lo requiera en este dominio.

DELETE

Borrar en nuestro modelo significa quitar la fila (repository.deleteById(id)). Si el id no existe, ArmaService.borrarArma lanza ArmaNoEncontradaException y el handler responde 404. No marcamos como "borrado lógico" porque el enunciado no lo pide y el modelo no tiene estado "inactivo". El DELETE habla con el service, que a su vez habla con el repositorio; el controller no toca la base directamente.

Dos reglas

1.  Explosión de Granada (SimulacionService.simularExplosion): cuando una granada explota, todas las armas con alcance menor al radio de la explosión pierden la mitad de su munición. Participan Granada (la que explota), ArmaLarga/Francotirador (las que están en el radio) y Arma (el padre común). El resultado se persiste (repository.save) y un GET /api/db/armas posterior muestra la munición reducida.
2.  Recarga Rápida de Francotirador (SimulacionService.simularRecargaRapida): un francotirador con estabilidad > 0.7 puede hacer un disparo rápido que consume 2 de munición y lo deja inestable (estabilidad = 0.5). Participan Francotirador y Arma. Para poder probarla desde HTTP agregamos POST /api/db/simular/aguantar/{id}, que sube la estabilidad a 1.0 antes de la recarga rápida.

El recurso HTTP de la simulación es /api/db/simular/... (no /api/db/armas/...) porque la simulación no es un CRUD: es una acción que cambia el estado de varias armas. Un GET posterior confirma que el cambio persistió.

Excepciones

JuegoException extends RuntimeException es la base no comprobada. Elegimos no comprobada porque las reglas del juego no son errores recuperables en el flujo normal: si el cliente pide explotar algo que no es granada, la respuesta correcta es 400 y el flujo termina. Obligar a throws en cada método del service llenaría el código de try/catch que no aportan. Sus dos hijas son:

-   ArmaNoEncontradaException → handler 404 Not Found con {"error":"notfound","mensaje":"..."}.
-   ReglaDeJuegoException → handler 400 Bad Request con {"error":"regladejuego","mensaje":"..."}.

Un recurso inexistente (404) y una regla incumplida (400) son tipos distintos porque el cliente debe reaccionar distinto: un 404 sugiere que el id está mal o el recurso fue borrado; un 400 sugiere que el pedido está mal formado según las reglas del juego. El JSON de error lleva siempre "error" (el tipo) y "mensaje" (el detalle), para que el cliente distinga a las hermanas aunque ambas sean RuntimeException.

---

Colección Insomnia

En insomnia/coleccion-poo-08.json y insomnia/coleccion-poo-09.json. Para usarlas:

1.  Abrir Insomnia.
2.  Menú de colección → Import → seleccionar el archivo.
3.  Ejecutar en orden: crear AWP, crear HE, listar, simular, probar errores.
4.  En Insomnia 13, si el body JSON no se envía, agregar manualmente el header Content-Type: application/json.

La colección de POO-08 incluye:

-   POST para crear AWP.
-   POST para crear HE.
-   GET de la colección.
-   GET por ID.
-   PATCH para recargar.
-   DELETE para borrar.
-   POST para simular explosión, recarga rápida y aguantar respiración.
-   GET de error 404.
-   POST de error 400 (explosión de no-granada).
-   POST de error 400 (recarga sin estabilidad).

La colección de POO-09 incluye:

-   Entorno con variables a1, a2, a3.
-   Request al OpenAPI propio.
-   Escenario feliz.
-   Rechazo por A2.
-   Rechazo por A3.
-   Modo autónomo (fallback).

---

Cómo probar desde terminal (sin Insomnia)

bash
Crear AWP
curl -X POST http://localhost:8080/api/db/armas \
  -H "Content-Type: application/json" \
  -d '{"tipo":"francotirador","nombre":"AWP","daño":115,"precio":4750,"municionMax":10,"precision":1.0,"equipo":"AMBOS","alcance":100,"cadencia":40,"zoom":10}'

Crear HE
curl -X POST http://localhost:8080/api/db/armas \
  -H "Content-Type: application/json" \
  -d '{"tipo":"granada","nombre":"HE","daño":98,"precio":300,"equipo":"AMBOS","tipoGranada":"frag","radioExplosion":50,"tiempoActivacion":1.6}'

Crear SSG 08 (alcance 30, dentro del radio de la HE)
curl -X POST http://localhost:8080/api/db/armas \
  -H "Content-Type: application/json" \
  -d '{"tipo":"francotirador","nombre":"SSG 08","daño":88,"precio":1700,"municionMax":10,"precision":0.9,"equipo":"AMBOS","alcance":30,"cadencia":40,"zoom":5}'

Listar
curl http://localhost:8080/api/db/armas

Simular explosión (asumiendo que HE es id=2)
curl -X POST http://localhost:8080/api/db/simular/explosion/2

Verificar (el SSG 08 baja de 10 a 5; el AWP con alcance 100 queda con 10)
curl http://localhost:8080/api/db/armas

Aguantar respiración y recarga rápida
curl -X POST http://localhost:8080/api/db/simular/aguantar/1
curl -X POST http://localhost:8080/api/db/simular/recarga/1

Probar errores
curl -i http://localhost:8080/api/db/armas/999
curl -i -X POST http://localhost:8080/api/db/simular/explosion/1

---

Estructura del proyecto (nuevos archivos de POO-09)
src/main/java/py/edu/uc/lp3/  
├── client/  
│ ├── A2Client.java  
│ ├── A3Client.java  
│ ├── DanioRequest.java  
│ ├── DisparoRequest.java  
│ ├── FallbackMunicionVida.java  
│ ├── ImpactoRequest.java  
│ ├── MunicionRequest.java  
│ ├── OrigenResultado.java  
│ ├── ReglaClient.java  
│ ├── ReglaFallback.java  
│ └── ResultadoRegla.java  
├── config/OpenApiConfig.java  
├── domain/Jugada.java  
├── repository/JugadaRepository.java  
├── service/EscenarioService.java  
├── rest/controller/EscenarioController.java  
├── rest/controller/ReglasController.java  
├── rest/dto/EscenarioDisparoRequest.java  
├── rest/dto/DisparoRapidoRequest.java  
└── rest/dto/DisparoRapidoResponse.java

---

Licencia

Este proyecto está bajo la licencia Apache 2.0.  
Ver el archivo [LICENSE](LICENSE) para el texto completo.
