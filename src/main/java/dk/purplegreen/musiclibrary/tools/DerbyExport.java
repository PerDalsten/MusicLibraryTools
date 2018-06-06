package dk.purplegreen.musiclibrary.tools;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DerbyExport {

	private static final Logger log = LogManager.getLogger(DerbyExport.class);

	static {
		try {
			Class.forName("org.apache.derby.jdbc.ClientDriver");
		} catch (ClassNotFoundException e) {
			log.error("Error initializing AlbumDB", e);
			throw new IllegalStateException("Unable to load database driver", e);
		}
	}

	public static void main(String[] args) {
		try {
			new DerbyExport().export();

		} catch (Exception e) {
			log.error(e);
		}
	}

	private void export() throws IOException, AlbumException {
		Properties p = new Properties();
		p.load(DerbyExport.class.getResourceAsStream("/musiclibrarytools.properties"));

		AlbumCollection albums = new AlbumIO().loadDirectory(new File(p.getProperty("albumdir")));

		new AlbumDB(p.getProperty("jdbc.url.derby")).save(albums.iterator());
	}
}
