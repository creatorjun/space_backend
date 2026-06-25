CREATE TYPE payment_status AS ENUM (
    'READY',
    'APPROVED',
    'REFUND_REQUESTED',
    'REFUNDED',
    'FAILED'
);

CREATE TYPE payment_provider AS ENUM ('NAVER_PAY', 'KAKAO_PAY');

CREATE TABLE payments (
    id                 UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    booking_id         UUID             NOT NULL UNIQUE REFERENCES bookings(id),
    user_id            UUID             NOT NULL REFERENCES users(id),
    provider           payment_provider NOT NULL,
    amount_krw         INT              NOT NULL DEFAULT 0,
    status             payment_status   NOT NULL DEFAULT 'READY',
    pg_order_id        TEXT             NOT NULL UNIQUE,
    pg_transaction_id  TEXT,
    refund_amount_krw  INT,
    refund_reason      TEXT,
    paid_at            TIMESTAMPTZ,
    refunded_at        TIMESTAMPTZ,
    created_at         TIMESTAMPTZ      NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ      NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_payments_booking_id ON payments(booking_id);
CREATE INDEX idx_payments_user_id ON payments(user_id);
CREATE INDEX idx_payments_pg_order_id ON payments(pg_order_id);
CREATE INDEX idx_payments_status ON payments(status);
