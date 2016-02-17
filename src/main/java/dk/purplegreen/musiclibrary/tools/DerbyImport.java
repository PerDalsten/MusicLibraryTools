package dk.purplegreen.musiclibrary.tools;

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

		// TODO
		// Read all albums from database and import as XML
	}
}
