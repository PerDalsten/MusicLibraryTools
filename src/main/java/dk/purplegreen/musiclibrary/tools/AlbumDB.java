package dk.purplegreen.musiclibrary.tools;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AlbumDB {

	private final static Logger log = LogManager.getLogger(AlbumDB.class);

	static {
		try {
			Class.forName("org.apache.derby.jdbc.ClientDriver");
		} catch (ClassNotFoundException e) {
			log.error("Error initializing AlbumDB", e);
			throw new RuntimeException("Unable to load database driver", e);
		}
	}

	private Properties p = new Properties();

	public AlbumDB() throws IOException {
		p.load(AlbumDB.class.getResourceAsStream("/musiclibrarytools.properties"));
	}

	private Connection getConnection() throws SQLException {
		Connection con = DriverManager.getConnection(p.getProperty("jdbc.url"));
		con.setAutoCommit(false);
		return con;
	}

	public void save(Album album) throws AlbumException {
		save(Arrays.asList(album).iterator());
	}

	public void save(Iterator<Album> albums) throws AlbumException {

		Connection con = null;
		PreparedStatement stmtArtist = null;
		ResultSet rsArtist = null;
		PreparedStatement stmtAlbum = null;
		ResultSet rsAlbum = null;
		PreparedStatement stmtSong = null;

		try {
			con = getConnection();

			String sql = "INSERT INTO album (artist_id, album_title, album_year) VALUES (?,?,?)";
			stmtAlbum = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			sql = "INSERT INTO song (album_id, song_title, track, disc) VALUES (?,?,?,?)";
			stmtSong = con.prepareStatement(sql);

			sql = "INSERT INTO artist (artist_name) VALUES (?)";
			stmtArtist = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			Map<String, Integer> artistMap = new HashMap<>();
			rsArtist = con.createStatement().executeQuery("SELECT id, artist_name AS artist FROM artist");
			while (rsArtist.next()) {
				artistMap.put(rsArtist.getString("artist"), rsArtist.getInt("id"));
			}

			while (albums.hasNext()) {

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
				rsAlbum = stmtAlbum.getGeneratedKeys();
				if (rsAlbum.next()) {
					album_id = rsAlbum.getInt(1);
				}

				for (Song song : album.getSongs()) {
					stmtSong.setInt(1, album_id);
					stmtSong.setString(2, song.getTitle());
					stmtSong.setInt(3, song.getTrack());
					stmtSong.setInt(4, song.getDisc());
					stmtSong.addBatch();
				}

				stmtSong.executeBatch();
			}

			con.commit();
		} catch (SQLException e) {

			log.error(e);
			if (con != null) {
				try {
					con.rollback();
				} catch (SQLException re) {
					log.error(re);
				}
			}

			throw new DatabaseException(e);
		} finally {
			if (rsArtist != null) {
				try {
					rsArtist.close();
				} catch (SQLException e) {
					log.error(e);
				}
			}
			if (stmtAlbum != null) {

				try {
					stmtAlbum.close();
				} catch (SQLException e) {
					log.error(e);
				}
			}

			if (rsAlbum != null) {
				try {
					rsAlbum.close();
				} catch (SQLException e) {
					log.error(e);
				}
			}
			if (stmtAlbum != null) {

				try {
					stmtAlbum.close();
				} catch (SQLException e) {
					log.error(e);
				}
			}
			if (stmtSong != null) {

				try {
					stmtSong.close();
				} catch (SQLException e) {
					log.error(e);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					log.error(e);
				}
			}
		}
	}

	public Collection<Album> load() throws AlbumException {

		Map<Integer, Album> albums = new HashMap<>();

		Connection con = null;
		PreparedStatement stmtAlbum = null;
		ResultSet rsAlbum = null;
		PreparedStatement stmtSong = null;
		ResultSet rsSong = null;

		try {
			con = getConnection();
			con.setAutoCommit(true);

			stmtAlbum = con.prepareStatement(
					"SELECT album.id AS id, artist.artist_name AS artist, album.album_title AS title, album.album_year AS yr FROM album JOIN artist ON album.artist_id = artist.id ORDER BY artist, yr");

			rsAlbum = stmtAlbum.executeQuery();

			while (rsAlbum.next()) {
				Album album = new Album();
				album.setArtist(rsAlbum.getString("artist"));
				album.setTitle(rsAlbum.getString("title"));
				album.setYear(rsAlbum.getInt("yr"));
				albums.put(rsAlbum.getInt("id"), album);
			}

			stmtSong = con.prepareStatement(
					"SELECT album_id, song_title AS title, track, disc FROM song ORDER BY album_id, disc, track");

			rsSong = stmtSong.executeQuery();

			while (rsSong.next()) {
				Song song = new Song();
				song.setTitle(rsSong.getString("title"));
				song.setTrack(rsSong.getInt("track"));
				song.setDisc(rsSong.getInt("disc"));

				albums.get(rsSong.getInt("album_id")).getSongs().add(song);
			}

			LinkedList<Album> result = new LinkedList<>(albums.values());

			Collections.sort(result, new Comparator<Album>() {
				@Override
				public int compare(Album o1, Album o2) {
					return o1.getArtist().toLowerCase().compareTo(o2.getArtist().toLowerCase()) == 0
							? o1.getYear() - o2.getYear()
							: o1.getArtist().toLowerCase().compareTo(o2.getArtist().toLowerCase());
				}
			});

			return result;

		} catch (SQLException e) {
			log.error(e);
			throw new DatabaseException(e);
		} finally {
			if (rsAlbum != null)
				try {
					rsAlbum.close();
				} catch (SQLException e) {
					log.error(e);
				}

			if (rsSong != null)
				try {
					rsSong.close();
				} catch (SQLException e) {
					log.error(e);
				}

			if (stmtAlbum != null)
				try {
					stmtAlbum.close();
				} catch (SQLException e) {
					log.error(e);
				}

			if (stmtSong != null)
				try {
					stmtSong.close();
				} catch (SQLException e) {
					log.error(e);
				}

			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					log.error(e);
				}
			}
		}
	}
}
