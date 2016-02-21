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
		jc = JAXBContext.newInstance(Album.class);
	}

	@Test
	public void testRoundTrip() throws Exception {

		Album album = new Album();
		album.setArtist("Aerosmith");
		album.setTitle("Draw the Line");
		album.setYear(1977);

		String[] songs = { "Draw the Line", "I Wanna Know Why", "Critical Mass", "Get It Up", "Bright Light Fright",
				"Kings and Queens", "The Hand That Feeds", "Sight for Sore Eyes", "Milk Cow Blues" };

		for (int i = 0; i < songs.length; i++) {
			album.getSongs().add(new Song(songs[i], i + 1));
		}

		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		marshaller.marshal(album, os);

		log.debug("Album xml: "+os.toString());

		Unmarshaller unmarshaller = jc.createUnmarshaller();
		album = (Album) unmarshaller.unmarshal(new ByteArrayInputStream(os.toByteArray()));

		assertEquals("Wrong artist", "Aerosmith", album.getArtist());
		assertEquals("Wrong title", "Draw the Line", album.getTitle());
		assertEquals("Wrong year", 1977, album.getYear());

		assertEquals("Wrong number of songs", songs.length, album.getSongs().size());

		for (Song song : album.getSongs()) {
			assertEquals("Wrong song title", songs[song.getTrack() - 1], song.getTitle());
		}
	}
}
