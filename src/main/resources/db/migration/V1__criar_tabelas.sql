-- Schema base — Aí 'Se Cream (Flyway)

CREATE TABLE usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    perfil VARCHAR(20) NOT NULL,
    ativo TINYINT(1) NOT NULL DEFAULT 1,
    criado_em DATETIME NOT NULL
);

CREATE TABLE sabor (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao TEXT,
    ativo TINYINT(1) NOT NULL DEFAULT 1,
    criado_em DATETIME NOT NULL
);

CREATE TABLE loja (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    endereco VARCHAR(255) NOT NULL,
    telefone VARCHAR(20),
    ativo TINYINT(1) NOT NULL DEFAULT 1,
    criado_em DATETIME NOT NULL
);

CREATE TABLE lote_producao (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sabor_id INT NOT NULL,
    quantidade_produzida INT NOT NULL,
    quantidade_disponivel INT NOT NULL,
    data_producao DATE NOT NULL,
    criado_em DATETIME NOT NULL,
    criado_por INT NOT NULL,
    CONSTRAINT fk_lote_sabor FOREIGN KEY (sabor_id) REFERENCES sabor (id),
    CONSTRAINT fk_lote_usuario FOREIGN KEY (criado_por) REFERENCES usuario (id)
);

CREATE TABLE distribuicao (
    id INT AUTO_INCREMENT PRIMARY KEY,
    lote_id INT NOT NULL,
    loja_id INT NOT NULL,
    quantidade INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    distribuido_em DATETIME NOT NULL,
    criado_por INT NOT NULL,
    CONSTRAINT fk_dist_lote FOREIGN KEY (lote_id) REFERENCES lote_producao (id),
    CONSTRAINT fk_dist_loja FOREIGN KEY (loja_id) REFERENCES loja (id),
    CONSTRAINT fk_dist_usuario FOREIGN KEY (criado_por) REFERENCES usuario (id)
);

CREATE TABLE baixa_estoque (
    id INT AUTO_INCREMENT PRIMARY KEY,
    loja_id INT NOT NULL,
    lote_id INT NOT NULL,
    quantidade INT NOT NULL,
    observacao TEXT,
    criado_em DATETIME NOT NULL,
    criado_por INT NOT NULL,
    CONSTRAINT fk_baixa_loja FOREIGN KEY (loja_id) REFERENCES loja (id),
    CONSTRAINT fk_baixa_lote FOREIGN KEY (lote_id) REFERENCES lote_producao (id),
    CONSTRAINT fk_baixa_usuario FOREIGN KEY (criado_por) REFERENCES usuario (id)
);
