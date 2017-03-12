package dk.purplegreen.musiclibrary.tools;

import java.io.File;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MySQLExport {

	private final static Logger log = LogManager.getLogger(MySQLExport.class);

	static {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			log.error("Error initializing MySQLExport", e);
			throw new IllegalStateException("Unable to load database driver", e);
		}
	}

	public static void main(String[] args) {
		try {
			new MySQLExport().export();

		} catch (Exception e) {
			log.error(e);
		}
	}

	private void export() throws Exception {
		Properties p = new Properties();
		p.load(MySQLExport.class.getResourceAsStream("/musiclibrarytools.properties"));

		AlbumCollection albums = new AlbumIO().loadDirectory(new File(p.getProperty("albumdir")));

		new AlbumDB(p.getProperty("jdbc.url.mysql")).save(albums.iterator());
	}
}
