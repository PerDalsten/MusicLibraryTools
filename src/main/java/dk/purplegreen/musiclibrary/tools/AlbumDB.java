package dk.purplegreen.musiclibrary.tools;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;

public class AlbumDB {

	static {
		try {
			Class.forName("org.apache.derby.jdbc.ClientDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
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

			if (con != null) {
				try {
					con.rollback();
				} catch (SQLException re) {
					re.printStackTrace();
				}
			}

			e.printStackTrace();
			throw new DatabaseException(e);
		} finally {

			if (rsAlbum != null)
				try {
					rsAlbum.close();
				} catch (SQLException e) {
				}

			if (stmtAlbum != null)
				try {
					stmtAlbum.close();
				} catch (SQLException e) {
				}

			if (stmtSong != null)
				try {
					stmtSong.close();
				} catch (SQLException e) {
				}

			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
