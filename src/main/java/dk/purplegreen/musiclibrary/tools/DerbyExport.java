package dk.purplegreen.musiclibrary.tools;

import java.io.File;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DerbyExport {

	private final static Logger log = LogManager.getLogger(DerbyExport.class);

	public static void main(String[] args) {
		try {
			new DerbyExport().exportDerby();

		} catch (Exception e) {
			log.error(e);
		}
	}

	public void exportDerby() throws Exception {
		Properties p = new Properties();
		p.load(DerbyExport.class.getResourceAsStream("/musiclibrarytools.properties"));

		AlbumCollection albums = new AlbumIO().loadDirectory(new File(p.getProperty("albumdir")));

		new AlbumDB().save(albums.iterator());
	}
}
