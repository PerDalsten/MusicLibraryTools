CREATE SCHEMA musiclibrary;
SET SCHEMA=musiclibrary;

CREATE TABLE artist
(
	id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	artist_name VARCHAR(255) NOT NULL,
	CONSTRAINT artist_pk PRIMARY KEY (id),
	CONSTRAINT artist_uniq UNIQUE (artist_name)
);

CREATE TABLE album
(
	id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	artist_id INTEGER NOT NULL,
	album_title VARCHAR(255) NOT NULL,
	album_year SMALLINT NOT NULL,	
    CONSTRAINT album_pk PRIMARY KEY (id),
    CONSTRAINT artist_fk FOREIGN KEY (artist_id) REFERENCES artist (id)
);

CREATE TABLE song
(
	id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	album_id INTEGER NOT NULL,
	song_title VARCHAR(255) NOT NULL,
	track SMALLINT NOT NULL,	
	disc SMALLINT NOT NULL,
    CONSTRAINT song_pk PRIMARY KEY (id),
    CONSTRAINT album_fk FOREIGN KEY (album_id) REFERENCES album (id)
);
