ALTER TABLE gasto ADD COLUMN cliente_id BIGINT REFERENCES cliente(id);

CREATE INDEX idx_gasto_cliente ON gasto(cliente_id);
