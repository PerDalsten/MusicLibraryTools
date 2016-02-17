package dk.purplegreen.musiclibrary.tools;

public class AlbumException extends Exception {
		
	private static final long serialVersionUID = -5897672501541817859L;

	public AlbumException(String message) {
		super(message);
	}

	public AlbumException(Throwable cause) {
		super(cause);
	}

	public AlbumException(String message, Throwable cause) {
		super(message, cause);
	}
}
