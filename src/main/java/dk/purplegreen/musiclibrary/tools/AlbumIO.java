package dk.purplegreen.musiclibrary.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AlbumIO {

	private static final Logger log = LogManager.getLogger(AlbumIO.class);

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

			String fileName = String.join("", "albums_",
					LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")), ".xml");

			try (FileOutputStream fos = new FileOutputStream(new File(directory, fileName))) {
				fos.write(xml);
				fos.flush();
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

		File[] albumCollections = directory.listFiles(file -> file.getName().endsWith(".xml"));

		for (File ac : albumCollections) {
			albums.addCollection(load(ac));
		}

		return albums;
	}
}
