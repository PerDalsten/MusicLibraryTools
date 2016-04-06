package dk.purplegreen.musiclibrary.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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
			jc = JAXBContext.newInstance(Album.class);
		} catch (JAXBException e) {
			log.error(e);
			throw new NullPointerException("Unable to create JAXBContext");
		}
	}

	public void save(Album album, File directory) throws IOException {

		try {
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			marshaller.marshal(album, os);

			byte[] xml = os.toByteArray();

			StringBuilder fileName = new StringBuilder(album.getArtist());
			fileName.append("_");
			fileName.append(album.getTitle());
			fileName.append(".xml");

			String file = fileName.toString();

			char[] illegalChars = new char[] { ':', '/', '\\', '?', '*', '<', '>', '|', '%', '#', '~', '&', '"' };
			for (char c : illegalChars) {
				file = file.replace(c, '_');
			}

			FileOutputStream fos = new FileOutputStream(new File(directory, file));
			fos.write(xml);
			fos.flush();
			fos.close();

		} catch (JAXBException e) {
			log.error(e);
			throw new IllegalArgumentException();
		}
	}

	public void save(Iterator<Album> albums, File directory) throws IOException {
		while (albums.hasNext()) {
			save(albums.next(), directory);
		}
	}

	public Album load(File albumFile) throws IOException {
		try {
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			Album album = (Album) unmarshaller.unmarshal(new FileInputStream(albumFile));
			return album;
		} catch (JAXBException e) {
			log.error(e);
			throw new IllegalArgumentException();
		}
	}

	public List<Album> loadDirectory(File directory) throws IOException {
		List<Album> albums = new ArrayList<>();

		File[] albumFiles = directory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return (name.endsWith(".xml"));
			}
		});

		for (File album : albumFiles) {
			albums.add(load(album));
		}
		return albums;
	}
}
