-- Glean Generated Code
-- Run once against the RDS MySQL instance after `terraform apply`:
--   mysql -h <rds-endpoint> -u orderapp -p orders < schema.sql

CREATE TABLE IF NOT EXISTS orders (
    order_id     VARCHAR(64) PRIMARY KEY,
    customer_id  VARCHAR(64) NOT NULL,
    sku          VARCHAR(64) NOT NULL,
    quantity     INT         NOT NULL,
    price_cents  BIGINT      NOT NULL,
    total_cents  BIGINT      NOT NULL,
    created_at   TIMESTAMP   NOT NULL,
    INDEX idx_customer_id (customer_id),
    INDEX idx_sku (sku),
    INDEX idx_created_at (created_at)
);
