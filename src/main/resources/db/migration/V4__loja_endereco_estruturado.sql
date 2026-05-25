-- Endereço estruturado da loja (substitui coluna única endereco)
ALTER TABLE loja ADD COLUMN IF NOT EXISTS cep VARCHAR(9) NULL;
ALTER TABLE loja ADD COLUMN IF NOT EXISTS estado CHAR(2) NULL;
ALTER TABLE loja ADD COLUMN IF NOT EXISTS cidade VARCHAR(100) NULL;
ALTER TABLE loja ADD COLUMN IF NOT EXISTS logradouro VARCHAR(150) NULL;
ALTER TABLE loja ADD COLUMN IF NOT EXISTS numero VARCHAR(20) NULL;
ALTER TABLE loja ADD COLUMN IF NOT EXISTS complemento VARCHAR(100) NULL;

UPDATE loja
SET
    logradouro = COALESCE(NULLIF(TRIM(logradouro), ''), NULLIF(TRIM(endereco), ''), 'Endereço não informado'),
    numero = COALESCE(NULLIF(TRIM(numero), ''), 'S/N'),
    cidade = COALESCE(NULLIF(TRIM(cidade), ''), 'Não informado'),
    estado = COALESCE(NULLIF(TRIM(estado), ''), 'NI'),
    cep = COALESCE(NULLIF(TRIM(cep), ''), '00000-000')
WHERE endereco IS NOT NULL OR logradouro IS NULL;

UPDATE loja
SET
    logradouro = COALESCE(NULLIF(TRIM(logradouro), ''), 'Endereço não informado'),
    numero = COALESCE(NULLIF(TRIM(numero), ''), 'S/N'),
    cidade = COALESCE(NULLIF(TRIM(cidade), ''), 'Não informado'),
    estado = COALESCE(NULLIF(TRIM(estado), ''), 'NI'),
    cep = COALESCE(NULLIF(TRIM(cep), ''), '00000-000')
WHERE logradouro IS NULL;

ALTER TABLE loja DROP COLUMN endereco;

ALTER TABLE loja MODIFY cep VARCHAR(9) NOT NULL;
ALTER TABLE loja MODIFY estado CHAR(2) NOT NULL;
ALTER TABLE loja MODIFY cidade VARCHAR(100) NOT NULL;
ALTER TABLE loja MODIFY logradouro VARCHAR(150) NOT NULL;
ALTER TABLE loja MODIFY numero VARCHAR(20) NOT NULL;
