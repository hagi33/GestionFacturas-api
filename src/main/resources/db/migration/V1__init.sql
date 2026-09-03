CREATE TABLE usuario (
    id          BIGSERIAL PRIMARY KEY,
    email       VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    nombre      VARCHAR(255) NOT NULL,
    creado_en   TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE categoria (
    id                  BIGSERIAL PRIMARY KEY,
    nombre              VARCHAR(100) NOT NULL UNIQUE,
    deducible_defecto   BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE gasto (
    id              BIGSERIAL PRIMARY KEY,
    usuario_id      BIGINT NOT NULL REFERENCES usuario(id),
    categoria_id    BIGINT REFERENCES categoria(id),
    emisor          VARCHAR(255),
    fecha_emision   DATE,
    base_imponible  NUMERIC(12,2),
    iva             NUMERIC(12,2),
    total           NUMERIC(12,2),
    moneda          VARCHAR(3) NOT NULL DEFAULT 'EUR',
    deducible       BOOLEAN NOT NULL DEFAULT false,
    estado          VARCHAR(20) NOT NULL DEFAULT 'BORRADOR',
    creado_en       TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_gasto_usuario ON gasto(usuario_id);
CREATE INDEX idx_gasto_fecha ON gasto(fecha_emision);
CREATE INDEX idx_gasto_estado ON gasto(estado);