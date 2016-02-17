package dk.purplegreen.musiclibrary.tools;

public class Song {
	private String title;
	private int track;
	private int disc = 1;

	public Song() {
	}

	public Song(String title, int track) {
		this.title = title;
		this.track = track;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getTrack() {
		return track;
	}

	public void setTrack(int track) {
		this.track = track;
	}

	public int getDisc() {
		return disc;
	}

	public void setDisc(int disc) {
		this.disc = disc;
	}
}
