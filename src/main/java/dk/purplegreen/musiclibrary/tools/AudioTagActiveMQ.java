package dk.purplegreen.musiclibrary.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.StringJoiner;
import java.util.stream.Stream;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

public class AudioTagActiveMQ {

	private static final Logger log = LogManager.getLogger(AudioTagActiveMQ.class);

	private ConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);

	private Connection connection;

	private Properties props = new Properties();

	public static void main(String[] args) {
		try {
			new AudioTagActiveMQ().importAudioTag();
		} catch (Exception e) {
			log.error(e);
		}
	}

	public AudioTagActiveMQ() {
		try {
			props.load(AudioTagImport.class.getResourceAsStream("/musiclibrarytools.properties"));
		} catch (IOException e) {
			log.error("Error loading properties", e);
		}
	}

	public void importAudioTag() throws Exception {

		try {
			connection = factory.createConnection();
			connection.start();

			try (Stream<Path> mp3Files = Files.walk(Paths.get(props.getProperty("mp3dir")))) {
				mp3Files.filter(Files::isRegularFile).filter(f -> f.getFileName().toString().endsWith("mp3"))
						.forEach(this::processFile);
			}
		} finally {
			connection.close();
		}
	}

	private void processFile(Path mp3File) {
		try {
			Mp3File mp3 = new Mp3File(mp3File.toAbsolutePath().toString());

			if (mp3.hasId3v2Tag()) {
				ID3v2 tag = mp3.getId3v2Tag();

				StringJoiner result = new StringJoiner(",", "{", "}");

				result.add(String.format("\"artist\": \"%s\"", tag.getArtist()));
				result.add(String.format("\"album\": \"%s\"", tag.getAlbum()));
				result.add(String.format("\"year\": %d", Integer.valueOf(tag.getYear())));
				result.add(String.format("\"title\": \"%s\"", tag.getTitle()));
				result.add(String.format("\"track\": %d", Integer.valueOf(tag.getTrack())));
				result.add(String.format("\"disc\": %d",
						tag.getPartOfSet() == null ? 1 : Integer.valueOf(tag.getPartOfSet())));

				log.info("Submitting song: {}", result.toString());

				Session session = null;
				MessageProducer producer = null;

				try {

					session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
					producer = session.createProducer(session.createQueue(props.getProperty("activemq.destination")));

					TextMessage message = session.createTextMessage();
					message.setText(result.toString());
					producer.send(message);

				} catch (JMSException e) {
					log.error("Exception sending MP3 file: {}", mp3File, e);
				} finally {
					if (producer != null) {
						try {
							producer.close();
						} catch (JMSException e) {
							log.error(e);
						}
					}

					if (session != null) {
						try {
							session.close();
						} catch (JMSException e) {
							log.error(e);
						}
					}
				}
			} else
				log.error("Missing ID3v2 tag in file: {}", mp3File);
		} catch (UnsupportedTagException | InvalidDataException | IOException e) {
			log.error("Exception processing MP3 file: {}", mp3File, e);
		}
	}
}
