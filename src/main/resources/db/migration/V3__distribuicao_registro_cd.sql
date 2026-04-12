-- Registro histórico: produção inicial do lote e saldo no CD após cada distribuição
ALTER TABLE distribuicao ADD COLUMN quantidade_lote_inicial INT NULL;
ALTER TABLE distribuicao ADD COLUMN saldo_disponivel_cd_apos INT NULL;

UPDATE distribuicao d
INNER JOIN lote_producao l ON d.lote_id = l.id
SET d.quantidade_lote_inicial = l.quantidade_produzida;

UPDATE distribuicao SET quantidade_lote_inicial = 0 WHERE quantidade_lote_inicial IS NULL;

ALTER TABLE distribuicao MODIFY quantidade_lote_inicial INT NOT NULL;
