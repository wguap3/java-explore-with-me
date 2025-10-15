DROP TABLE IF EXISTS participation_requests CASCADE;
DROP TABLE IF EXISTS compilation_events CASCADE;
DROP TABLE IF EXISTS compilations CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS users CASCADE;


CREATE TABLE IF NOT EXISTS users(
     user_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
     name VARCHAR(40) NOT NULL,
     email VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS categories(
     category_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
     name VARCHAR(40) NOT NULL
);

CREATE TABLE events(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    annotation VARCHAR(5000) NOT NULL,
    description VARCHAR(10000) NOT NULL,
    category_id BIGINT NOT NULL,
    initiator_id BIGINT NOT NULL,
    state VARCHAR(50) NOT NULL,
    event_date TIMESTAMP NOT NULL,
    created_on TIMESTAMP NOT NULL,
    published_on TIMESTAMP NULL,
    lat DOUBLE PRECISION NOT NULL,
    lon DOUBLE PRECISION NOT NULL,
    paid BOOLEAN NOT NULL,
    participant_limit INT DEFAULT 0,
    request_moderation BOOLEAN DEFAULT TRUE,

    CONSTRAINT fk_event_category
        FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE CASCADE,

    CONSTRAINT fk_event_initiator
        FOREIGN KEY (initiator_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE participation_requests (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    event_id BIGINT NOT NULL,
    requester_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    created TIMESTAMP NOT NULL,

    CONSTRAINT uq_request UNIQUE (event_id, requester_id),

    CONSTRAINT fk_request_event
        FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,

    CONSTRAINT fk_request_user
        FOREIGN KEY (requester_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE compilations (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    pinned BOOLEAN DEFAULT FALSE
);

CREATE TABLE compilation_events (
    compilation_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    PRIMARY KEY (compilation_id, event_id),
    CONSTRAINT fk_compilation FOREIGN KEY (compilation_id) REFERENCES compilations(id) ON DELETE CASCADE,
    CONSTRAINT fk_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
);

