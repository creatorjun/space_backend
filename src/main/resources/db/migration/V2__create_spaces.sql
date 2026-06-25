CREATE TABLE space_categories (
    id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name          VARCHAR(100) NOT NULL,
    display_order INT          NOT NULL DEFAULT 0,
    is_active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE spaces (
    id               UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    category_id      UUID         NOT NULL REFERENCES space_categories(id),
    name             VARCHAR(200) NOT NULL,
    description      TEXT,
    address          VARCHAR(500),
    capacity         INT          NOT NULL DEFAULT 1,
    min_hours        INT          NOT NULL DEFAULT 1,
    max_hours        INT          NOT NULL DEFAULT 24,
    price_per_hour   INT          NOT NULL DEFAULT 0,
    thumbnail_url    TEXT,
    display_order    INT          NOT NULL DEFAULT 0,
    is_active        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE space_images (
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    space_id     UUID NOT NULL REFERENCES spaces(id) ON DELETE CASCADE,
    image_url    TEXT NOT NULL,
    display_order INT NOT NULL DEFAULT 0
);

CREATE TABLE space_operating_hours (
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    space_id     UUID    NOT NULL REFERENCES spaces(id) ON DELETE CASCADE,
    day_of_week  INT     NOT NULL,
    open_time    TIME,
    close_time   TIME,
    is_closed    BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE (space_id, day_of_week)
);

CREATE TABLE space_closed_days (
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    space_id     UUID  NOT NULL REFERENCES spaces(id) ON DELETE CASCADE,
    closed_date  DATE  NOT NULL,
    reason       TEXT,
    UNIQUE (space_id, closed_date)
);

CREATE INDEX idx_spaces_category_id ON spaces(category_id);
CREATE INDEX idx_spaces_is_active ON spaces(is_active);
CREATE INDEX idx_space_images_space_id ON space_images(space_id);
CREATE INDEX idx_space_operating_hours_space_id ON space_operating_hours(space_id);
CREATE INDEX idx_space_closed_days_space_id ON space_closed_days(space_id);
