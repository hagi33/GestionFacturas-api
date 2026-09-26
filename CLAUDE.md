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
- Rate limiting: Bucket4j 8.x, in-memory — implemented
- OCR: Tesseract via Tess4J (Spanish), selected by the `ocr` Spring profile; mock OCR is
  the default (dev and tests)
- File storage: local filesystem (MinIO planned)
- springdoc-openapi 3.x (compatible with Spring Boot 4; do NOT downgrade to 2.x)
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
beans in `infrastructure/config/DomainBeansConfig`. New pure-domain services that need
injection go there too.

## Package structure

Root package: `com.fabio.GestionFacturas`

```
domain
├── gasto      -> Gasto (optional nullable clienteId), EstadoGasto, GastoInvalidoException,
│                 FacturaTextParser, DatosFacturaExtraidos
├── ingreso    -> Ingreso, EstadoCobro (PENDIENTE/COBRADA), IngresoInvalidoException,
│                 CobroInvalidoException, IngresoNoEncontradoException
├── cliente    -> Cliente, ClienteInvalidoException, ClienteDuplicadoException,
│                 ClienteNoEncontradoException
├── categoria  -> Categoria
├── usuario    -> Usuario, RefreshToken, EmailYaRegistradoException,
│                 CredencialesInvalidadException  (note: existing typo "Invalidad")
└── shared     -> Dinero (value object)

application  -> per concept (gasto, ingreso, cliente, categoria, usuario):
                port/in, port/out, service. shared/port/out: OcrPort, FileStoragePort,
                ExportPort. usuario/port/out: UsuarioRepositoryPort, TokenGeneradorPort,
                RefreshTokenRepositoryPort, RefreshTokenGeneradorPort.

infrastructure
├── adapter/in/web          -> gasto, ingreso, cliente, categoria, usuario (AuthController),
│                              health, GlobalExceptionHandler
├── adapter/out/persistence -> gasto, ingreso, cliente, categoria, usuario (+ refresh_token):
│                              JpaEntity + JpaRepository + Mapper + PersistenceAdapter each
├── adapter/out/ocr         -> MockOcrAdapter (default), TesseractOcrAdapter (@Profile("ocr"))
├── adapter/out/storage     -> LocalStorageAdapter (FileStoragePort)
└── config                  -> DomainBeansConfig, SecurityConfig,
                                config/ratelimit (RateLimitFilter),
                                config/security (JwtTokenProvider, JwtAuthenticationFilter,
                                OpenApiConfig, RefreshTokenGenerador)
```

## Domain modules

- **gasto**: expenses (money out). Manual or OCR digitization. State, deducible, file
  reference. Optional nullable clienteId (many gastos are general). When a clienteId is
  provided (create/review), validate it exists, is the user's, and is ACTIVE; if null, skip.
- **ingreso**: income (money in) — invoices issued to a client. clienteId (required, must
  exist/be the user's/active), concepto, fechaEmision, base/iva/total (Dinero), estadoCobro
  (PENDIENTE/COBRADA), fechaCobro. Payment is marked MANUALLY (no bank integration). Domain
  transitions (rules in the DOMAIN, not the service): registrarCobro(fecha) — only if
  PENDIENTE, fecha >= fechaEmision, sets COBRADA; revertirCobro() — only if COBRADA, back to
  PENDIENTE. Two SEPARATE entities Gasto/Ingreso (not a shared Movimiento).
- **cliente**: the user's clients. nombre, nif, email, telefono. NIF unique per user. SOFT
  DELETE via `activo` (desactivar() sets false; never removes the row). buscarPorUsuario
  returns only active. Another user's client -> not-found (404).
- **usuario**: identity + JWT auth.
- **categoria**: expense categories (read-only catalog).

## Security (implemented)

- BCrypt for passwords. JwtTokenProvider implements TokenGeneradorPort. The JWT principal
  is the user id as a STRING (the token subject); JwtAuthenticationFilter stores it in the
  SecurityContext. Controllers use @AuthenticationPrincipal Long usuarioId.
- Refresh tokens: revocable, stored HASHED (SHA-256 — lookupable; NOT BCrypt). Logout marks
  revocado=true (no delete). No rotation yet (deferred).
- SecurityConfig: stateless, CSRF disabled. Public: /api/auth/**, /api/health,
  springdoc/swagger. Else authenticated. OpenApiConfig declares the Bearer JWT scheme.

### Rate limiting (Bucket4j, in-memory)
- `RateLimitFilter` (config/ratelimit), a OncePerRequestFilter. Registered in SecurityConfig
  with addFilterAfter(rateLimitFilter, JwtAuthenticationFilter.class) so the user is already
  in the SecurityContext when it runs. A FilterRegistrationBean with setEnabled(false)
  disables its servlet-chain auto-registration (otherwise it wouldn't run in the security
  chain). Single @Component instance injected into SecurityConfig.
- Buckets live in instance-field ConcurrentHashMaps (one per bucket type), reused via
  computeIfAbsent with a STABLE key — this persistence between requests is essential; local
  maps were the bug that let everything through.
- Limits: login /api/auth/login 10/min per IP; register /api/auth/register 5/hour per IP;
  all other authenticated endpoints 50/min per user (key = usuarioId). Over limit -> 429
  with Retry-After. Requests without an authenticated user pass through (Security handles 401).
- KNOWN LIMITATION (pending): the bucket maps grow unbounded (no eviction of idle buckets) —
  fine for now, but for production use a cache with expiry or Redis.

## OCR / digitization (Phase 1)
- OcrPort (application/shared): byte[] -> String plain text ONLY. Must not parse fields.
- MockOcrAdapter default; TesseractOcrAdapter @Profile("ocr"), datapath/language from config.
  Images only; PDF pending.
- FacturaTextParser (domain, wired in DomainBeansConfig): text -> DatosFacturaExtraidos.
  Missing fields -> null. Built with TDD.
- DigitalizarFacturaService: store -> OCR -> parse -> create Gasto in BORRADOR with file ref.

## Error handling (GlobalExceptionHandler, @RestControllerAdvice)
- GastoInvalidoException / ClienteInvalidoException / IngresoInvalidoException /
  IllegalArgumentException -> 400
- MethodArgumentNotValidException -> 400 with field errors
- EmailYaRegistradoException / ClienteDuplicadoException -> 409
- CobroInvalidoException -> 409
- CredencialesInvalidadException -> 401
- ClienteNoEncontradoException / IngresoNoEncontradoException -> 404
  Domain throws domain exceptions; the handler maps to HTTP. Domain never knows HTTP.

## Code conventions
- No ternary operators; explicit ifs. Constructor injection only (never @Autowired fields).
- DTOs and commands are records (commands nested in their use-case interface).
- Money is BigDecimal, wrapped in Dinero in the domain.
- Enums persisted as STRING. Business rules live in the domain; services orchestrate.
- Access control by usuarioId everywhere; another user's resource is not-found, not revealed.
- Spanish identifiers/domain terms.

## Database
- Schema owned by Flyway (ddl-auto: validate). Migrations V{n}__desc.sql: V1 init,
  V2 seed usuario, V3 refresh_token, V4 gasto referencia_archivo, V5 cliente, V6 ingreso,
  V7 gasto cliente_id.
- Do NOT edit applied migrations; add a new V{n}. snake_case columns, mapped explicitly.
- Config (DB creds, JWT secret, tesseract datapath) via .env (EnvFile plugin in IntelliJ).
  Never hardcode secrets or commit the .env.

## Testing
- Unit tests with JUnit 5 + Mockito (mock the ports) for domain and services. Broad coverage.
- Prefer TDD for new business rules (login, invoice parser, cliente rules, ingreso cobro
  transitions were built test-first). Use verify(..., never()) for "did NOT happen". Owner
  writes the TDD tests for genuinely new rules; Claude Code implements against them and does
  the mechanical parts. Business rules are tested in the domain (pure POJO); service tests
  cover orchestration/access-control, not the rules again.
- Tests use the mock OCR, never native Tesseract. Testcontainers planned.

## Pending security work (deferred, mostly for deployment)
- Refresh token rotation (reuse detection).
- CORS configuration (when the KMP client connects).
- HTTP security headers (HSTS, X-Content-Type-Options, CSP) — when deployed behind HTTPS.
- Distributed rate limiting with Redis — when running multiple instances.
- Real client IP via X-Forwarded-For — only behind a trusted proxy.
- Request/upload size limits (for invoice uploads).
- Eviction of idle rate-limit buckets (currently unbounded in memory).
- Secrets in a secrets manager (production), not just .env.

## Environment gotchas (learned the hard way)
- Run with Java 21 (project target), not a newer JDK.
- If DevTools causes erratic startups after big changes, run `./mvnw clean compile` in the
  terminal, then Rebuild Project.
- Each run configuration needs the EnvFile enabled, or JWT/DB/OCR config arrives empty.
- A @Bean Filter auto-registers in the servlet chain; to run it only in the Security chain,
  disable that with a FilterRegistrationBean(setEnabled(false)).
- Shared state in a filter (rate-limit buckets) must be in instance fields, not locals.

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