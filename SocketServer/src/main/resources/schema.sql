DROP TABLE IF EXISTS users,  messages, rooms, rooms CASCADE;

SET TIME ZONE 'GMT';

CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY NOT NULL,
    login VARCHAR(30) NOT NULL,
    password VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS rooms (
    id SERIAL PRIMARY KEY NOT NULL,
    name VARCHAR(30) NOT NULL
);

CREATE TABLE IF NOT EXISTS messages (
    id SERIAL PRIMARY KEY NOT NULL,
    text VARCHAR(3000) NOT NULL,
    sender_id INTEGER NOT NULL REFERENCES users(id),
    date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    room_id INTEGER NOT NULL REFERENCES rooms(id)
);

INSERT INTO users (login, password) VALUES ('Ann625', 'aaaa1111'), ('Ivan3000', 'bbbb4444'), ('jiordan', 'cccc6666');

INSERT INTO rooms (name) VALUES ('Egypt'), ('friends'), ('sales');

INSERT INTO messages (text, sender_id, room_id) VALUES ('Hi!', 1, 2), ('What`s new?', 1, 2), ('Hello', 2, 2), ('I need photos', 3, 1);
