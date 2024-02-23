DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS messages;

CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY NOT NULL,
    login VARCHAR(30) NOT NULL,
    password VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS messages (
    id SERIAL PRIMARY KEY NOT NULL,
    text VARCHAR(3000) NOT NULL,
    sender_id INTEGER NOT NULL REFERENCES users(id),
    date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO users (login, password) VALUES ('Ann625', 'aaaa1111'), ('Ivan3000', 'bbbb4444'), ('jiordan', 'cccc6666');

INSERT INTO messages (text, sender_id) VALUES ('Hi!', 1), ('What`s new?', 1), ('Hello', 2);