package dk.purplegreen.musiclibrary.tools;

import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;

public class AudioTagImport {

	private static final Logger log = LogManager.getLogger(AudioTagImport.class);

	public static void main(String[] args) {
		try {
			new AudioTagImport().importAudioTag();
		} catch (Exception e) {
			log.error(e);
		}
	}

	private Map<String, Album> albums = new HashMap<>();

	public void importAudioTag() throws Exception {
		Properties p = new Properties();
		p.load(AudioTagImport.class.getResourceAsStream("/musiclibrarytools.properties"));

		File rootDirectory = new File(p.getProperty("mp3dir"));

		if (rootDirectory.exists() && rootDirectory.isDirectory()) {
			processDirectory(rootDirectory);
		}

		File outDir = new File(p.getProperty("albumdir"));
		outDir.mkdirs();

		albums.forEach((key, album) -> album.getSongs().sort(Comparator.comparing(Song::getTrack)));

		new AlbumIO().save(new AlbumCollection(albums.values()), outDir);
	}

	public void processDirectory(File dir) throws Exception {

		File[] dirs = dir.listFiles(File::isDirectory);
		for (File subDir : dirs) {
			processDirectory(subDir);
		}

		// MP3s in this directory
		File[] mp3s = dir.listFiles(file -> file.getName().endsWith(".mp3"));
		for (File mp3File : mp3s) {
			Mp3File mp3 = new Mp3File(mp3File.getAbsolutePath());
			if (mp3.hasId3v2Tag()) {
				ID3v2 tag = mp3.getId3v2Tag();

				final String artist = tag.getArtist();
				final String title = tag.getAlbum();
				final String key = artist + title;
				final int year = Integer.parseInt(tag.getYear());

				Album album = albums.computeIfAbsent(key, k -> new Album(artist, title, year));

				Song song = new Song();
				song.setTitle(tag.getTitle());
				song.setTrack(Integer.valueOf(tag.getTrack()));
				song.setDisc(tag.getPartOfSet() == null ? 1 : Integer.parseInt(tag.getPartOfSet()));

				album.getSongs().add(song);

				log.info("Added song: {}", song);

			} else
				log.error("Missing ID3v2 tag in file: {}", mp3File);
		}
	}
}
