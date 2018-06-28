package dk.purplegreen.musiclibrary.tools;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MySQLImport {

	private static final Logger log = LogManager.getLogger(MySQLImport.class);

	static {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			log.error("Error initializing AlbumDB", e);
			throw new IllegalStateException("Unable to load database driver", e);
		}
	}

	public static void main(String[] args) {
		try {
			new MySQLImport().importMySQL();

		} catch (Exception e) {
			log.error(e);
		}
	}

	public void importMySQL() throws IOException, AlbumException {
		Properties p = new Properties();
		p.load(MySQLImport.class.getResourceAsStream("/musiclibrarytools.properties"));

		Collection<Album> albums = new AlbumDB(p.getProperty("jdbc.url.mysql")).load();

		File outDir = new File(p.getProperty("albumdir"));
		outDir.mkdirs();

		new AlbumIO().save(new AlbumCollection(albums), outDir);
	}
}
