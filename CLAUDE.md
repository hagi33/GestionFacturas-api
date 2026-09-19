# CLAUDE.md

Guidance for Claude Code when working in this repository. Read this before making changes.

## What this project is

A financial-control web/mobile app for freelancers and self-employed individuals. Tracks
expenses, income and clients, digitizes invoices with OCR, and keeps them organized for the
accountant. Guiding principle: **organization and visibility, never tax advice or official
invoicing**.

Backend in this repo. The client will be a separate Kotlin Multiplatform (KMP) app
(mobile + desktop, Compose Multiplatform) — planned, not started; the backend is a plain
REST API so the client is decoupled.

## Tech stack

- Java 21, Spring Boot 4.1.x
- PostgreSQL + Flyway
- Spring Security + JWT (access + refresh revocable) — implemented
- OCR: Tesseract via Tess4J (Spanish), selected by the `ocr` Spring profile; a mock OCR
  adapter is the default (dev and tests)
- File storage: local filesystem (MinIO planned)
- springdoc-openapi 3.x (the 3.x line is the one compatible with Spring Boot 4; do NOT
  downgrade to 2.x)
- Maven; JUnit 5 + Mockito + AssertJ

## Architecture — READ THIS CAREFULLY

Pure **hexagonal architecture (ports and adapters)**. Deliberate; must be preserved. Do
NOT collapse into a conventional layered CRUD structure.

```
domain          -> business core. Pure POJOs. NO Spring, NO JPA, NO framework imports.
application     -> use cases + ports (in/out interfaces). May use @Service/@Transactional.
infrastructure  -> adapters (web, persistence, ocr, storage, security) + config.
```

### Dependency rule (non-negotiable)
- infrastructure knows application, which knows domain. domain knows nothing.
- application DEFINES ports (interfaces). infrastructure IMPLEMENTS them.
- The application talks to ports, never to concrete adapters or framework types.

### Three separate models — intentional, do not "simplify"
DTO (web) / domain model / JPA entity, with mappers. Never expose JPA entities or domain
objects in controllers. Never put a framework type (e.g. MultipartFile) in an
application-layer port — convert to byte[] in the controller.

### Pure-domain beans
Domain classes with logic but no Spring annotations (e.g. FacturaTextParser) are wired as
beans in `infrastructure/config/DomainBeansConfig`. This keeps the domain free of framework
annotations while still injectable. New pure-domain services that need to be injected go
there too.

## Package structure

Root package: `com.fabio.GestionFacturas`

```
domain
├── gasto      -> Gasto, EstadoGasto, GastoInvalidoException, FacturaTextParser,
│                 DatosFacturaExtraidos
├── cliente    -> Cliente, ClienteInvalidoException, ClienteDuplicadoException,
│                 ClienteNoEncontradoException
├── categoria  -> Categoria
├── usuario    -> Usuario, RefreshToken, EmailYaRegistradoException,
│                 CredencialesInvalidadException  (note: existing typo "Invalidad")
├── ingreso    -> (Phase 2, in progress — not present yet)
└── shared     -> Dinero (value object)

application  -> per concept (gasto, cliente, categoria, usuario, ingreso[planned]):
                port/in, port/out, service.
                shared/port/out: OcrPort, FileStoragePort, ExportPort.
                usuario/port/out: UsuarioRepositoryPort, TokenGeneradorPort,
                RefreshTokenRepositoryPort, RefreshTokenGeneradorPort.

infrastructure
├── adapter/in/web          -> gasto, cliente, categoria, usuario (AuthController), health,
│                              GlobalExceptionHandler
├── adapter/out/persistence -> gasto, cliente, categoria, usuario (+ refresh_token):
│                              JpaEntity + JpaRepository + Mapper + PersistenceAdapter each
├── adapter/out/ocr         -> MockOcrAdapter (default), TesseractOcrAdapter (@Profile("ocr")),
│                              OcrException
├── adapter/out/storage     -> LocalStorageAdapter (FileStoragePort), FileStorageException
└── config                  -> DomainBeansConfig, SecurityConfig,
                                config/security (JwtTokenProvider, JwtAuthenticationFilter,
                                OpenApiConfig, RefreshTokenGenerador)
```

## Domain modules

- **gasto**: expenses. Manual or via OCR digitization. State, deducible, file reference.
- **cliente**: the user's clients. nombre, nif, email, telefono. NIF unique per user
  (unique constraint on usuario_id + nif). SOFT DELETE via an `activo` boolean: "delete"
  calls a domain `desactivar()` method (sets activo=false) and saves — it never removes the
  row, to keep integrity with associated invoices. buscarPorUsuario returns only active
  clients. Access control: desactivar/edit/get of another user's client is treated as
  not-found (ClienteNoEncontradoException -> 404), never revealing it exists.
- **categoria**: expense categories (read-only catalog for now).
- **usuario**: identity + JWT auth (register, login, refresh, logout).
- **ingreso**: Phase 2, in progress. Invoices issued to clients, with a payment state
  (cobrada/pendiente) and a registrarCobro operation. Two separate entities Gasto/Ingreso
  (NOT one shared "Movimiento") — they diverge (deducible vs estadoCobro); shared bits live
  in shared/OCR/storage, not by merging the entities.

## Security (implemented)

- BCrypt for passwords (domain Usuario holds passwordHash, never a raw password).
- JwtTokenProvider (config/security) implements the application port TokenGeneradorPort.
  User id = subject, email = claim. JwtAuthenticationFilter validates the Bearer token and
  stores the user id as the SecurityContext principal.
- Controllers get the user via @AuthenticationPrincipal Long usuarioId. Never hardcode.
- Refresh tokens: revocable, stored HASHED (SHA-256, so they can be looked up; NOT BCrypt)
  in refresh_token. RefreshTokenGenerador (config/security) implements
  RefreshTokenGeneradorPort. Logout (LogoutService) marks revocado=true (does not delete).
  Refresh (RefreshTokenService) validates and issues a new access token. No rotation yet
  (deferred — do not add unless asked).
- SecurityConfig: stateless, CSRF disabled, JWT filter registered. Public: /api/auth/**,
  /api/health, springdoc/swagger. Everything else authenticated. (The default Spring
  Security "generated security password" log line at startup is harmless noise; the custom
  SecurityConfig is the one in effect.)
- OpenApiConfig (config/security) declares the Bearer JWT scheme so Swagger UI shows the
  "Authorize" button.

## OCR / digitization (Phase 1)

- OcrPort (application/shared): byte[] -> String plain text ONLY. OCR must not parse fields.
- MockOcrAdapter: default (dev/tests). TesseractOcrAdapter: @Profile("ocr"), datapath and
  language from config (never hardcode paths). Images only; PDF is a pending follow-up.
- FacturaTextParser (domain): raw text -> DatosFacturaExtraidos (emisor, fecha, importes);
  framework-free, unit-tested, built with TDD. Missing fields -> null (user completes them
  on review). Wired as a bean in DomainBeansConfig.
- DigitalizarFacturaService: store file -> OCR -> parse -> create Gasto in BORRADOR with the
  file reference (referenciaArchivo on Gasto).

## Error handling (GlobalExceptionHandler, @RestControllerAdvice)
- GastoInvalidoException / ClienteInvalidoException / IllegalArgumentException -> 400
- MethodArgumentNotValidException -> 400 with field errors
- EmailYaRegistradoException / ClienteDuplicadoException -> 409
- CredencialesInvalidadException -> 401
- ClienteNoEncontradoException -> 404
  Domain throws domain exceptions; the handler maps to HTTP. Domain never knows HTTP.

## Code conventions
- No ternary operators; explicit ifs. Constructor injection only (never @Autowired fields).
- DTOs and commands are records (commands nested in their use-case interface).
- Money is BigDecimal, wrapped in Dinero in the domain.
- Enums persisted as STRING. Domain objects validate invariants and throw domain exceptions.
- Access control by usuarioId everywhere; another user's resource is not-found, not revealed.
- Spanish identifiers/domain terms.

## Database
- Schema owned by Flyway (ddl-auto: validate). Migrations V{n}__desc.sql. Current:
  V1 init, V2 seed usuario, V3 refresh_token, V4 gasto referencia_archivo, V5 cliente.
- Do NOT edit applied migrations; add a new V{n}. snake_case columns, mapped explicitly.
- Config (DB creds, JWT secret, tesseract datapath) via .env (EnvFile plugin in IntelliJ).
  Never hardcode secrets or commit the .env.

## Testing
- Unit tests with JUnit 5 + Mockito (mock the ports) for domain and services. Broad coverage
  exists (cliente, gasto, usuario/refresh/logout services; Dinero, Gasto, FacturaTextParser;
  LocalStorageAdapter; JwtTokenProvider).
- Prefer TDD for new business rules (login, invoice parser, and the cliente rules
  —duplicate NIF, soft delete— were built test-first). Use verify(..., never()) to assert
  a side effect did NOT happen. The owner writes the TDD tests for new business rules
  himself; Claude Code implements against them and does the mechanical parts.
- Tests always use the mock OCR, never native Tesseract. Testcontainers planned for
  persistence integration.

## Environment gotchas (learned the hard way)
- Run with Java 21 (project target), not a newer JDK, or behavior is inconsistent.
- If DevTools causes erratic startups after big changes, run `./mvnw clean compile` in the
  terminal to see the real state, then Rebuild Project.
- Each run configuration needs the EnvFile enabled, or JWT/DB/OCR config arrives empty.

## Working style
- Focused, minimal changes for the task. Do not refactor unrelated code.
- Run ./mvnw compile (and ./mvnw test when logic changed) before finishing.
- When implementing against existing tests: the tests are the contract — do NOT modify them
  to make them pass; adapt the code.
- Show a summary of changed files before considering the task done.
- If a request would violate the architecture rules, flag it instead of doing it.

## Git workflow
- Before a new feature/change, create a branch: git checkout -b feature/<desc>
  (prefixes: feature/, fix/, refactor/, test/, docs/). Never commit feature work to main.
- Focused commits. Do not merge to main or delete branches unless explicitly asked.