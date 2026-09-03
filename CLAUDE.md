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
- Spring Security + JWT (planned)
- Maven
- JUnit 5 + Mockito + Testcontainers (tests)

## Architecture — READ THIS CAREFULLY

This project uses **pure hexagonal architecture (ports and adapters)**. This is a
deliberate design choice and must be preserved. Do NOT collapse it into a
conventional layered CRUD structure, even if that would be shorter.

Three layers, with dependencies always pointing inward:

```
domain          → business core. Pure POJOs. NO Spring, NO JPA, NO framework imports.
application     → use cases + ports (in/out interfaces). May use @Service/@Transactional.
infrastructure  → adapters (web, persistence, OCR, storage) + config. All framework code lives here.
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

Mappers translate between them. Do not make the domain model a JPA entity. Do not
expose JPA entities or domain objects directly in controllers. This decoupling is
the whole point of the architecture.

### Package structure

```
com.GestionFacturas
├── domain          → per concept: gasto, ingreso, cliente, categoria, usuario, shared
├── application     → per concept: port/in, port/out, service
└── infrastructure
    ├── adapter/in/web        → controllers, dto, web mappers
    ├── adapter/out/persistence → JPA entities, repositories, adapters, mappers
    ├── adapter/out/ocr        → OCR adapters (planned)
    ├── adapter/out/storage    → file storage adapters (planned)
    └── config                 → security, OpenAPI
```

## Code conventions

- **No ternary operators.** Use explicit `if` statements. (Owner preference.)
- **Constructor injection only.** Never `@Autowired` on fields.
- **DTOs and commands are `record`s.**
- **Money is always `BigDecimal`**, never `float`/`double`. In the domain it is
  wrapped in the `Dinero` value object (amount + currency).
- **Enums persisted as STRING** (`@Enumerated(EnumType.STRING)`), never ordinal.
- **Domain objects validate their own invariants** in the constructor and throw
  domain exceptions (e.g. `GastoInvalidoException`), not generic ones.
- **Access control in queries**: a user must only access their own data. Filter by
  `usuarioId` in read operations.
- Language: code identifiers and domain terms are in Spanish (Gasto, Ingreso,
  Cliente, crearBorrador...). Keep that consistent.

## Database

- Schema is owned by **Flyway**, not Hibernate. `spring.jpa.hibernate.ddl-auto` is
  `validate` — Hibernate only checks entities match the schema, never creates or
  alters tables.
- Migrations live in `src/main/resources/db/migration/` as `V{n}__description.sql`.
- While the schema is still unstable and there is no real data, editing `V1__init.sql`
  directly and recreating the DB (`docker compose down -v && docker compose up -d`)
  is acceptable. Once there is data to preserve, add a new `V{n}` migration instead.
- Column names are `snake_case`; map them explicitly in JPA entities.

## Testing

- Domain and services: unit tests with JUnit 5 + Mockito (mock the ports). These are
  fast and high-value — prioritize them.
- Persistence/integration: Testcontainers with a real PostgreSQL. Add these once the
  persistence layer is stable, not before.

## Current status (Phase 0)

Building the backend end-to-end with manual CRUD, no OCR yet. Temporary scaffolds
that are known and intentional:
- `usuarioId` is hardcoded to `1L` in controllers (will come from the JWT in Phase 1).
- Security is open (`permitAll`) so the flow can be tested (will be hardened with JWT).

Do not "fix" these scaffolds unless the task is specifically about JWT/security.

## Working style

- Make focused, minimal changes for the task at hand. Do not refactor unrelated code.
- Before finishing, compile to verify no broken references.
- Show a summary of changed files before considering the task done.
- If a request would violate the architecture rules above, flag it instead of
  silently doing it.