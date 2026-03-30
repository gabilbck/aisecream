-- Usuário inicial para desenvolvimento (senha: admin123 — altere em produção)
INSERT INTO usuario (nome, email, senha_hash, perfil, ativo, criado_em)
VALUES (
    'Administrador',
    'admin@aisecream.com',
    '$2a$10$smjp7ovEjRAXMcCItfUtFO/IDKSwE95jtcpBXiF7fAu64WIkRRiPC',
    'ADMIN',
    1,
    NOW()
);
