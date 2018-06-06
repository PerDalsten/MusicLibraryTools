package dk.purplegreen.musiclibrary.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;

public class AlbumDBTest {

	@Rule
	public Database database = new Database();

	@Test
	public void testSaveAlbum() throws Exception {

		Album album = new Album("Rolling Stones", "Beggars Banquet", 1968);
		album.getSongs().add(new Song("Sympathy for the Devil", 1));
		album.getSongs().add(new Song("No Expectatopns", 2));
		album.getSongs().add(new Song("Dear Doctor", 3));

		new AlbumDB(database.getConnectionURL()).save(album);

		try (Connection con = DriverManager.getConnection(database.getConnectionURL())) {

			try (PreparedStatement stmt = con
					.prepareStatement("SELECT * FROM artist WHERE artist_name='Rolling Stones'");
					ResultSet rs = stmt.executeQuery()) {

				assertTrue("Artist not created", rs.next());
			}

			Integer id;
			try (PreparedStatement stmt = con
					.prepareStatement("SELECT * FROM album WHERE album_title='Beggars Banquet'");
					ResultSet rs = stmt.executeQuery()) {

				assertTrue("Album not created", rs.next());
				id = rs.getInt("id");
			}

			try (PreparedStatement stmt = con.prepareStatement("SELECT COUNT(*) FROM song WHERE album_id=" + id);
					ResultSet rs = stmt.executeQuery()) {

				assertTrue("Songs not created", rs.next());
				assertEquals("Wrong number of songs", 3, rs.getInt(1));
			}
		}
	}

	@Test
	public void loadAlbums() throws Exception {

		Collection<Album> albums = new AlbumDB(database.getConnectionURL()).load();

		assertEquals("Wrong number of albums", 3, albums.size());

		assertEquals("Wrong sort", "AC/DC", albums.iterator().next().getArtist());

		assertEquals("Wrong number of songs", 2, albums.iterator().next().getSongs().size());

	}

}
