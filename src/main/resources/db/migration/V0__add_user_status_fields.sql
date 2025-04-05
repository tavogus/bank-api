-- Adiciona os campos de status da conta do usuário se não existirem
ALTER TABLE users
ADD COLUMN IF NOT EXISTS account_non_expired BOOLEAN DEFAULT TRUE,
ADD COLUMN IF NOT EXISTS account_non_locked BOOLEAN DEFAULT TRUE,
ADD COLUMN IF NOT EXISTS credentials_non_expired BOOLEAN DEFAULT TRUE,
ADD COLUMN IF NOT EXISTS enabled BOOLEAN DEFAULT TRUE; 