package dk.purplegreen.musiclibrary.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AlbumIO {

	private final static Logger log = LogManager.getLogger(AlbumIO.class);

	private final JAXBContext jc;

	public AlbumIO() {
		try {
			jc = JAXBContext.newInstance(AlbumCollection.class);
		} catch (JAXBException e) {
			log.error(e);
			throw new NullPointerException("Unable to create JAXBContext");
		}
	}

	public void save(AlbumCollection albums, File directory) throws IOException {

		try {
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			marshaller.marshal(albums, os);

			byte[] xml = os.toByteArray();

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");

			StringBuilder fileName = new StringBuilder("albums_");
			fileName.append(sdf.format(new Date()));
			fileName.append(".xml");

			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(new File(directory, fileName.toString()));
				fos.write(xml);
				fos.flush();
			} finally {
				if (fos != null)
					fos.close();
			}			
		} catch (JAXBException e) {
			log.error(e);
			throw new IllegalArgumentException();
		}
	}

	public AlbumCollection load(File albumFile) throws IOException {
		try {
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			AlbumCollection albums = (AlbumCollection) unmarshaller.unmarshal(new FileInputStream(albumFile));
			return albums;
		} catch (JAXBException e) {
			log.error(e);
			throw new IllegalArgumentException();
		}
	}

	public AlbumCollection loadDirectory(File directory) throws IOException {
		AlbumCollection albums = new AlbumCollection();

		File[] albumCollections = directory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".xml");
			}
		});

		for (File ac : albumCollections) {
			albums.addCollection(load(ac));
		}
		return albums;
	}
}
