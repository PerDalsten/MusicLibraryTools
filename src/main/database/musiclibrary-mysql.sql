CREATE DATABASE musiclibrarydb;

USE musiclibrarydb;

CREATE TABLE artist
(
	id INT NOT NULL AUTO_INCREMENT,
	artist_name VARCHAR(255) NOT NULL,
	PRIMARY KEY (id),
	UNIQUE (artist_name)
);

CREATE TABLE album
(
	id INT NOT NULL AUTO_INCREMENT,
	artist_id INT NOT NULL,
	album_title VARCHAR(255) NOT NULL,
	album_year SMALLINT NOT NULL,	
    PRIMARY KEY (id),
	FOREIGN KEY (artist_id) REFERENCES artist(id)
);

CREATE TABLE song
(
	id INTEGER NOT NULL AUTO_INCREMENT,
	album_id INTEGER NOT NULL,
	song_title VARCHAR(255) NOT NULL,
	track SMALLINT NOT NULL,	
	disc SMALLINT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (album_id) REFERENCES album (id)
);

USE mysql;

CREATE USER 'musiclibrary'@'localhost' IDENTIFIED BY 'musiclibrary';

GRANT ALL PRIVILEGES ON musiclibrarydb.* TO 'musiclibrary'@'localhost';
