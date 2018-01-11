package dk.purplegreen.musiclibrary.tools;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AlbumReport {

	private static final Logger log = LogManager.getLogger(AlbumReport.class);

	public static void main(String[] args) {
		try {
			new AlbumReport().report();

		} catch (Exception e) {
			log.error(e);
		}
	}

	private void report() throws Exception {

		Properties p = new Properties();
		p.load(DerbyExport.class.getResourceAsStream("/musiclibrarytools.properties"));

		List<Album> albums = new AlbumIO().loadDirectory(new File(p.getProperty("albumdir"))).getAlbums();

		System.out.println("Album count: " + albums.size());

		System.out.println("Song count: " + albums.stream().mapToInt(album -> album.getSongs().size()).sum());

		Map<Integer, Long> byYear = albums.stream()
				.collect(Collectors.groupingBy(Album::getYear, Collectors.counting()));

		System.out.println("\nAlbums by year:");
		byYear.entrySet().stream()
				.forEach(entry -> System.out.println("\t" + entry.getKey() + ": " + entry.getValue()));

		System.out.println("\nAlbums by artist:");

		Map<String, List<Album>> byArtist = albums.stream().collect(Collectors.groupingBy(Album::getArtist));

		byArtist.entrySet().stream().sorted(Map.Entry.comparingByKey())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1, LinkedHashMap::new))
				.entrySet().stream().forEach(entry -> {

					System.out.println(new StringJoiner("").add("\t").add(entry.getKey()).add(" [")
							.add(Integer.toString(entry.getValue().size())).add("]"));

					entry.getValue().stream().forEach(album -> System.out.println(
							String.join("", "\t\t", album.getTitle(), " [", Integer.toString(album.getYear()), "]")));
				});
	}
}
