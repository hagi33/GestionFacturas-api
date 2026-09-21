# GestionFacturas — Control económico para autónomos

> ⚠️ **Proyecto en desarrollo activo.** Este README describe el estado actual y la
> dirección del proyecto, y evoluciona con él.

Aplicación para que **freelances y autónomos individuales** lleven el control de su
actividad económica: registran sus **gastos**, sus **ingresos** y sus **clientes**,
digitalizan facturas con OCR, y lo mantienen ordenado para entregar al gestor. El objetivo
es dar visión de beneficio y de rentabilidad por cliente.

Principio rector: **organización y visibilidad, nunca asesoría fiscal ni facturación
oficial**. La app registra y reporta; no calcula la declaración ni emite facturas legales.

## El problema que resuelve

Gestionar la contabilidad siendo autónomo es un caos manual: facturas desperdigadas,
ingresos sin controlar, cobros que se pierden de vista, y un desastre que ordenar cada
trimestre. La app ataca ese dolor siendo fuerte en cuatro cosas: capturar sin esfuerzo
(foto -> datos), no perder nada, entender el dinero (qué entra, qué sale, cuánto queda,
quién me debe, por cliente), y entregar ordenado al gestor.

## Público objetivo

Freelances y autónomos individuales que trabajan con un gestor externo. La app no sustituye
al gestor: le da el trabajo ya ordenado.

## Stack tecnológico

| Capa | Tecnología |
|------|-----------|
| Lenguaje | Java 21 |
| Framework | Spring Boot 4.1.x |
| Base de datos | PostgreSQL |
| Migraciones | Flyway |
| Seguridad | Spring Security + JWT (access + refresh revocable) |
| OCR | Tesseract vía Tess4J (español); mock para desarrollo/tests |
| Almacenamiento de archivos | Sistema de archivos local (MinIO planificado) |
| Documentación API | OpenAPI / Swagger (springdoc 3.x) |
| Build | Maven |
| Tests | JUnit 5 + Mockito + AssertJ |
| Entorno | Docker Compose (PostgreSQL) |
| Cliente | Kotlin Multiplatform (KMP) + Compose Multiplatform *(planificado; móvil + escritorio)* |

## Arquitectura

Arquitectura **hexagonal (puertos y adaptadores)**, tres capas con las dependencias
apuntando siempre hacia el dominio:

- **domain** — núcleo de negocio. POJOs puros, sin Spring ni JPA.
- **application** — casos de uso + puertos (interfaces in/out).
- **infrastructure** — adaptadores (web, persistencia, OCR, storage, seguridad) + config.

**Regla de dependencia:** `infrastructure` conoce `application`, que conoce `domain`. El
dominio no conoce a nadie. La aplicación **define** los puertos; la infraestructura los
**implementa**. Para cada entidad hay tres modelos separados (DTO / dominio / entidad JPA)
con mappers entre ellos.

```mermaid
flowchart TB
    subgraph INFRA["INFRAESTRUCTURA - Spring, JPA, jjwt, Tesseract"]
        direction TB
        subgraph APP["APLICACION - casos de uso + puertos"]
            direction TB
            subgraph DOM["DOMINIO - POJOs puros"]
                D1["Gasto - Ingreso - Cliente<br/>Usuario - Dinero - FacturaTextParser"]
            end
            PIN["Puertos IN (gasto, ingreso, cliente, usuario)"]
            SVC["Servicios (impl)"]
            POUT["Puertos OUT<br/>Repositorios - Ocr - FileStorage - TokenGenerador"]
        end
        AIN["Adaptadores IN (controllers REST)"]
        AOUT["Adaptadores OUT<br/>JPA - Tesseract/Mock - LocalStorage - Jwt"]
    end

    AIN --> PIN
    PIN --> SVC
    SVC --> DOM
    SVC --> POUT
    AOUT -.implementa.-> POUT

    classDef dom fill:#e8e8e8,stroke:#666,color:#000
    classDef app fill:#efe6f7,stroke:#8257b5,color:#000
    classDef adin fill:#e3f0fb,stroke:#3b82c4,color:#000
    classDef adout fill:#e0f2ef,stroke:#2fa894,color:#000
    class D1 dom
    class PIN,SVC,POUT app
    class AIN adin
    class AOUT adout
```

## Módulos de dominio

- **gasto** — dinero que sale. Creación manual o por digitalización (OCR). Estado
  (borrador/revisado), deducible, referencia al archivo. Opcionalmente imputable a un
  cliente (nullable — muchos gastos son generales).
- **ingreso** — dinero que entra: facturas emitidas a un cliente. Importe (base/IVA/total),
  concepto, y **estado de cobro** (PENDIENTE/COBRADA) con fecha de cobro. El usuario marca
  el cobro a mano (la app no se conecta al banco). Transiciones: `registrarCobro` y
  `revertirCobro` (deshacer), con sus reglas de negocio.
- **cliente** — personas/empresas a las que factura el usuario. Nombre, NIF, email,
  teléfono. NIF único por usuario. Soft delete (`activo`): "borrar" desactiva, no elimina.
- **usuario** — identidad y autenticación (registro, login, refresh, logout).
- **shared** — `Dinero` (objeto de valor).

## Cómo se obtiene la visión económica

- Cada **ingreso** apunta a un **cliente** y tiene un **estado de cobro**: eso permite ver
  quién ha pagado y quién debe. Cada **gasto** puede imputarse a un cliente. Cruzando
  ingresos y gastos por cliente y por periodo se obtiene beneficio y rentabilidad — ese
  cruce y sus vistas son la **Fase 3 (dashboard)**; este backend monta los datos que lo
  hacen posible.

## Seguridad

- Contraseñas con **BCrypt**. Autenticación **JWT**: access token corto + refresh token
  revocable (hash SHA-256 en BD). Logout real. Rotación de tokens: pendiente.
- Usuario autenticado vía `@AuthenticationPrincipal`. Control de acceso por `usuarioId` en
  todas las consultas: un usuario solo ve y opera sobre sus propios datos.

## Digitalización (OCR)

Subes una imagen de factura -> se almacena -> Tesseract extrae el texto -> `FacturaTextParser`
saca los campos (emisor, fecha, importes) -> se crea el gasto en BORRADOR para revisar. OCR
real con el perfil `ocr`; mock por defecto. Pendiente: PDF (ahora solo imágenes), emisor,
OCR asíncrono, MinIO, y digitalización de ingresos.

## Estado actual

- **Fase 0 — Fundamentos** ✅ cerrada y testeada.
- **Seguridad (JWT)** ✅ implementada (falta rotación de refresh tokens).
- **Fase 1 — Digitalización (OCR)** ✅ funcional para gastos (faltan PDF, emisor, async, MinIO).
- **Fase 2 — Ingresos y clientes** ✅ cerrada:
  - [x] Módulo **cliente** (CRUD + soft delete, reglas con TDD)
  - [x] Módulo **ingreso** (estado de cobro, transiciones registrar/revertir con TDD)
  - [x] Gastos e ingresos ligables a **cliente** (rentabilidad por cliente posible)
  - [ ] Digitalización de ingresos por OCR (opcional, pendiente)
- **Fase 3 — Dashboard** ⬜ siguiente: beneficio por periodo, rentabilidad por cliente,
  pendientes de cobro, total facturado.

## Roadmap

| Fase | Foco |
|------|------|
| Fase 0 | Backend en pie, CRUD de gasto (hecho) |
| Seguridad | Autenticación JWT completa (hecho; falta rotación) |
| Fase 1 | OCR + almacenamiento de archivos (hecho; faltan mejoras) |
| Fase 2 | Ingresos y clientes (hecho) |
| Fase 3 | Dashboard: beneficio por periodo, rentabilidad por cliente, pendientes de cobro |
| Fase 4 | Exportación al gestor, pulido, despliegue |
| Cliente | App KMP (móvil + escritorio) consumiendo la API |
| v1+ | Duplicados, recurrentes, presupuestos, categorización automática... |

## Cómo arrancar (desarrollo)

**Requisitos:** Java 21, Docker, Maven. Para OCR real: Tesseract con el idioma español.

```bash
docker compose up -d                 # PostgreSQL
cp .env.example .env                 # y rellenar (BD, JWT_SECRET, ruta tessdata)
./mvnw spring-boot:run               # arranca (mock OCR por defecto)
```

- **Modo mock OCR:** arranque normal, no requiere Tesseract.
- **Modo OCR real:** perfil `ocr` activo (`SPRING_PROFILES_ACTIVE=ocr`) + ruta de tessdata.

API en `http://localhost:8080`. Swagger UI en `http://localhost:8080/swagger-ui.html`
(botón "Authorize" para el token JWT).

## Tests

```bash
./mvnw test
```

Tests unitarios de dominio y servicios (JUnit 5 + Mockito + AssertJ). El login, el parser
de facturas, las reglas de cliente (NIF duplicado, soft delete) y las transiciones de cobro
del ingreso se construyeron con TDD. Los tests usan el mock de OCR, nunca Tesseract real.

## Variables de entorno

Ver `.env.example`: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `SERVER_PORT`, y
la ruta de datos de Tesseract. Nunca subir el `.env` real ni la clave JWT a Git.

---

*Proyecto personal en desarrollo. La documentación se amplía conforme avanzan las fases.*
