--liquibase formatted sql

--changeset shaik:001-create-inventory
CREATE TABLE inventory (
    article_id BIGINT  PRIMARY KEY,
    available  INTEGER NOT NULL,
    reserved   INTEGER NOT NULL
);
--rollback DROP TABLE inventory;

--changeset shaik:002-create-reservations
CREATE TABLE reservations (
    order_id   UUID PRIMARY KEY,
    article_id BIGINT      NOT NULL,
    quantity   INTEGER     NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL
);
--rollback DROP TABLE reservations;
