package dk.purplegreen.musiclibrary.tools;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class PMAXMLImport {

	private final static Logger log = LogManager.getLogger(PMAXMLImport.class);

	public static void main(String[] args) {
		try {
			new PMAXMLImport().importPMAXML();

		} catch (Exception e) {
			log.error(e);
		}
	}

	public void importPMAXML() throws IOException, ParserConfigurationException, SAXException {
		Properties p = new Properties();
		p.load(PMAXMLImport.class.getResourceAsStream("/musiclibrarytools.properties"));

		Map<String, Album> albums = new HashMap<>();

		DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = df.newDocumentBuilder();

		// ALBUMS
		Document doc = builder.parse(new File(new File(p.getProperty("pmaxmldir")), "music.xml"));
		NodeList nodes = doc.getElementsByTagName("table");

		for (int i = 0; i < nodes.getLength(); i++) {

			NodeList values = nodes.item(i).getChildNodes();

			Album album = new Album();
			String id = null;

			for (int j = 0; j < values.getLength(); j++) {
				Node n = values.item(j);
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) n;

					switch (e.getAttribute("name")) {
					case "id":
						id = e.getFirstChild().getNodeValue().trim();
						break;
					case "title":
						album.setTitle(e.getFirstChild().getNodeValue().trim());
						break;
					case "artist":
						album.setArtist(e.getFirstChild().getNodeValue().trim());
						break;
					case "year":
						album.setYear(Integer.valueOf(e.getFirstChild().getNodeValue().trim()));
						break;
					default:
						break;
					}
				}
			}

			albums.put(id, album);
		}

		// SONGS
		doc = builder.parse(new File(new File(p.getProperty("pmaxmldir")), "songs.xml"));
		nodes = doc.getElementsByTagName("table");

		for (int i = 0; i < nodes.getLength(); i++) {
			NodeList values = nodes.item(i).getChildNodes();

			Song song = new Song();
			String albumId = null;

			for (int j = 0; j < values.getLength(); j++) {
				Node n = values.item(j);
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) n;
					switch (e.getAttribute("name")) {

					case "music_id":
						albumId = e.getFirstChild().getNodeValue().trim();
						break;
					case "title":
						song.setTitle(e.getFirstChild().getNodeValue().trim());
						break;
					case "track":
						song.setTrack(Integer.valueOf(e.getFirstChild().getNodeValue().trim()));
						break;
					case "part":
						song.setDisc(Integer.valueOf(e.getFirstChild().getNodeValue().trim()));
						break;
					default:
						break;
					}
				}
			}

			albums.get(albumId).getSongs().add(song);
		}

		new AlbumIO().save(albums.values().iterator(), new File(p.getProperty("albumdir")));
	}

}
