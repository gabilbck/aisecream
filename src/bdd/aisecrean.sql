
CREATE DATABASE IF NOT EXISTS aisecrean
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE aisecrean;

-- =====================================================
-- TABELA: usuario
-- =====================================================
CREATE TABLE usuario (
    id          INT          NOT NULL AUTO_INCREMENT,
    nome        VARCHAR(100)    NOT NULL,
    email       VARCHAR(150)    NOT NULL,
    senha_hash  VARCHAR(255)    NOT NULL,
    perfil      ENUM('ADMIN', 'OPERADOR') NOT NULL,
    ativo       BOOLEAN         NOT NULL DEFAULT TRUE,
    criado_em   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_usuario PRIMARY KEY (id),
    CONSTRAINT uq_usuario_email UNIQUE (email)
);

-- =====================================================
-- TABELA: sabor
-- =====================================================
CREATE TABLE sabor (
    id          INT          NOT NULL AUTO_INCREMENT,
    nome        VARCHAR(100)    NOT NULL,
    descricao   TEXT,
    ativo       BOOLEAN         NOT NULL DEFAULT TRUE,
    criado_em   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_sabor PRIMARY KEY (id),
    CONSTRAINT uq_sabor_nome UNIQUE (nome)
);

-- =====================================================
-- TABELA: loja
-- =====================================================
CREATE TABLE loja (
    id          INT          NOT NULL AUTO_INCREMENT,
    nome        VARCHAR(100)    NOT NULL,
    endereco    VARCHAR(255)    NOT NULL,
    telefone    VARCHAR(20),
    ativo       BOOLEAN         NOT NULL DEFAULT TRUE,
    criado_em   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_loja PRIMARY KEY (id)
);

-- =====================================================
-- TABELA: lote_producao
-- =====================================================
CREATE TABLE lote_producao (
    id                      INT  NOT NULL AUTO_INCREMENT,
    sabor_id                INT  NOT NULL,
    quantidade_produzida    INT     NOT NULL CHECK (quantidade_produzida > 0),
    quantidade_disponivel   INT     NOT NULL CHECK (quantidade_disponivel >= 0),
    data_producao           DATE    NOT NULL,
    criado_em               DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    criado_por              INT  NOT NULL,

    CONSTRAINT pk_lote_producao PRIMARY KEY (id),
    CONSTRAINT fk_lote_sabor    FOREIGN KEY (sabor_id)   REFERENCES sabor(id),
    CONSTRAINT fk_lote_usuario  FOREIGN KEY (criado_por) REFERENCES usuario(id)
);

-- =====================================================
-- TABELA: distribuicao
-- =====================================================
CREATE TABLE distribuicao (
    id              INT      NOT NULL AUTO_INCREMENT,
    lote_id         INT      NOT NULL,
    loja_id         INT      NOT NULL,
    quantidade      INT         NOT NULL CHECK (quantidade > 0),
    status          ENUM('ATIVA', 'CANCELADA') NOT NULL DEFAULT 'ATIVA',
    distribuido_em  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    criado_por      INT      NOT NULL,

    CONSTRAINT pk_distribuicao      PRIMARY KEY (id),
    CONSTRAINT fk_dist_lote         FOREIGN KEY (lote_id)    REFERENCES lote_producao(id),
    CONSTRAINT fk_dist_loja         FOREIGN KEY (loja_id)    REFERENCES loja(id),
    CONSTRAINT fk_dist_usuario      FOREIGN KEY (criado_por) REFERENCES usuario(id)
);

-- =====================================================
-- TABELA: baixa_estoque
-- =====================================================
CREATE TABLE baixa_estoque (
    id          INT      NOT NULL AUTO_INCREMENT,
    loja_id     INT      NOT NULL,
    lote_id     INT      NOT NULL,
    quantidade  INT         NOT NULL CHECK (quantidade > 0),
    observacao  TEXT,
    criado_em   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    criado_por  INT      NOT NULL,

    CONSTRAINT pk_baixa_estoque     PRIMARY KEY (id),
    CONSTRAINT fk_baixa_loja        FOREIGN KEY (loja_id)    REFERENCES loja(id),
    CONSTRAINT fk_baixa_lote        FOREIGN KEY (lote_id)    REFERENCES lote_producao(id),
    CONSTRAINT fk_baixa_usuario     FOREIGN KEY (criado_por) REFERENCES usuario(id)
);

-- =====================================================
-- DADOS INICIAIS
-- Senha: admin123 (BCrypt hash gerado com fator 10)
-- =====================================================
INSERT INTO usuario (nome, email, senha_hash, perfil) VALUES
    ('Administrador', 'admin@aisecrean.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN');
