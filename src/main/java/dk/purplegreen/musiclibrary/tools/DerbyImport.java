package dk.purplegreen.musiclibrary.tools;

import java.io.File;
import java.util.Collection;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DerbyImport {

	private final static Logger log = LogManager.getLogger(DerbyImport.class);

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
			new DerbyImport().importDerby();

		} catch (Exception e) {
			log.error(e);
		}
	}

	public void importDerby() throws Exception {
		Properties p = new Properties();
		p.load(DerbyImport.class.getResourceAsStream("/musiclibrarytools.properties"));

		Collection<Album> albums = new AlbumDB(p.getProperty("jdbc.url.derby")).load();

		File outDir = new File(p.getProperty("albumdir"));
		outDir.mkdirs();

		new AlbumIO().save(new AlbumCollection(albums), outDir);
	}
}
