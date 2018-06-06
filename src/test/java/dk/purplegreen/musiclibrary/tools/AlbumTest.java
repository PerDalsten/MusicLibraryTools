package dk.purplegreen.musiclibrary.tools;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

public class AlbumTest {

	private final static Logger log = LogManager.getLogger(AlbumTest.class);

	private static JAXBContext jc;

	@BeforeClass
	public static void setUp() throws Exception {
		jc = JAXBContext.newInstance(AlbumCollection.class);
	}

	@Test
	public void testRoundTrip() throws Exception {

		Album album = new Album("Aerosmith", "Draw the Line", 1977);

		String[] songs = { "Draw the Line", "I Wanna Know Why", "Critical Mass", "Get It Up", "Bright Light Fright",
				"Kings and Queens", "The Hand That Feeds", "Sight for Sore Eyes", "Milk Cow Blues" };

		for (int i = 0; i < songs.length; i++) {
			album.getSongs().add(new Song(songs[i], i + 1));
		}

		AlbumCollection albums = new AlbumCollection();
		albums.addAlbum(album);

		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		marshaller.marshal(albums, os);

		log.debug("Albums xml: " + os.toString());

		Unmarshaller unmarshaller = jc.createUnmarshaller();
		albums = (AlbumCollection) unmarshaller.unmarshal(new ByteArrayInputStream(os.toByteArray()));

		album = albums.getAlbums().get(0);

		assertEquals("Wrong artist", "Aerosmith", album.getArtist());
		assertEquals("Wrong title", "Draw the Line", album.getTitle());
		assertEquals("Wrong year", 1977, album.getYear());

		assertEquals("Wrong number of songs", songs.length, album.getSongs().size());

		for (Song song : album.getSongs()) {
			assertEquals("Wrong song title", songs[song.getTrack() - 1], song.getTitle());
		}
	}

	@Test
	public void testAlbumToString() {
		Album album = new Album("Aerosmith", "Draw the Line", 1977);

		assertEquals("Wrong toString", "Title: Draw the Line, Artist: Aerosmith, Year: 1977", album.toString());
	}

	@Test
	public void testSongToString() {

		Song song = new Song("Milk Cow Blues", 9);

		assertEquals("Wrong toString", "Title: Milk Cow Blues, Track: 9, Disc: 1", song.toString());
	}
}
