# ms-moderacion — Sistema de Foros en Línea

Microservicio para la **gestión de publicaciones y comentarios por parte de un moderador o administrador**: banear y desbanear temas o comentarios que incumplan las normas del foro, con historial de cada acción.
Desarrollo Full Stack III (DSY2205) — Experiencia 2.

Se generó con **[microservicio-arquetipo](../microservicio_arquetipo)** y trabaja sobre la misma base de datos Oracle Cloud que [ms-usuarios](https://github.com/brychavez123/microservicio_usuario) y [ms-foro](https://github.com/brychavez123/microservicio_foro).

## Tecnologías
Java 21 · Spring Boot 3.5 · Spring Data JPA · Bean Validation · Spring Security (HTTP Basic + BCrypt) · Oracle Cloud con Wallet · Lombok · Actuator · Docker

## Patrones de diseño
| Patrón | Dónde | Para qué |
|---|---|---|
| **Strategy** | `strategy/AccionModeracionStrategy`, `BanearStrategy`, `DesbanearStrategy` | Cada acción encapsula sus reglas (validar y aplicar). Agregar una acción nueva no obliga a cambiar el servicio. |
| **Factory** | `strategy/AccionModeracionFactory` | Entrega la estrategia correcta según la acción (`BANEAR` / `DESBANEAR`). |
| **Builder** | `model/AccionModeracion` (`@Builder` de Lombok) | Construye cada registro del historial de forma legible. |
| Interfaz común | `model/Moderable` | `Tema` y `Comentario` se moderan con el mismo código. |

```
ModeracionController → ModeracionService → AccionModeracionFactory.obtener(accion)
                                              └─ BanearStrategy / DesbanearStrategy  (validar + aplicar)
                                         → AccionModeracion.builder()...build()  (historial)
```

## Arquitectura
```
src/main/java/com/duoc/moderacion
├── controller/   ModeracionController, InfoController
├── service/      ModeracionService
├── strategy/     AccionModeracionStrategy, BanearStrategy, DesbanearStrategy, AccionModeracionFactory
├── repository/   Tema-, Comentario-, AccionModeracion-, UsuarioRepository
├── model/        Tema, Comentario, AccionModeracion, Moderable, enums (+ Usuario, Rol, Categoria de solo lectura)
├── dto/          MotivoRequest, ContenidoModeradoResponse, AccionModeracionResponse, ResumenModeracionResponse
├── exception/    GlobalExceptionHandler
└── security/     SecurityConfig, UsuarioDetailsService, UsuarioActual
```

## Base de datos
Orden de ejecución en Oracle Cloud:
1. `db/01_usuarios.sql` (ms-usuarios)
2. `db/02_foro.sql` (ms-foro)
3. [`db/03_moderacion.sql`](db/03_moderacion.sql): crea `MODERACION_ACCION` y registra los baneos de ejemplo.

## Roles
| Rol | Acceso |
|---|---|
| USUARIO | Sin acceso (403) |
| **MODERADOR** | Ver contenido, banear y desbanear, historial y resumen |
| **ADMIN** | Lo anterior + eliminar registros del historial |

## Endpoints (Basic Auth, usuario = email)
| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/info` | Público: estado del servicio |
| GET | `/api/moderacion/resumen` | Indicadores (activos, baneados, acciones de 7 días) |
| GET | `/api/moderacion/temas?estado=BANEADO` | Temas (filtro opcional `ACTIVO` / `BANEADO`) |
| GET | `/api/moderacion/temas/{id}` | Un tema |
| PUT | `/api/moderacion/temas/{id}/banear` | Banear tema. Body: `{ "motivo": "..." }` |
| PUT | `/api/moderacion/temas/{id}/desbanear` | Desbanear tema (motivo opcional) |
| GET | `/api/moderacion/comentarios?estado=` | Comentarios |
| GET | `/api/moderacion/comentarios/{id}` | Un comentario |
| PUT | `/api/moderacion/comentarios/{id}/banear` | Banear comentario |
| PUT | `/api/moderacion/comentarios/{id}/desbanear` | Desbanear comentario |
| GET | `/api/moderacion/historial?tipo=TEMA&contenidoId=5` | Historial (filtros opcionales) |
| GET | `/api/moderacion/historial/{id}` | Un registro |
| GET | `/api/moderacion/historial/moderador/{id}` | Acciones de un moderador |
| DELETE | `/api/moderacion/historial/{id}` | Eliminar registro (solo ADMIN) |

Códigos de respuesta: `200`, `400` (validación), `401`, `403`, `404`, `409` (por ejemplo, banear algo que ya está baneado).

### Prueba rápida con Postman
1. **Authorization → Basic Auth**: `moderador@foro.cl` / `Modera123!`
2. `PUT http://localhost:8083/api/moderacion/temas/1/banear` con el body `{ "motivo": "Spam o publicidad" }`
3. `GET http://localhost:8083/api/moderacion/historial` muestra la acción registrada.
4. En ms-foro, `GET http://localhost:8082/api/temas` ya no muestra ese tema.

En AWS todo pasa por Nginx en el puerto 80: `http://IP/api/moderacion/...`

## Ejecutar
```bash
# Local (con db.properties y el Wallet en la raíz)
./mvnw spring-boot:run          # http://localhost:8083

# Docker
docker build -t ms-moderacion .
docker run -p 8083:8083 --env-file db.properties -e DB_WALLET_PATH=/app/wallet \
  -v ./Wallet_NOMBRE:/app/wallet:ro ms-moderacion
```
> Importante: la ruta del Wallet **no puede tener espacios** (el driver de Oracle la corta). Usa una ruta relativa como `./Wallet_NOMBRE`.

## Despliegue en AWS
`.github/workflows/deploy-aws.yml` corre los tests, publica la imagen en **Amazon ECR** y la despliega en la **EC2** por Systems Manager en cada push a `main`. Conserva el contenedor anterior hasta que el nuevo responde (rollback) y termina con una prueba desde internet a través de Nginx. El nombre, el puerto y las rutas de prueba están en `aws/servicio.env`.

Paso a paso completo (CloudFormation, Secrets Manager, S3 y GitHub): [MANUAL-DESPLIEGUE.md](https://github.com/brychavez123/frontend_foro/blob/main/MANUAL-DESPLIEGUE.md).
