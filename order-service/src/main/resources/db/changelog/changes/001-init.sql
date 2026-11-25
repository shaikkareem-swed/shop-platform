--liquibase formatted sql

--changeset shaik:001-create-orders
CREATE TABLE orders (
    id            UUID PRIMARY KEY,
    customer_id   VARCHAR(255)  NOT NULL,
    article_id    BIGINT        NOT NULL,
    quantity      INTEGER       NOT NULL,
    total_price   NUMERIC(14,2) NOT NULL,
    status        VARCHAR(20)   NOT NULL,
    cancel_reason VARCHAR(255),
    created_at    TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at    TIMESTAMP(6) WITH TIME ZONE NOT NULL
);
--rollback DROP TABLE orders;

--changeset shaik:002-create-order-view
CREATE TABLE order_view (
    id            UUID PRIMARY KEY,
    customer_id   VARCHAR(255),
    article_id    BIGINT,
    quantity      INTEGER NOT NULL,
    total_price   NUMERIC(38,2),
    status        VARCHAR(255),
    cancel_reason VARCHAR(255),
    created_at    TIMESTAMP(6) WITH TIME ZONE,
    updated_at    TIMESTAMP(6) WITH TIME ZONE
);
CREATE INDEX idx_order_view_customer_id ON order_view (customer_id);
--rollback DROP TABLE order_view;
