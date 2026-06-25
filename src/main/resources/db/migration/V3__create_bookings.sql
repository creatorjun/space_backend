CREATE TYPE booking_status AS ENUM (
    'PENDING',
    'CONFIRMED',
    'COMPLETED',
    'CANCEL_REQUESTED',
    'CANCELLED_BY_USER',
    'CANCELLED_BY_ADMIN'
);

CREATE TYPE payment_type AS ENUM ('NAVER_PAY', 'KAKAO_PAY');

CREATE TABLE bookings (
    id                   UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id              UUID           NOT NULL REFERENCES users(id),
    space_id             UUID           NOT NULL REFERENCES spaces(id),
    start_at             TIMESTAMPTZ    NOT NULL,
    end_at               TIMESTAMPTZ    NOT NULL,
    hours                INT            NOT NULL,
    headcount            INT            NOT NULL DEFAULT 1,
    total_price          INT            NOT NULL DEFAULT 0,
    payment_type         payment_type,
    status               booking_status NOT NULL DEFAULT 'PENDING',
    pending_expires_at   TIMESTAMPTZ,
    memo                 TEXT,
    admin_memo           TEXT,
    created_at           TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT bookings_end_after_start CHECK (end_at > start_at)
);

ALTER TABLE bookings
    ADD CONSTRAINT bookings_no_overlap
    EXCLUDE USING gist (
        space_id WITH =,
        tstzrange(start_at, end_at) WITH &&
    )
    WHERE (status IN ('PENDING', 'CONFIRMED'));

CREATE INDEX idx_bookings_user_id ON bookings(user_id);
CREATE INDEX idx_bookings_space_id ON bookings(space_id);
CREATE INDEX idx_bookings_status ON bookings(status);
CREATE INDEX idx_bookings_start_at ON bookings(start_at);
CREATE INDEX idx_bookings_pending_expires_at ON bookings(pending_expires_at) WHERE status = 'PENDING';
