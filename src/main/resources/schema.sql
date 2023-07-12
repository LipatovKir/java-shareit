CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(255)                            NOT NULL,
    email VARCHAR(512)                            NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description VARCHAR(512)                            NOT NULL,
    requestor_id   BIGINT REFERENCES users (id) ON DELETE CASCADE,
    created     TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS items
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name        VARCHAR(255)                            NOT NULL,
    description VARCHAR(512)                            NOT NULL,
    is_available   BOOLEAN                                 NOT NULL DEFAULT FALSE,
    owner_id    BIGINT REFERENCES users (id) ON DELETE CASCADE,
    request_id  BIGINT REFERENCES requests (id) ON DELETE CASCADE,
    CONSTRAINT pk_item PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_time TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    end_time   TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    item_id    BIGINT REFERENCES items (id) ON DELETE CASCADE,
    booker_id  BIGINT REFERENCES users (id) ON DELETE CASCADE,
    status     VARCHAR(64)                             NOT NULL,
    PRIMARY KEY (booker_id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text      VARCHAR(512)                            NOT NULL,
    item_id   BIGINT REFERENCES items (id) ON DELETE CASCADE,
    author_id BIGINT REFERENCES users (id) ON DELETE CASCADE,
    created   TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    PRIMARY KEY (id)
);
