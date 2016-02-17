package dk.purplegreen.musiclibrary.tools;

import java.io.File;
import java.util.List;
import java.util.Properties;

public class DerbyExport {

	public static void main(String[] args) {
		try {
			new DerbyExport().exportDerby();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void exportDerby() throws Exception {
		Properties p = new Properties();
		p.load(DerbyExport.class.getResourceAsStream("/musiclibrarytools.properties"));

		List<Album> albums = new AlbumIO().loadDirectory(new File(p.getProperty("albumdir")));

		new AlbumDB().save(albums.iterator());
	}
}
