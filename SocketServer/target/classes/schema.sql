DROP TABLE IF EXISTS users,  messages, rooms, rooms CASCADE;

SET TIME ZONE 'GMT';

CREATE TABLE IF NOT EXISTS users (
    login VARCHAR(30) PRIMARY KEY NOT NULL,
    password VARCHAR(1000) NOT NULL
);

CREATE TABLE IF NOT EXISTS rooms (
    name VARCHAR(30) PRIMARY KEY NOT NULL
);

CREATE TABLE IF NOT EXISTS messages (
    text VARCHAR(3000) NOT NULL,
    sender VARCHAR(30) REFERENCES users(login),
    date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP PRIMARY KEY NOT NULL,
    room VARCHAR(30) REFERENCES rooms(name)
);

INSERT INTO users (login, password) VALUES ('Ann625', '$2a$10$qF1QRa18S6fRsXcN01FV7ulCc09J3xdZ8ChKjnwqBz0scpYxrzomW'), ('Ivan3000', '$2a$10$5sj0filanvnQ3Q6.OX4BCuXNFzKqohCNaAFI4VELcREN4Fh0pzVFK'), ('jiordan', '$2a$10$TXJM3MjnPotaoWT2IOZGsONGNiQo27zeRCW3HkpqP81SRJXsw3IXK');

INSERT INTO rooms (name) VALUES ('Egypt'), ('friends'), ('sales');

INSERT INTO messages (text, sender, room) VALUES ('Hi!','Ann625', 'friends');

INSERT INTO messages (text, sender, room) VALUES ('What`s new?', 'Ann625', 'friends');

INSERT INTO messages (text, sender, room) VALUES ('Hello', 'Ivan3000', 'friends');

INSERT INTO messages (text, sender, room) VALUES  ('I need photos', 'jiordan', 'Egypt');