CREATE TABLE authors
(
    id   SERIAL PRIMARY KEY,
    name TEXT UNIQUE NOT NULL
);

COMMENT ON TABLE authors IS '著者';
COMMENT ON COLUMN authors.id IS '著者の ID';
COMMENT ON COLUMN authors.name IS '著者の名前';