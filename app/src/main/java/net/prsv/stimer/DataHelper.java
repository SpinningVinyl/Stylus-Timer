package net.prsv.stimer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DataHelper extends SQLiteOpenHelper {


    public static final String PROFILE_TABLE = "PROFILE_TABLE";
    public static final String PROFILE_ID = "PROFILE_ID";
    public static final String PROFILE_NAME = "PROFILE_NAME";
    public static final String PROFILE_THRESHOLD = "THRESHOLD";

    public static final String STYLUS_TABLE = "STYLUS_TABLE";
    public static final String STYLUS_NAME = "STYLUS_NAME";
    public static final String STYLUS_ID = "STYLUS_ID";
    public static final String STYLUS_PROFILE = "STYLUS_PROFILE";
    public static final String STYLUS_TRACKING_FORCE = "STYLUS_TRACKING_FORCE";
    public static final String STYLUS_HOURS = "STYLUS_HOURS";
    public static final String STYLUS_CUSTOM_THRESHOLD = "STYLUS_CUSTOM_THRESHOLD";

    public DataHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
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
        String createProfileTableStatement = "CREATE TABLE " + PROFILE_TABLE + " (" + PROFILE_ID + " INTEGER PRIMARY KEY, " + PROFILE_NAME + " TEXT, " + PROFILE_THRESHOLD + " INTEGER);";
        String createStylusTableStatement = "CREATE TABLE " + STYLUS_TABLE + " (" + STYLUS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + STYLUS_NAME + " TEXT, " + STYLUS_PROFILE + " INTEGER NOT NULL, " + STYLUS_TRACKING_FORCE + " FLOAT, " + STYLUS_HOURS + " FLOAT, " + STYLUS_CUSTOM_THRESHOLD + " INTEGER, FOREIGN KEY(STYLUS_PROFILE) REFERENCES " + PROFILE_TABLE + "(" + PROFILE_ID + "));";
        db.execSQL(createProfileTableStatement);
        db.execSQL(createStylusTableStatement);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
