CREATE TABLE ingreso (
    id              BIGSERIAL PRIMARY KEY,
    usuario_id      BIGINT NOT NULL REFERENCES usuario(id),
    cliente_id      BIGINT REFERENCES cliente(id),
    concepto        VARCHAR(255),
    fecha_emision   DATE,
    base_imponible  NUMERIC(12,2),
    iva             NUMERIC(12,2),
    total           NUMERIC(12,2),
    moneda          VARCHAR(3) NOT NULL DEFAULT 'EUR',
    estado_cobro    VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    fecha_cobro     DATE,
    creado_en       TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_ingreso_usuario ON ingreso(usuario_id);
CREATE INDEX idx_ingreso_fecha ON ingreso(fecha_emision);
CREATE INDEX idx_ingreso_estado_cobro ON ingreso(estado_cobro);
