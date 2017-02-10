package dk.purplegreen.musiclibrary.tools;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "artist", "title", "year", "songs" })
public class Album {
	private String title;
	private String artist;
	private int year;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	@XmlElementWrapper(name = "songs")
	@XmlElement(name = "song")
	private List<Song> songs = new ArrayList<>();

	public List<Song> getSongs() {
		return songs;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("Title: ");
		result.append(title);
		result.append(", Artist: ");
		result.append(artist);
		result.append(", Year: ");
		result.append(year);

		return result.toString();
	}
}
