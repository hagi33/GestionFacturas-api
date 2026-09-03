-- Fase 0: usuario semilla con id = 1, para satisfacer la FK gasto.usuario_id mientras
-- el usuarioId sigue hardcodeado en los controladores (se sustituirá por JWT en Fase 1).
-- Password es un valor de relleno, no un hash real; no hay login todavía.
INSERT INTO usuario (id, email, password, nombre)
VALUES (1, 'demo@gestionfacturas.local', 'placeholder', 'Usuario Demo');

SELECT setval('usuario_id_seq', (SELECT MAX(id) FROM usuario));
