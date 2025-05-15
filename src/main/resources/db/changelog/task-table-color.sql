--liquibase formatted sql


--changeset mrshoffen:3
ALTER TABLE tasks
    ADD COLUMN color     VARCHAR(32)  DEFAULT NULL,
    ADD COLUMN cover_url VARCHAR(255) DEFAULT NULL;