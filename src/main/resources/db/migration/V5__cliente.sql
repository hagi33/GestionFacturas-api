CREATE TABLE cliente (
    id          BIGSERIAL PRIMARY KEY,
    usuario_id  BIGINT NOT NULL REFERENCES usuario(id),
    nombre      VARCHAR NOT NULL,
    nif         VARCHAR NOT NULL,
    email       VARCHAR,
    telefono    VARCHAR,
    activo      BOOLEAN NOT NULL DEFAULT true,
    creado_en   TIMESTAMP NOT NULL DEFAULT now(),
    UNIQUE (usuario_id, nif)
);

CREATE INDEX idx_cliente_usuario ON cliente(usuario_id);
