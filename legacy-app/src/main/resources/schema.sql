-- Cymbal HK Legacy Merchant & Order Settlement Schema (PostgreSQL 10)

CREATE TABLE IF NOT EXISTS merchants (
    id              SERIAL PRIMARY KEY,
    merchant_code   VARCHAR(32)  NOT NULL UNIQUE,
    name            VARCHAR(160) NOT NULL,
    district        VARCHAR(80)  NOT NULL,
    tier            VARCHAR(20)  NOT NULL DEFAULT 'STANDARD', -- STANDARD | VIP
    contact_email   VARCHAR(160) NOT NULL,
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS merchant_orders (
    id              SERIAL PRIMARY KEY,
    order_ref       VARCHAR(40)    NOT NULL UNIQUE,
    merchant_id     INTEGER        NOT NULL REFERENCES merchants(id),
    amount_hkd      NUMERIC(12, 2) NOT NULL,
    fee_hkd         NUMERIC(12, 2) NOT NULL,
    net_payout_hkd  NUMERIC(12, 2) NOT NULL,
    status          VARCHAR(20)    NOT NULL DEFAULT 'PENDING', -- PENDING | APPROVED | SETTLED | CANCELLED
    created_by      VARCHAR(64)    NOT NULL,
    created_at      TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS audit_logs (
    id              SERIAL PRIMARY KEY,
    order_id        INTEGER      NOT NULL REFERENCES merchant_orders(id),
    action          VARCHAR(40)  NOT NULL,
    actor           VARCHAR(64)  NOT NULL,
    details         TEXT,
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_orders_merchant ON merchant_orders(merchant_id);
CREATE INDEX IF NOT EXISTS idx_orders_status   ON merchant_orders(status);
