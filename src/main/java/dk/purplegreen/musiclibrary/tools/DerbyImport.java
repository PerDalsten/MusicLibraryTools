package dk.purplegreen.musiclibrary.tools;

import java.io.File;
import java.util.Collection;
import java.util.Properties;

public class DerbyImport {

	public static void main(String[] args) {
		try {
			new DerbyImport().importDerby();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void importDerby() throws Exception {
		Properties p = new Properties();
		p.load(DerbyImport.class.getResourceAsStream("/musiclibrarytools.properties"));
		
		Collection<Album> albums = new AlbumDB().load();

		File outDir = new File(p.getProperty("albumdir"));
		outDir.mkdirs();
		new AlbumIO().save(albums.iterator(), outDir);
	}
}
