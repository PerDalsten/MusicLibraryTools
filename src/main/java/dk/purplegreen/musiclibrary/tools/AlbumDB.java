package dk.purplegreen.musiclibrary.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AlbumDB {

	private static final Logger log = LogManager.getLogger(AlbumDB.class);
	
	private static final String SELECT_ARTIST_SQL = "SELECT id, artist_name AS artist FROM artist";

	private static final String INSERT_ALBUM_SQL = "INSERT INTO album (artist_id, album_title, album_year) VALUES (?,?,?)";

	private static final String INSERT_SONG_SQL = "INSERT INTO song (album_id, song_title, track, disc) VALUES (?,?,?,?)";

	private static final String INSERT_ARTIST_SQL = "INSERT INTO artist (artist_name) VALUES (?)";

	private static final String SELECT_ALBUM_SQL = "SELECT album.id AS id, artist.artist_name AS artist, album.album_title AS title, album.album_year AS yr FROM album JOIN artist ON album.artist_id = artist.id ORDER BY artist, yr";

	private static final String SELECT_SONG_SQL = "SELECT album_id, song_title AS title, track, disc FROM song ORDER BY album_id, disc, track";

	private String connectionURL;

	public AlbumDB(String connectionURL) {
		this.connectionURL = connectionURL;
	}

	private Connection getConnection() throws SQLException {
		return DriverManager.getConnection(connectionURL);
	}

	public void save(Album album) throws AlbumException {
		save(Arrays.asList(album).iterator());
	}
	
	public void save(Iterator<Album> albums) throws AlbumException {
		try (Connection con = getConnection()) {
			con.setAutoCommit(false);

			try (PreparedStatement stmtArtist = con.prepareStatement(INSERT_ARTIST_SQL,
					Statement.RETURN_GENERATED_KEYS);
					PreparedStatement stmtAlbum = con.prepareStatement(INSERT_ALBUM_SQL,
							Statement.RETURN_GENERATED_KEYS);
					PreparedStatement stmtSong = con.prepareStatement(INSERT_SONG_SQL);) {

				Map<String, Integer> artistMap = new HashMap<>();
				try (PreparedStatement stmtExistingArtists = con.prepareStatement(SELECT_ARTIST_SQL)) {
					ResultSet rsExistingArtists = stmtExistingArtists.executeQuery();
					while (rsExistingArtists.next()) {
						artistMap.put(rsExistingArtists.getString("artist"), rsExistingArtists.getInt("id"));
					}
				}

				while (albums.hasNext()) {
					ResultSet rsArtist;

					Album album = albums.next();
					if (artistMap.get(album.getArtist()) == null) {
						stmtArtist.setString(1, album.getArtist());
						stmtArtist.executeUpdate();
						rsArtist = stmtArtist.getGeneratedKeys();
						if (rsArtist.next()) {
							int artist_id = rsArtist.getInt(1);
							artistMap.put(album.getArtist(), artist_id);
							log.info("Created artist: " + album.getArtist() + " with id: " + artist_id);
						}
					}

					stmtAlbum.setInt(1, artistMap.get(album.getArtist()));
					stmtAlbum.setString(2, album.getTitle());
					stmtAlbum.setInt(3, album.getYear());
					stmtAlbum.executeUpdate();

					int album_id = -1;
					ResultSet rsAlbum = stmtAlbum.getGeneratedKeys();
					if (rsAlbum.next()) {
						album_id = rsAlbum.getInt(1);
					}

					log.info("Created album: " + album + " with id: " + album_id);

					for (Song song : album.getSongs()) {
						stmtSong.setInt(1, album_id);
						stmtSong.setString(2, song.getTitle());
						stmtSong.setInt(3, song.getTrack());
						stmtSong.setInt(4, song.getDisc());
						stmtSong.addBatch();
					}

					stmtSong.executeBatch();
				}
			} catch (SQLException e) {
				con.rollback();
				throw e;
			}
			con.commit();
		} catch (SQLException e) {
			log.error(e);
			throw new DatabaseException(e);
		}
	}

	public Collection<Album> load() throws AlbumException {

		Map<Integer, Album> albums = new HashMap<>();

		try (Connection con = getConnection()) {
			con.setAutoCommit(true);

			try (PreparedStatement stmtAlbum = con.prepareStatement(SELECT_ALBUM_SQL)) {

				ResultSet rsAlbum = stmtAlbum.executeQuery();

				while (rsAlbum.next()) {
					Album album = new Album();
					album.setArtist(rsAlbum.getString("artist"));
					album.setTitle(rsAlbum.getString("title"));
					album.setYear(rsAlbum.getInt("yr"));
					albums.put(rsAlbum.getInt("id"), album);
				}
			}

			try (PreparedStatement stmtSong = con.prepareStatement(SELECT_SONG_SQL)) {

				ResultSet rsSong = stmtSong.executeQuery();

				while (rsSong.next()) {
					Song song = new Song();
					song.setTitle(rsSong.getString("title"));
					song.setTrack(rsSong.getInt("track"));
					song.setDisc(rsSong.getInt("disc"));

					albums.get(rsSong.getInt("album_id")).getSongs().add(song);
				}

				LinkedList<Album> result = new LinkedList<>(albums.values());

				result.sort(Comparator.comparing((Album album) -> album.getArtist().toLowerCase())
						.thenComparing(Album::getYear));

				return result;
			}
		} catch (SQLException e) {
			log.error(e);
			throw new DatabaseException(e);
		}
	}
}
