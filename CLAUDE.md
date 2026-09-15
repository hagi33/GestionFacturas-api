# CLAUDE.md

Guidance for Claude Code when working in this repository. Read this before making changes.

## What this project is

A financial-control web/mobile app for freelancers and self-employed individuals.
It tracks expenses (income and clients planned), digitizes invoices with OCR, and keeps
them organized to hand to an accountant. Guiding principle: **organization and visibility,
never tax advice or official invoicing**.

Backend only in this repo (a Flutter client is planned separately).

## Tech stack

- Java 21
- Spring Boot 4.1.x
- PostgreSQL + Flyway
- Spring Security + JWT (access + refresh revocable) — implemented
- OCR: Tesseract via Tess4J (Spanish language), selected by the `ocr` Spring profile;
  a mock OCR adapter is the default (used in development and tests)
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

### The dependency rule (non-negotiable)

- `infrastructure` knows `application`, which knows `domain`. `domain` knows nothing.
- `application` DEFINES the ports (interfaces). `infrastructure` IMPLEMENTS them.
- The application talks to ports, never to concrete adapters or framework types.

### Three separate models — intentional, do not "simplify"

DTO (web contract) / domain model (business) / JPA entity (persistence), with mappers
between them. Never expose JPA entities or domain objects directly in controllers. Never
put a framework type (e.g. MultipartFile) in an application-layer port — convert it to
byte[] in the controller.

## Package structure

Root package: `com.fabio.GestionFacturas`

```
domain
├── gasto      -> Gasto, EstadoGasto, GastoInvalidoException, FacturaTextParser
│                 (parser: raw OCR text -> extracted fields; framework-free)
├── categoria  -> Categoria
├── usuario    -> Usuario, RefreshToken, EmailYaRegistradoException,
│                 CredencialesInvalidadException  (note: existing typo, "Invalidad")
└── shared     -> Dinero (value object)

application
├── gasto      -> port/in (CrearGasto, ConsultarGastos, DigitalizarFactura),
│                 port/out (GastoRepositoryPort), service
├── categoria  -> port/in, port/out, service
├── usuario    -> port/in (RegistrarUsuario, Autenticar, refresh/logout use cases),
│                 port/out (UsuarioRepositoryPort, TokenGeneradorPort,
│                 RefreshTokenRepositoryPort, refresh token generator port), service
└── shared     -> port/out (OcrPort, FileStoragePort, ExportPort)

infrastructure
├── adapter/in/web   -> gasto, categoria, usuario (AuthController), health,
│                       GlobalExceptionHandler
├── adapter/out/persistence -> gasto, categoria, usuario (+ refresh_token):
│                       JpaEntity + JpaRepository + Mapper + PersistenceAdapter each
├── adapter/out/ocr  -> MockOcrAdapter (default), TesseractOcrAdapter (@Profile("ocr")),
│                       OcrException
├── adapter/out/storage -> LocalStorageAdapter (implements FileStoragePort)
└── config          -> SecurityConfig, OpenApiConfig, config/security
                        (JwtTokenProvider, JwtAuthenticationFilter)
```

`ingreso` and `cliente` modules are planned (Phase 2), not present yet.

## Security (implemented)

- Passwords hashed with BCrypt. Domain `Usuario` holds `passwordHash`, never a raw password.
- JWT: `JwtTokenProvider` (config/security) generates/validates tokens and implements the
  application port `TokenGeneradorPort`. User id is the subject; email is a claim.
- `JwtAuthenticationFilter` (OncePerRequestFilter) validates the Bearer token and stores
  the user id (Long) as the SecurityContext principal.
- Controllers get the user via `@AuthenticationPrincipal Long usuarioId`. Never hardcode.
- Refresh tokens: revocable, stored HASHED in the `refresh_token` table (SHA-256 so they
  can be looked up; NOT BCrypt). Logout marks `revocado = true` (does not delete the row).
  No token rotation yet (intentionally deferred — do not add it unless asked).
- SecurityConfig: stateless, CSRF disabled, JWT filter registered. Public routes:
  `/api/auth/**`, `/api/health`, springdoc/swagger routes. Everything else authenticated.
- Note: the default Spring Security autoconfig still logs a "generated security password"
  line at startup; this is harmless noise — the custom SecurityConfig is the one in effect
  (protected endpoints correctly reject unauthenticated requests).

## OCR / invoice digitization (Phase 1, implemented)

- `OcrPort` (application/shared): `byte[] -> String` (plain text only). OCR's only job is
  image -> text; it must NOT parse fields. Keeps OCR interchangeable.
- `MockOcrAdapter`: default bean (passthrough / fixed text) for dev and tests, so the flow
  works without the native engine.
- `TesseractOcrAdapter`: `@Profile("ocr")`, uses Tess4J. Datapath/language come from
  config (do NOT hardcode absolute paths). Currently handles images only; PDF is a
  pending follow-up (PDFBox is available for it).
- `FacturaTextParser` (domain): raw text -> fields (emisor, fecha, base, iva, total).
  Framework-free, unit-tested. Amounts as BigDecimal; missing fields -> null (the user
  fills them in during review). Built/refined with TDD against real OCR output.
- `DigitalizarFacturaService` orchestrates: store file -> OCR -> parse -> create Gasto in
  BORRADOR with the storage reference. `Gasto` has a nullable `referenciaArchivo`.

## Error handling (HTTP status mapping)

`GlobalExceptionHandler` (@RestControllerAdvice) maps domain exceptions:
- `GastoInvalidoException`, `IllegalArgumentException` -> 400
- `MethodArgumentNotValidException` -> 400 with field errors
- `EmailYaRegistradoException` -> 409
- `CredencialesInvalidadException` -> 401
  Domain throws domain exceptions; the handler translates to HTTP. The domain never knows HTTP.

## Code conventions

- No ternary operators. Explicit `if`.
- Constructor injection only. Never @Autowired on fields.
- DTOs and commands are records (commands nested in their use-case interface).
- Money is BigDecimal, wrapped in the `Dinero` value object in the domain.
- Enums persisted as STRING (@Enumerated(EnumType.STRING)).
- Domain objects validate their own invariants and throw domain exceptions.
- Access control: filter by usuarioId; on get-by-id return empty if it belongs to another
  user.
- Spanish identifiers/domain terms (Gasto, crearBorrador, Dinero...).

## Database

- Schema owned by Flyway (`ddl-auto: validate`). Migrations in
  src/main/resources/db/migration as V{n}__desc.sql. Current: V1 init, V2 seed usuario,
  V3 refresh_token, V4 gasto referencia_archivo.
- Do NOT edit applied migrations; add a new V{n}. Column names snake_case, mapped
  explicitly in JPA entities.
- Config (DB creds, JWT secret, tesseract datapath) comes from a `.env` (loaded via the
  EnvFile plugin in IntelliJ). Never hardcode secrets or commit the `.env`.

## Testing

- Unit tests with JUnit 5 + Mockito (mock the ports) for domain and services. Prefer TDD
  for new business logic (login and the invoice parser were built this way).
- Use verify(..., never()) to assert a side effect did NOT happen.
- Tests always use the mock OCR, never the native Tesseract engine.
- Testcontainers for persistence integration: planned, when the layer is stable.

## Environment gotchas (learned the hard way)

- Run with Java 21 (the project target), not a newer JDK, or you get inconsistent behavior.
- If DevTools causes erratic startups after big changes, run `./mvnw clean compile` in the
  terminal to see the real state without IDE/DevTools noise, then Rebuild Project.
- Each run configuration needs the EnvFile enabled, or JWT/DB config arrives empty.

## Working style

- Focused, minimal changes for the task. Do not refactor unrelated code.
- Run `./mvnw compile` (and `./mvnw test` when logic changed) before finishing.
- Show a summary of changed files before considering the task done.
- If a request would violate the architecture rules above, flag it instead of doing it.

## Git workflow

- Before starting a new feature or distinct change, create a branch:
  `git checkout -b feature/<desc>` (prefixes: feature/, fix/, refactor/, test/, docs/).
  Never commit feature work directly to main.
- Focused commits with clear messages. Do not merge to main or delete branches unless
  explicitly asked — leave that to the owner.