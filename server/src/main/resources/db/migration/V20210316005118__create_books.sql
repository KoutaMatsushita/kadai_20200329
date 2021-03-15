CREATE TABLE books
(
    id        SERIAL PRIMARY KEY,
    name      TEXT NOT NULL,
    author_id INTEGER NOT NULL REFERENCES authors(id)
);

CREATE INDEX ON books(name);

COMMENT ON TABLE books IS '書籍';
COMMENT ON COLUMN books.id IS '書籍の ID';
COMMENT ON COLUMN books.name IS '書籍の名前';