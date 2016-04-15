package dk.purplegreen.musiclibrary.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

			FileOutputStream fos = new FileOutputStream(new File(directory, fileName.toString()));
			fos.write(xml);
			fos.flush();
			fos.close();

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

	public List<Album> loadDirectory(File directory) throws IOException {
		List<Album> albums = new ArrayList<>();

		File[] albumCollections = directory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return (name.endsWith(".xml"));
			}
		});

		for (File ac : albumCollections) {
			albums.addAll(load(ac).getAlbums());
		}
		return albums;
	}
}
