CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS btree_gist;

CREATE TYPE user_role AS ENUM ('USER', 'ADMIN');
CREATE TYPE oauth_provider AS ENUM ('NAVER', 'KAKAO');

CREATE TABLE users (
    id               UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name             VARCHAR(100)  NOT NULL,
    email            VARCHAR(255),
    phone            VARCHAR(20),
    profile_image_url TEXT,
    role             user_role     NOT NULL DEFAULT 'USER',
    is_active        BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE TABLE user_social_accounts (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id     UUID          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    provider    oauth_provider NOT NULL,
    social_id   TEXT          NOT NULL,
    created_at  TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    UNIQUE (provider, social_id)
);

CREATE TABLE refresh_tokens (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id     UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token       TEXT        NOT NULL UNIQUE,
    expires_at  TIMESTAMPTZ NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_user_social_accounts_user_id ON user_social_accounts(user_id);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);
