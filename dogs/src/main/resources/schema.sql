CREATE TABLE IF NOT EXISTS dog_breed
(
    id BIGSERIAL PRIMARY KEY,
    breed VARCHAR(256) NOT NULL,
    sub_breed VARCHAR(256),
    image BYTEA
);
