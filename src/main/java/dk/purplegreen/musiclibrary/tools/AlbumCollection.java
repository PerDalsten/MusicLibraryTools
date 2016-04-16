package dk.purplegreen.musiclibrary.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "albums")
public class AlbumCollection implements Iterable<Album> {

	public AlbumCollection() {
	}

	public AlbumCollection(Collection<Album> albums) {
		this.albums.addAll(albums);
	}

	@XmlElement(name = "album")
	private List<Album> albums = new ArrayList<>();

	public List<Album> getAlbums() {
		return albums;
	}

	@Override
	public Iterator<Album> iterator() {
		return albums.iterator();
	}

	public void addAlbum(Album album) {
		getAlbums().add(album);
	}

	public void addCollection(AlbumCollection collection) {
		this.albums.addAll(collection.getAlbums());
	}
}
