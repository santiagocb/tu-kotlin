CREATE TABLE IF NOT EXISTS dogbreed
(
    id BIGSERIAL PRIMARY KEY,
    breed VARCHAR(255) NOT NULL,
    sub_breed VARCHAR(255),
    image     BYTEA
);
