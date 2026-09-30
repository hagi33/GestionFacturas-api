# CLAUDE.md

Guidance for Claude Code when working in this repository. Read this before making changes.

## What this project is

A financial-control web/mobile app for freelancers and self-employed individuals. Tracks
expenses, income and clients, digitizes invoices with OCR, and gives a dashboard view of
profit and per-client profitability. Guiding principle: **organization and visibility,
never tax advice or official invoicing**.

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
Domain classes with logic but no Spring annotations (e.g. FacturaTextParser,
CalculadoraResumenPeriodo, CalculadoraRentabilidadClientes) are wired as beans in
`infrastructure/config/DomainBeansConfig`. New pure-domain services needing injection go
there too.

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
├── dashboard  -> ResumenPeriodo, CalculadoraResumenPeriodo, RentabilidadCliente,
│                 CalculadoraRentabilidadClientes, PendientesCobro (+ its calculator/helper)
├── categoria  -> Categoria
├── usuario    -> Usuario, RefreshToken, EmailYaRegistradoException,
│                 CredencialesInvalidadException  (note: existing typo "Invalidad")
└── shared     -> Dinero (value object)

application  -> per concept (gasto, ingreso, cliente, categoria, usuario, dashboard):
                port/in, port/out, service. shared/port/out: OcrPort, FileStoragePort,
                ExportPort. usuario/port/out: UsuarioRepositoryPort, TokenGeneradorPort,
                RefreshTokenRepositoryPort, RefreshTokenGeneradorPort.
                dashboard/port/out: DashboardConsultaPort (buscarGastosPorPeriodo,
                buscarIngresosPorPeriodo, buscarIngresosPendientesDeCobro).

infrastructure
├── adapter/in/web          -> gasto, ingreso, cliente, categoria, usuario (AuthController),
│                              dashboard, health, GlobalExceptionHandler
├── adapter/out/persistence -> gasto, ingreso, cliente, categoria, usuario (+ refresh_token),
│                              dashboard (DashboardPersistenceAdapter — read-only queries,
│                              reuses/extends GastoJpaRepository and IngresoJpaRepository)
├── adapter/out/ocr         -> MockOcrAdapter (default), TesseractOcrAdapter (@Profile("ocr"))
├── adapter/out/storage     -> LocalStorageAdapter (FileStoragePort)
└── config                  -> DomainBeansConfig, SecurityConfig,
                                config/ratelimit (RateLimitFilter),
                                config/security (JwtTokenProvider, JwtAuthenticationFilter,
                                OpenApiConfig, RefreshTokenGenerador)
```

## Domain modules

- **gasto**: expenses (money out). Manual or OCR digitization. Optional nullable clienteId.
- **ingreso**: income (money in) — invoices issued to a client. clienteId is MANDATORY
  (never null). estadoCobro (PENDIENTE/COBRADA), fechaCobro. Payment marked MANUALLY.
  Transitions registrarCobro/revertirCobro live in the domain with their rules.
- **cliente**: the user's clients. Soft delete via `activo`.
- **usuario**: identity + JWT auth.
- **categoria**: expense categories (read-only catalog).
- **dashboard**: read-only aggregation views over gasto/ingreso/cliente — no own table.
  Three views:
    - **Resumen de periodo** (`CalculadoraResumenPeriodo`): given period-filtered gastos and
      ingresos, computes facturadoTotal/Base (ALL ingresos), cobradoTotal/Base (COBRADA only),
      gastosTotal/Base, and four beneficio figures (caja = cobrado - gastos; facturado =
      facturado - gastos; each in total and base). Pure domain, TDD-built, unit-tested with
      hand-computed sums. This is the single source of truth for these formulas — other
      dashboard calculators reuse it rather than re-implementing the arithmetic.
    - **Rentabilidad por cliente** (`CalculadoraRentabilidadClientes`): groups gastos/ingresos
      by clienteId and delegates each group's figures to CalculadoraResumenPeriodo. Gastos
      with clienteId null are grouped into ONE trailing "sin cliente" entry (only present if
      such gastos exist) — ingresos are never in "sin cliente" since their clienteId is
      mandatory.
    - **Pendientes de cobro**: lists the user's ingresos with estadoCobro PENDIENTE plus an
      aggregate total.

### KNOWN ISSUE — investigate before relying on rentabilidad-por-cliente in multi-client scenarios
Observed in manual testing: with 2 active clients where client A has ingresos and client B
also has ingresos (no gastos for either), GET /api/dashboard/rentabilidad-clientes returned
only client A's entry — client B was missing from the response, even though its ingresos'
figures were confirmed present in the database within the queried date range (not yet fully
confirmed whether the dates were in range — that check was left unfinished). The domain
calculator (CalculadoraRentabilidadClientes) was inspected and looks correct (grouping logic
handles multiple clienteIds properly); RentabilidadClientesService was also inspected and is
a thin, correct pass-through. The suspected location is DashboardPersistenceAdapter's
buscarIngresosPorPeriodo implementation (or the underlying JPA query) possibly not returning
all clients' ingresos — NOT yet confirmed. Do not assume this is fixed; verify with a
controlled multi-client dataset (check exact fechaEmision values against the query range
first) before treating rentabilidad-por-cliente as reliable for more than one client with
data. Do not silently "fix" this without diagnosing the actual root cause and reporting it.

## Security (implemented)

- BCrypt for passwords. JWT principal is the user id as a STRING (token subject);
  JwtAuthenticationFilter stores it in the SecurityContext. Controllers use
  @AuthenticationPrincipal Long usuarioId.
- Refresh tokens: revocable, HASHED with SHA-256 (lookupable; NOT BCrypt). Logout marks
  revocado=true. No rotation yet (deferred).
- SecurityConfig: stateless, CSRF disabled. Public: /api/auth/**, /api/health,
  springdoc/swagger. Else authenticated.

### Rate limiting (Bucket4j, in-memory)
- `RateLimitFilter` (config/ratelimit), registered via addFilterAfter(rateLimitFilter,
  JwtAuthenticationFilter.class); a FilterRegistrationBean with setEnabled(false) disables
  its servlet-chain auto-registration. Single @Component instance.
- Bucket maps MUST be instance fields (not locals) — this was a real bug that let every
  request through.
- Limits: login 10/min/IP; register 5/hour/IP; other authenticated endpoints 50/min/user.
  Over limit -> 429 with Retry-After.

## OCR / digitization (Phase 1)

- OcrPort: byte[] -> String plain text ONLY, no field parsing.
- MockOcrAdapter default; TesseractOcrAdapter @Profile("ocr"). Images only; PDF pending.
- FacturaTextParser (domain): text -> DatosFacturaExtraidos. Built with TDD.
- DigitalizarFacturaService: store -> OCR -> parse -> create Gasto in BORRADOR.

## Error handling (GlobalExceptionHandler, @RestControllerAdvice)
- GastoInvalidoException / ClienteInvalidoException / IngresoInvalidoException /
  IllegalArgumentException -> 400
- MethodArgumentNotValidException -> 400 with field errors
- EmailYaRegistradoException / ClienteDuplicadoException -> 409
- CobroInvalidoException -> 409
- CredencialesInvalidadException -> 401
- ClienteNoEncontradoException / IngresoNoEncontradoException -> 404

## Code conventions
- No ternary operators; explicit ifs. Constructor injection only.
- DTOs and commands are records (commands nested in their use-case interface).
- Money is BigDecimal, wrapped in Dinero. Use isEqualByComparingTo in tests (never
  isEqualTo — BigDecimal scale differs, e.g. "100.0" vs "100.00").
- Enums persisted as STRING. Business rules live in the domain; services orchestrate only.
- Access control by usuarioId everywhere; another user's resource is not-found.
- Spanish identifiers/domain terms.

## Database
- Schema owned by Flyway (ddl-auto: validate). Migrations: V1 init, V2 seed usuario,
  V3 refresh_token, V4 gasto referencia_archivo, V5 cliente, V6 ingreso, V7 gasto cliente_id.
- Do NOT edit applied migrations; add a new V{n}.
- Config via .env (EnvFile plugin in IntelliJ). Never hardcode secrets.

## Testing
- Unit tests with JUnit 5 + Mockito for domain and services. Broad coverage.
- Prefer TDD for new business rules (login, invoice parser, cliente rules, ingreso cobro
  transitions, and the dashboard calculators were built test-first). Domain calculators
  are tested with hand-computed sums using isEqualByComparingTo.
- IMPORTANT LESSON: passing unit tests do not guarantee correct end-to-end behavior —
  verify against real data in the database (SQL query for ground truth, then compare field
  by field with the API response) before trusting a "looks right" result, especially for
  aggregation/grouping logic. Do not assume a discrepancy is a data artifact without
  checking; equally, do not assume it's a code bug without checking the data first.
- Tests use the mock OCR, never native Tesseract. Testcontainers planned.

## Pending / known issues
- Refresh token rotation, CORS, HTTP security headers, Redis-backed rate limiting,
  X-Forwarded-For (behind trusted proxy only), upload size limits, rate-limit bucket
  eviction, secrets manager for production — all deferred, mostly for deployment time.
- Digitalización de ingresos (OCR for ingresos, not just gastos) not implemented.
- PDF support in OCR, emisor extraction, async OCR, MinIO — Phase 1 follow-ups.

## Environment gotchas (learned the hard way)
- Run with Java 21 (project target), not a newer JDK.
- If DevTools causes erratic startups after big changes, run `./mvnw clean compile`, then
  Rebuild Project.
- Each run configuration needs the EnvFile enabled.
- A @Bean Filter auto-registers in the servlet chain; disable with
  FilterRegistrationBean(setEnabled(false)) to run it only via Security's addFilterAfter.
- Shared state in a filter must be in instance fields, not locals.

## Working style
- Focused, minimal changes for the task. Do not refactor unrelated code.
- Run ./mvnw compile (and ./mvnw test when logic changed) before finishing.
- Tests are the contract — do NOT modify them to make them pass; adapt the code.
- When asked to verify/debug behavior: report findings with evidence (actual data, actual
  responses, exact discrepancies) — do not silently patch code during a verification task,
  and do not claim something "works" without comparing against real data.
- Show a summary of changed files before considering the task done.
- If a request would violate the architecture rules, flag it instead of doing it.

## Git workflow
- Before a new feature/change, create a branch: git checkout -b feature/<desc>
  (prefixes: feature/, fix/, refactor/, test/, docs/). Never commit feature work to main.
- Focused commits. Do not merge to main or delete branches unless explicitly asked.