-- Milestone 2 (D6): idempotency_record(principal, idem_key, payload_hash, operation_id, response, created_at)
--                   with UNIQUE (principal, idem_key)

CREATE TABLE orders (
    id          UUID           PRIMARY KEY,
    account_id  VARCHAR(64)    NOT NULL,
    symbol      VARCHAR(16)    NOT NULL,
    side        VARCHAR(8)     NOT NULL,
    order_type  VARCHAR(8)     NOT NULL,
    quantity    NUMERIC(19, 4) NOT NULL CHECK (quantity > 0),
    limit_price NUMERIC(19, 4) CHECK (limit_price > 0),
    status      VARCHAR(16)    NOT NULL,
    created_at  TIMESTAMPTZ    NOT NULL,
    CHECK (order_type <> 'LIMIT' OR limit_price IS NOT NULL)
);

-- Serves "most recent orders for these accounts"
CREATE INDEX idx_orders_account_created ON orders (account_id, created_at DESC);
