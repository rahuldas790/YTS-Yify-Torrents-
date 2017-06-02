package rahulkumardas.ytsyifytorrents.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rahulkumardas.ytsyifytorrents.models.Movie;
import rahulkumardas.ytsyifytorrents.models.Torrent;

/**
 * Created by Rahul Kumar Das on 25-05-2017.
 */

public class BookmarkDatabase extends SQLiteOpenHelper {

    private final String TAG = "BookmarkDatabase";
    private Context context;
    private final String DATABASE_NAME = "Bookmarks";
    private final int VERSION = 1;

    private final String TABLE_NAME = "Bookmark";
    private final String MOVIE_ID = "id";
    private final String MOVIE_NAME = "name";
    private final String IMAGE_URL = "url";
    private final String BG_IMAGE_URL = "bg_url";
    private final String GENRE = "genre";
    private final String TRAILER_CODE = "yt_code";
    private final String SYNOPSIS = "synopsis";
    private final String IMDB_CODE = "imdb_code";
    private final String LANGUAGE = "lang";
    private final String RATING = "rating";
    private final String YEAR = "year";
    private final String LENGTH = "length";

    private final String LOW_TORR_SIZE = "low_size";
    private final String LOW_TORR_URL = "low_url";
    private final String LOW_TORR_SEEDS = "low_seeds";
    private final String LOW_TORR_PEERS = "low_peers";
    private final String LOW_TORR_HASH = "low_hash";
    private final String LOW_TORR_QUALITY = "low_quality";

    private final String HIGH_TORR_SIZE = "high_size";
    private final String HIGH_TORR_URL = "high_url";
    private final String HIGH_TORR_SEEDS = "high_seeds";
    private final String HIGH_TORR_PEERS = "high_peers";
    private final String HIGH_TORR_HASH = "high_hash";
    private final String HIGH_TORR_QUALITY = "high_quality";

    private final String THREE_TORR_SIZE = "three_size";
    private final String THREE_TORR_URL = "three_url";
    private final String THREE_TORR_SEEDS = "three_seeds";
    private final String THREE_TORR_PEERS = "three_peers";
    private final String THREE_TORR_HASH = "three_hash";
    private final String THREE_TORR_QUALITY = "three_quality";

    public BookmarkDatabase(Context context) {
        super(context, Environment.getExternalStorageDirectory()
                + File.separator + "Torrents"
                + File.separator + "Bookmarks", null, 1);
        Log.i("Database Handler", "Instanciated");

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE " + TABLE_NAME + " (" + MOVIE_ID + " TEXT," + MOVIE_NAME + " TEXT," + IMAGE_URL + " TEXT," + BG_IMAGE_URL + " TEXT,"
                + GENRE + " TEXT," + TRAILER_CODE + " TEXT," + SYNOPSIS + " TEXT," + IMDB_CODE + " TEXT," + LANGUAGE + " TEXT," + RATING + " TEXT," + YEAR + " TEXT," + LENGTH + " TEXT,"
                + LOW_TORR_SIZE + " TEXT," + LOW_TORR_HASH + " TEXT," + LOW_TORR_QUALITY + " TEXT," + LOW_TORR_SEEDS + " TEXT," + LOW_TORR_PEERS + " TEXT," + LOW_TORR_URL + " TEXT,"
                + HIGH_TORR_SIZE + " TEXT," + HIGH_TORR_HASH + " TEXT," + HIGH_TORR_QUALITY + " TEXT," + HIGH_TORR_SEEDS + " TEXT," + HIGH_TORR_PEERS + " TEXT," + HIGH_TORR_URL + " TEXT,"
                + THREE_TORR_SIZE + " TEXT," + THREE_TORR_HASH + " TEXT," + THREE_TORR_QUALITY + " TEXT," + THREE_TORR_SEEDS + " TEXT," + THREE_TORR_PEERS + " TEXT," + THREE_TORR_URL + " TEXT)";

        sqLiteDatabase.execSQL(query);
        Log.i(TAG, "Table Created Successfully");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String query = "DROP TABLE IF EXIST " + TABLE_NAME;
        sqLiteDatabase.execSQL(query);
        onCreate(sqLiteDatabase);
    }

    public boolean existMOVIE(String movieId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(true, TABLE_NAME, new String[]{MOVIE_ID}, MOVIE_ID + "=?", new String[]{movieId}, null, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.close();
            db.close();
            Log.i(TAG, "1 record found!");
            return true;
        }
        cursor.close();
        db.close();
        Log.i(TAG, "No record found!");
        return false;
    }

    public void insertMovie(Movie movie) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MOVIE_ID, movie.id + "");
        values.put(MOVIE_NAME, movie.movieName + "");
        values.put(IMAGE_URL, movie.imageUrl + "");
        values.put(BG_IMAGE_URL, movie.bgImageUrl + "");
        values.put(GENRE, movie.genre + "");
        values.put(TRAILER_CODE, movie.trailerCode + "");
        values.put(SYNOPSIS, movie.synopsis + "");
        values.put(IMDB_CODE, movie.imdbCode + "");
        values.put(LANGUAGE, movie.language + "");
        values.put(RATING, movie.rating + "");
        values.put(YEAR, movie.year + "");
        values.put(LENGTH, movie.length + "");

        List<Torrent> list = movie.list;
        for (int i = 0; i < list.size(); i++) {
            Torrent torrent = list.get(i);
            if (torrent.quality.equals("720p")) {
                values.put(LOW_TORR_QUALITY, torrent.quality + "");
                values.put(LOW_TORR_URL, torrent.torUrl + "");
                values.put(LOW_TORR_SIZE, torrent.size + "");
                values.put(LOW_TORR_SEEDS, torrent.seeds + "");
                values.put(LOW_TORR_PEERS, torrent.peers + "");
                values.put(LOW_TORR_HASH, torrent.hash + "");
            } else if (torrent.quality.equals("1080p")) {
                values.put(HIGH_TORR_QUALITY, torrent.quality);
                values.put(HIGH_TORR_URL, torrent.torUrl + "");
                values.put(HIGH_TORR_SIZE, torrent.size + "");
                values.put(HIGH_TORR_SEEDS, torrent.seeds + "");
                values.put(HIGH_TORR_PEERS, torrent.peers + "");
                values.put(HIGH_TORR_HASH, torrent.hash + "");
            } else {
                values.put(THREE_TORR_QUALITY, torrent.quality);
                values.put(THREE_TORR_URL, torrent.torUrl + "");
                values.put(THREE_TORR_SIZE, torrent.size + "");
                values.put(THREE_TORR_SEEDS, torrent.seeds + "");
                values.put(THREE_TORR_PEERS, torrent.peers + "");
                values.put(THREE_TORR_HASH, torrent.hash + "");
            }
        }

        db.insert(TABLE_NAME, null, values);
        db.close();
        Log.i(TAG, "1 Row inserted successfully!");
    }

    public void deleteMovie(String movieId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, MOVIE_ID + "=?", new String[]{movieId});
        db.close();
        Log.i(TAG, "1 Row deleted!");
    }

    public List<Movie> getAllMovies() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        List<Movie> list = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Movie movie = new Movie();
                movie.id = Long.valueOf(cursor.getString(0));
                movie.movieName = cursor.getString(1);
                movie.imageUrl = cursor.getString(2);
                movie.bgImageUrl = cursor.getString(3);
                movie.genre = cursor.getString(4);
                movie.trailerCode = cursor.getString(5);
                movie.synopsis = cursor.getString(6);
                movie.imdbCode = cursor.getString(7);
                movie.language = cursor.getString(8);
                movie.rating = Float.valueOf(cursor.getString(9));
                movie.year = Integer.valueOf(cursor.getString(10));
                movie.length = Integer.valueOf(cursor.getString(11));

                String url, hash, size, seeds, peers, quality;
                size = cursor.getString(12);
                hash = cursor.getString(13);
                quality = cursor.getString(14);
                seeds = cursor.getString(15);
                peers = cursor.getString(16);
                url = cursor.getString(17);
                Torrent torrent = new Torrent(url, hash, quality, size, Long.valueOf(seeds), Long.valueOf(peers));
                List<Torrent> torrents = new ArrayList<>();
                torrents.add(torrent);
                if (cursor.getString(18) != null) {
                    size = cursor.getString(18);
                    hash = cursor.getString(19);
                    quality = cursor.getString(20);
                    seeds = cursor.getString(21);
                    peers = cursor.getString(22);
                    url = cursor.getString(23);
                    torrent = new Torrent(url, hash, quality, size, Long.valueOf(seeds), Long.valueOf(peers));
                    torrents.add(torrent);
                }

                if (cursor.getString(24) != null) {
                    size = cursor.getString(24);
                    hash = cursor.getString(25);
                    quality = cursor.getString(26);
                    seeds = cursor.getString(27);
                    peers = cursor.getString(28);
                    url = cursor.getString(29);
                    torrent = new Torrent(url, hash, quality, size, Long.valueOf(seeds), Long.valueOf(peers));
                    torrents.add(torrent);
                }
                movie.list = torrents;

                list.add(movie);

            } while (cursor.moveToNext());
        }

        return list;
    }
}
