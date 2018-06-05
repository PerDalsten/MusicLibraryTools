package dk.purplegreen.musiclibrary.tools;

public class MP3Song {
	private String artist;
	private String album;
	private Integer year;
	private String title;
	private Integer track;
	private Integer disc;

	public String getArtist() {
		return artist;
	}

	public String getAlbum() {
		return album;
	}

	public Integer getYear() {
		return year;
	}

	public String getTitle() {
		return title;
	}

	public Integer getTrack() {
		return track;
	}

	public Integer getDisc() {
		return disc;
	}

	public MP3Song(String artist, String album, String year, String title, String track, String disc) {
		this.artist = artist;
		this.album = album;
		this.year = Integer.valueOf(year);
		this.title = title;
		this.track = Integer.valueOf(track);
		if (disc == null)
			this.disc = 1;
		else
			this.disc = Integer.valueOf(disc);
	}
}
