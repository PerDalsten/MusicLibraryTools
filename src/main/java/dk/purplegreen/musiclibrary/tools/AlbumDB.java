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
		PreparedStatement stmtAlbum = null;
		ResultSet rsAlbum = null;
		PreparedStatement stmtSong = null;

		try {
			con = getConnection();

			String sql = "INSERT INTO albums (album_artist, album_title, album_year) VALUES (?,?,?)";
			stmtAlbum = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			sql = "INSERT INTO songs (album_id, song_title, track, disc) VALUES (?,?,?,?)";
			stmtSong = con.prepareStatement(sql);

			while (albums.hasNext()) {

				Album album = albums.next();

				stmtAlbum.setString(1, album.getArtist());
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

			if (rsAlbum != null)
				try {
					rsAlbum.close();
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
					"SELECT id, album_artist AS artist, album_title AS title, album_year AS yr FROM albums ORDER BY artist, yr");

			rsAlbum = stmtAlbum.executeQuery();

			while (rsAlbum.next()) {
				Album album = new Album();
				album.setArtist(rsAlbum.getString("artist"));
				album.setTitle(rsAlbum.getString("title"));
				album.setYear(rsAlbum.getInt("yr"));
				albums.put(rsAlbum.getInt("id"), album);
			}

			stmtSong = con.prepareStatement(
					"SELECT album_id, song_title AS title, track, disc FROM songs ORDER BY album_id, disc, track");

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
