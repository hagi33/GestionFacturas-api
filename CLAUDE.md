# CLAUDE.md

Guidance for Claude Code when working in this repository. Read this before making changes.

## What this project is

A financial-control web/mobile app for freelancers and self-employed individuals.
It tracks **income and expenses**, links them to **clients**, and shows profit and
per-client profitability. Guiding principle: **organization and visibility, never
tax advice or official invoicing**. The app records and reports — it does not file
taxes or issue legal invoices.

Backend only in this repo (a Flutter client is planned separately).

## Tech stack

- Java 21
- Spring Boot 4.1.x
- PostgreSQL + Flyway (schema migrations)
- Spring Security + JWT (implemented)
- springdoc-openapi 3.x (the 3.x line is the one compatible with Spring Boot 4; do NOT
  downgrade to 2.x, which targets Spring Boot 3)
- Maven
- JUnit 5 + Mockito + AssertJ (tests); Testcontainers planned for integration tests

## Architecture — READ THIS CAREFULLY

This project uses **pure hexagonal architecture (ports and adapters)**. This is a
deliberate design choice and must be preserved. Do NOT collapse it into a
conventional layered CRUD structure, even if that would be shorter.

Three layers, with dependencies always pointing inward:

```
domain          -> business core. Pure POJOs. NO Spring, NO JPA, NO framework imports.
application     -> use cases + ports (in/out interfaces). May use @Service/@Transactional.
infrastructure  -> adapters (web, persistence, security) + config. All framework code lives here.
```

### The dependency rule (non-negotiable)

- `infrastructure` knows `application`, which knows `domain`.
- `domain` knows nothing. If you add `import org.springframework.*` or
  `import jakarta.persistence.*` to a domain class, that is a bug.
- `application` **defines** the ports (interfaces). `infrastructure` **implements** them.
- The application talks to ports, never to concrete adapters.

### Three separate models — this is intentional, do not "simplify"

For each entity there are three distinct classes, and they must stay separate:

- **DTO** (`infrastructure/adapter/in/web/.../dto`) — the public API contract (JSON).
- **Domain model** (`domain/...`) — business object with invariants and behavior.
- **JPA entity** (`infrastructure/adapter/out/persistence/...`) — flat mirror of the table.

Mappers translate between them (e.g. `GastoMapper`, `GastoWebMapper`). Do not make the
domain model a JPA entity. Do not expose JPA entities or domain objects directly in
controllers. This decoupling is the whole point of the architecture.

## Actual package structure

Root package: `com.fabio.GestionFacturas`

```
domain
├── gasto      -> Gasto, EstadoGasto, GastoInvalidoException
├── categoria  -> Categoria
├── usuario    -> Usuario, EmailYaRegistradoException, CredencialesInvalidadException
└── shared     -> Dinero (value object)

application
├── gasto      -> port/in (CrearGastoUseCase, ConsultarGastosUseCase),
│                 port/out (GastoRepositoryPort),
│                 service (CrearGastoService, ConsultarGastosService)
├── categoria  -> port/in (ConsultarCategoriaUseCase),
│                 port/out (CategoriaRepositoryPort),
│                 service (ConsultarCategoriaService)
├── usuario    -> port/in (RegistrarUsuarioUseCase, AutenticarUseCase),
│                 port/out (UsuarioRepositoryPort, TokenGeneradorPort),
│                 service (RegistrarUsuarioService, AutenticarService)
└── shared     -> port/out (OcrPort, FileStoragePort, ExportPort) [stubs, future phases]

infrastructure
├── adapter/in/web
│   ├── gasto      -> GastoController, GastoWebMapper, dto (CrearGastoRequest, GastoResponse)
│   ├── categoria  -> CategoriaController, CategoriaWebMapper, dto (CategoriaResponse)
│   ├── usuario    -> AuthController, dto (RegistrarRequest, LoginRequest,
│   │                 UsuarioResponse, LoginResponse)
│   ├── health     -> HealthController
│   └── GlobalExceptionHandler
├── adapter/out/persistence
│   ├── gasto      -> GastoJpaEntity, GastoJpaRepository, GastoMapper, GastoPersistenceAdapter
│   ├── categoria  -> (same 4-class pattern)
│   └── usuario    -> (same 4-class pattern)
└── config
    ├── SecurityConfig
    └── security   -> JwtTokenProvider, JwtAuthenticationFilter
```

Note: `ingreso` and `cliente` modules are planned (Phase 2), not yet present.
Known typo to fix eventually: `CredencialesInvalidadException` should be
`CredencialesInvalidasException` (extra "d"). Keep the current spelling in code until
it is renamed everywhere at once.

## Security (implemented)

Authentication is JWT-based and working. Key pieces and conventions:

- **Passwords**: hashed with BCrypt (`PasswordEncoder` bean in `SecurityConfig`). Never
  stored or logged in plain text. Domain `Usuario` holds a `passwordHash`, never a raw one.
- **Token generation**: `JwtTokenProvider` (`config/security`) generates and validates
  JWTs and implements the application port `TokenGeneradorPort`, so application services
  depend on the port, not on jjwt. The user's id is the token subject; the email is a claim.
- **Request authentication**: `JwtAuthenticationFilter` (extends `OncePerRequestFilter`)
  reads the `Authorization: Bearer <token>` header, validates the token, and stores the
  user's id (a Long) as the principal in Spring Security's SecurityContext.
- **Getting the current user in controllers**: use
  `@AuthenticationPrincipal Long usuarioId` as a handler parameter. Do NOT hardcode user
  ids — the old `usuarioId = 1L` scaffold has been removed.
- **SecurityConfig**: stateless sessions, CSRF disabled (stateless JWT API), the JWT
  filter registered before UsernamePasswordAuthenticationFilter. Public routes:
  `/api/auth/**`, `/api/health`, and springdoc/swagger routes (`/v3/api-docs/**`,
  `/swagger-ui/**`, `/swagger-ui.html`). Everything else requires authentication.
- **Auth endpoints**: `POST /api/auth/register`, `POST /api/auth/login` (both public).
- **Refresh tokens (revocable, DB-stored)**: planned — the "option C" approach, not yet
  implemented.

## Error handling (HTTP status mapping)

`GlobalExceptionHandler` (`@RestControllerAdvice`, in `adapter/in/web`) maps domain
exceptions to HTTP codes. Keep this convention; do not handle errors ad hoc in controllers:

- `GastoInvalidoException`, `IllegalArgumentException` -> 400 Bad Request
- `MethodArgumentNotValidException` (bean validation) -> 400 with field errors
- `EmailYaRegistradoException` -> 409 Conflict
- `CredencialesInvalidadException` -> 401 Unauthorized

Domain code throws domain exceptions; the handler (infrastructure) translates them to HTTP.
The domain never knows about HTTP.

## Code conventions

- **No ternary operators.** Use explicit `if` statements. (Owner preference.)
- **Constructor injection only.** Never `@Autowired` on fields.
- **DTOs and commands are `record`s.** Commands are nested records inside their use-case
  interface (e.g. `CrearGastoUseCase.ComandoCrearGasto`).
- **Money is always `BigDecimal`**, never `float`/`double`. In the domain it is
  wrapped in the `Dinero` value object (amount + currency).
- **Enums persisted as STRING** (`@Enumerated(EnumType.STRING)`), never ordinal.
- **Domain objects validate their own invariants** in the constructor and throw
  domain exceptions (e.g. `GastoInvalidoException`), not generic ones.
- **Access control in queries**: a user must only access their own data. Filter by
  `usuarioId` in reads, and on get-by-id return empty if the resource belongs to another
  user (see `ConsultarGastosService.obtenerPorId`).
- Language: code identifiers and domain terms are in Spanish (Gasto, Ingreso,
  Cliente, crearBorrador...). Keep that consistent.

## Database

- Schema is owned by **Flyway**, not Hibernate. `spring.jpa.hibernate.ddl-auto` is
  `validate` — Hibernate only checks entities match the schema, never creates or alters.
- Migrations in `src/main/resources/db/migration/` as `V{n}__description.sql`.
  Current: `V1__init.sql` (usuario, categoria, gasto), `V2__seed_usuario.sql`.
- While the schema is unstable and there is no real data, editing `V1` directly and
  recreating the DB (`docker compose down -v && docker compose up -d`) is acceptable.
  Once there is data to preserve, add a new `V{n}` migration instead.
- Column names are `snake_case`; map them explicitly in JPA entities.
- Config (DB credentials, JWT secret) comes from environment variables via a `.env`
  file (loaded with the EnvFile plugin in IntelliJ). Never hardcode secrets; never
  commit the `.env` or the real JWT secret. Config file is `application.yaml`.

## Testing

- Domain and services: unit tests with JUnit 5 + Mockito (mock the ports). Fast and
  high-value — prioritize them. Existing tests: DineroTest, GastoTest,
  CrearGastoServiceTest, ConsultarGastosServiceTest, RegistrarUsuarioServiceTest,
  AutenticarServiceTest, JwtTokenProviderTest.
- For new business logic, prefer TDD: write the failing test first, then the minimum
  code to make it pass (the login service was built this way).
- Use `verify(..., never())` to assert a side effect did NOT happen (e.g. no save on a
  duplicate-email registration).
- Persistence/integration: Testcontainers with a real PostgreSQL — add once the
  persistence layer is stable, not before.

## Current status

- **Phase 0 (fundamentals)**: closed and tested. Domain, use cases and persistence for
  gasto/categoria/usuario, controllers for gasto/categoria/health, GlobalExceptionHandler,
  Flyway, Docker (PostgreSQL), unit tests for domain and services.
- **Security (JWT)**: implemented — register, login (TDD), JwtTokenProvider (+ tests),
  JwtAuthenticationFilter, hardened SecurityConfig, controllers use
  `@AuthenticationPrincipal`. Refresh tokens (revocable, DB-stored) still pending.
- **Next**: refresh tokens; then Phase 1 (OCR + file storage), Phase 2 (ingresos +
  clientes), Phase 3 (dashboard), Phase 4 (export + polish).

## Working style

- Make focused, minimal changes for the task at hand. Do not refactor unrelated code.
- Before finishing, run `./mvnw compile` (and `./mvnw test` when logic changed) to
  verify nothing is broken.
- Show a summary of changed files before considering the task done.
- If a request would violate the architecture rules above, flag it instead of
  silently doing it.

## Git workflow

- Before starting work on a new feature or a distinct change, create and switch to a
  new branch: `git checkout -b <type>/<short-description>` (e.g. `feature/refresh-tokens`,
  `fix/credenciales-typo`). Never commit feature work directly to `main`.
- Use conventional branch prefixes: `feature/`, `fix/`, `refactor/`, `test/`, `docs/`.
- Make focused commits with clear messages. Do not mix unrelated changes in one branch.
- Do not merge to main or delete branches unless explicitly asked — leave that to the owner.