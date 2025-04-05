-- Cria a tabela de permissões se não existir
CREATE TABLE IF NOT EXISTS permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255) NOT NULL
);

-- Cria a tabela de relacionamento entre usuários e permissões se não existir
CREATE TABLE IF NOT EXISTS user_permissions (
    id_user BIGINT NOT NULL,
    id_permission BIGINT NOT NULL,
    PRIMARY KEY (id_user, id_permission),
    FOREIGN KEY (id_user) REFERENCES users(id),
    FOREIGN KEY (id_permission) REFERENCES permissions(id)
); 