package fr.gouret.music_player_android.model;


import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;

import fr.gouret.music_player_android.R;

/**
 * Represents a single audio file on the Android system.
 *
 * It's a simple data container, filled with setters/getters.
 *
 * Only mandatory fields are:
 * - id (which is a unique Android identified for a media file
 *       anywhere on the system)
 * - filePath (full path for the file on the filesystem).
 */
public class Song {

	private long id;
	private String photoPath;

	/**
	 * Creates a new Song, with specified `songID` and `filePath`.
	 *
	 * @note It's a unique Android identifier for a media file
	 *       anywhere on the system.
	 */
 
	public Song(long id, String filePath) {
		this.id        = id;
		this.photoPath  = filePath;
	}

    public Song( long id, String title, String artist) {
        this.artist = artist;
        this.title = title;
        this.id = id;
    }

    /**
	 * Identifier for the song on the Android system.
	 * (so we can locate the file anywhere)
	 */
	public long getId() {
		return id;
	}

	/**
	 * Full path for the music file within the filesystem.
	 */
	public String getFilePath() {
		return photoPath;
	}

	// optional metadata

	private String title       = "";
	private String artist      = "";
	private String album       = "";
	private int    year        = -1;
	private String genre       = "";
	private int    track_no    = -1;
	private long   duration_ms = -1;

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    private long   albumId = -1;



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


	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}


	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}


	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}


	public int getTrackNumber() {
		return track_no;
	}
	public void setTrackNumber(int track_no) {
		this.track_no = track_no;
	}

	/**
	 * Sets the duration of the song, in miliseconds.
	 */
	public void setDuration(long duration_ms) {
		this.duration_ms = duration_ms;
	}
	/**
	 * Returns the duration of the song, in miliseconds.
	 */
	public long getDuration() {
		return duration_ms;
	}
	public long getDurationSeconds() {
		return getDuration() / 1000;
	}
	public long getDurationMinutes() {
		return getDurationSeconds() / 60;
	}

    
    public Bitmap getImage(Context context) {
//        Long albumId = cursor.getLong(cursor
//                .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

        Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");
        Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);

        Log.d("TAG", albumArtUri.toString());
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(
                    context.getContentResolver(), albumArtUri);
           bitmap = Bitmap.createScaledBitmap(bitmap, 60, 60, true);

        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
            bitmap = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.notes);
        } catch (IOException e) {

            e.printStackTrace();
        } catch (NullPointerException e){
            bitmap = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.notes);
        }

        return bitmap;

    }
    
}
