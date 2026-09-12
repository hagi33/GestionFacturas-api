CREATE TABLE refresh_token (
    id            BIGSERIAL PRIMARY KEY,
    usuario_id    BIGINT NOT NULL REFERENCES usuario(id),
    token_hash    VARCHAR(255) NOT NULL UNIQUE,
    expira_en     TIMESTAMP NOT NULL,
    revocado      BOOLEAN NOT NULL DEFAULT false,
    creado_en     TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_refresh_token_hash ON refresh_token(token_hash);
CREATE INDEX idx_refresh_token_usuario ON refresh_token(usuario_id);