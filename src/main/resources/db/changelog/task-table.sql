--liquibase formatted sql


--changeset mrshoffen:2
CREATE TABLE IF NOT EXISTS tasks
(
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name         VARCHAR(256) NOT NULL,
    created_at   TIMESTAMP    NOT NULL,
    completed    BOOLEAN      NOT NULL,
    order_index  BIGINT       NOT NULL,
    user_id      UUID         NOT NULL,
    workspace_id UUID         NOT NULL,
    desk_id      UUID         NOT NULL,
    UNIQUE (desk_id, name)
);

CREATE INDEX IF NOT EXISTS tasks_workspace_id_id_idx ON tasks (workspace_id, id);

CREATE INDEX IF NOT EXISTS tasks_workspace_id_idx ON tasks (workspace_id);