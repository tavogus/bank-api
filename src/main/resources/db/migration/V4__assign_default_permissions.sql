-- Associa a permissão COMMON_USER a todos os usuários existentes
INSERT INTO user_permissions (id_user, id_permission)
SELECT u.id, p.id
FROM users u
CROSS JOIN permissions p
WHERE p.description = 'COMMON_USER'
AND NOT EXISTS (
    SELECT 1 FROM user_permissions up 
    WHERE up.id_user = u.id AND up.id_permission = p.id
); 