CREATE TABLE IF NOT EXISTS users
(
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name    VARCHAR(255)                            NOT NULL,
    email   VARCHAR(512)                            NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (user_id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items
(
    item_id     BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name        VARCHAR(255)                            NOT NULL,
    description VARCHAR(512)                            NOT NULL,
    available   BOOLEAN                                 NOT NULL DEFAULT FALSE,
    owner       BIGINT REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT pk_item PRIMARY KEY (item_id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    booking_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_time TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    end_time   TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    item_id    BIGINT REFERENCES items (item_id) ON DELETE CASCADE,
    booker_id  BIGINT REFERENCES users (user_id) ON DELETE CASCADE,
    status     VARCHAR(64)                             NOT NULL,
    PRIMARY KEY (booking_id)
);


CREATE TABLE IF NOT EXISTS requests
(
    request_id  BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description VARCHAR(512)                            NOT NULL,
    requestor   BIGINT REFERENCES users (user_id) ON DELETE CASCADE,
    created     TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    PRIMARY KEY (request_id)
);

CREATE TABLE IF NOT EXISTS comments
(
    comment_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text       VARCHAR(512)                            NOT NULL,
    item_id    BIGINT REFERENCES items (item_id) ON DELETE CASCADE,
    author_id  BIGINT REFERENCES users (user_id) ON DELETE CASCADE,
    created    TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    PRIMARY KEY (comment_id)
);
