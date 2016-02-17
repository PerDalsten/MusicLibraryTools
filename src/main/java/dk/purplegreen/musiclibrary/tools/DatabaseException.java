package dk.purplegreen.musiclibrary.tools;

public class DatabaseException extends AlbumException {

	private static final long serialVersionUID = 4023774132745194562L;

	public DatabaseException(String message) {
		super(message);
	}

	public DatabaseException(Throwable cause) {
		super(cause);
	}

	public DatabaseException(String message, Throwable cause) {
		super(message, cause);
	}
}
