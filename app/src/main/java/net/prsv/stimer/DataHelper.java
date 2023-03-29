package net.prsv.stimer;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DataHelper extends SQLiteOpenHelper {

    public static final String DB_FILE = "styli.db";

    public static final String PROFILE_TABLE = "PROFILE_TABLE";
    public static final String COLUMN_PROFILE_ID = "PROFILE_ID";
    public static final String COLUMN_PROFILE_NAME = "PROFILE_NAME";
    public static final String COLUMN_PROFILE_THRESHOLD = "THRESHOLD";

    public static final String STYLUS_TABLE = "STYLUS_TABLE";
    public static final String COLUMN_STYLUS_NAME = "STYLUS_NAME";
    public static final String COLUMN_STYLUS_ID = "STYLUS_ID";
    public static final String COLUMN_STYLUS_PROFILE = "STYLUS_PROFILE";
    public static final String COLUMN_STYLUS_TF = "STYLUS_TRACKING_FORCE";
    public static final String COLUMN_STYLUS_HOURS = "STYLUS_HOURS";
    public static final String COLUMN_STYLUS_CUSTOM_THRESHOLD = "STYLUS_CUSTOM_THRESHOLD";

    public DataHelper(@Nullable SQLiteDatabase.CursorFactory factory) {
        super(STimerApp.getContext(), DB_FILE, factory, 1);
    }


    public Stylus getStylusById(int id) {
        return new Stylus(id, "dummy", 0, 0, 0);
    }

    public ArrayList<Stylus> getAllStyli() {
        ArrayList<Stylus> result = new ArrayList<>();
        return result;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createProfileTableStatement = "CREATE TABLE " + PROFILE_TABLE + " (" + COLUMN_PROFILE_ID + " INTEGER PRIMARY KEY, " + COLUMN_PROFILE_NAME + " TEXT, " + COLUMN_PROFILE_THRESHOLD + " INTEGER);";
        String createStylusTableStatement = "CREATE TABLE " + STYLUS_TABLE + " (" + COLUMN_STYLUS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_STYLUS_NAME + " TEXT, " + COLUMN_STYLUS_PROFILE + " INTEGER NOT NULL, " + COLUMN_STYLUS_TF + " FLOAT, " + COLUMN_STYLUS_HOURS + " FLOAT, " + COLUMN_STYLUS_CUSTOM_THRESHOLD + " INTEGER, FOREIGN KEY(STYLUS_PROFILE) REFERENCES " + PROFILE_TABLE + "(" + COLUMN_PROFILE_ID + "));";
        db.execSQL(createProfileTableStatement);
        db.execSQL(createStylusTableStatement);

    }

    public int insertStylus(Stylus stylus) {
        assert stylus != null;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_STYLUS_NAME, stylus.getName());
        cv.put(COLUMN_STYLUS_PROFILE, stylus.getProfileId());
        cv.put(COLUMN_STYLUS_TF, stylus.getTrackingForce());
        cv.put(COLUMN_STYLUS_CUSTOM_THRESHOLD, stylus.getCustomThreshold());
        cv.put(COLUMN_STYLUS_HOURS, 0); // hours is always 0 for a new stylus
        return (int) db.insert(STYLUS_TABLE, null, cv);
    }

    public void updateStylus(Stylus stylus) {
        assert stylus != null;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_STYLUS_NAME, stylus.getName());
        cv.put(COLUMN_STYLUS_PROFILE, stylus.getProfileId());
        cv.put(COLUMN_STYLUS_TF, stylus.getTrackingForce());
        cv.put(COLUMN_STYLUS_CUSTOM_THRESHOLD, stylus.getCustomThreshold());
        cv.put(COLUMN_STYLUS_HOURS, stylus.getHours());
        db.update(STYLUS_TABLE, cv, "? = ?", new String[]{COLUMN_STYLUS_ID, String.valueOf(stylus.getId())});
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
