--liquibase formatted sql


--changeset mrshoffen:2
CREATE TABLE IF NOT EXISTS tasks
(
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name           VARCHAR(256) NOT NULL,
    created_at     TIMESTAMP    NOT NULL,
    completed      BOOLEAN      NOT NULL,
    order_index    BIGINT       NOT NULL,
    user_id        UUID         NOT NULL,
    workspace_id   UUID         NOT NULL,
    desk_id        UUID         NOT NULL,
    parent_task_id UUID,
    UNIQUE (desk_id, name)
);