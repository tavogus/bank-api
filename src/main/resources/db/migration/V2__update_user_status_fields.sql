-- Atualiza os campos de status da conta do usuário
UPDATE users 
SET account_non_expired = true,
    account_non_locked = true,
    credentials_non_expired = true,
    enabled = true
WHERE account_non_expired IS NULL 
   OR account_non_locked IS NULL 
   OR credentials_non_expired IS NULL 
   OR enabled IS NULL; 