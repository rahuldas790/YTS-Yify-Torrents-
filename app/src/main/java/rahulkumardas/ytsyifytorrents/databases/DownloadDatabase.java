package rahulkumardas.ytsyifytorrents.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rahul Kumar Das on 27-05-2017.
 */

public class DownloadDatabase extends SQLiteOpenHelper {

    private final int VERSION = 1;
    private final String DATABASE_NAME = "Bookmarks";
    private final String TAG = "DownloadDatabase";

    private final String TABLE_NAME = "Downloads";
    private final String KEY_ID = "id";
    private final String KEY_URI = "uri";
    private final String KEY_TYPE = "type";

    public DownloadDatabase(Context context) {
        super(context, Environment.getExternalStorageDirectory()
                + File.separator + "Torrents"
                + File.separator + "Downloads", null, 1);

        Log.i(TAG, "Database Instantiated!");
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE " + TABLE_NAME + " (" + KEY_ID + " TEXT AUTO_INCREMENT PRIMARY KEY," + KEY_URI + " TEXT," + KEY_TYPE + " TEXT)";
        sqLiteDatabase.execSQL(query);
        Log.i(TAG, "Database Created!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String query = "DROP TABLE IF EXIST " + TABLE_NAME;
        sqLiteDatabase.execSQL(query);
        onCreate(sqLiteDatabase);
        Log.i(TAG, "Table recreated!");
    }

    public void insertNew(String uri, String type) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_URI, uri);
        values.put(KEY_TYPE, type);
        db.insert(TABLE_NAME, null, values);
        Log.i(TAG, "1 Row inserted!");
    }

    public List<String> allTorrents() {
        List<String> torr = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(false, TABLE_NAME, new String[]{KEY_URI}, KEY_TYPE + "=?", new String[]{"torrent"}, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                String uri = cursor.getString(0);
                torr.add(uri);
            } while (cursor.moveToNext());
        }

        return torr;
    }

    public List<String> allImages() {
        List<String> img = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(false, TABLE_NAME, new String[]{KEY_URI}, KEY_TYPE + "=?", new String[]{"image"}, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                String uri = cursor.getString(0);
                img.add(uri);
            } while (cursor.moveToNext());
        }

        return img;
    }

    public void removeEntry(String uri) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, KEY_URI + "=?", new String[]{uri});
        Log.i(TAG, "1 Item Deleted!");
    }
}
