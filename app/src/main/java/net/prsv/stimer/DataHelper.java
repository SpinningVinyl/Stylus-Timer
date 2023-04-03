package net.prsv.stimer;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * The {@link DataHelper} class contains methods for storing {@link Stylus} and
 * {@link StylusProfile} objects in the database and retrieving them from the local
 * SQLite database.
 */
public class DataHelper extends SQLiteOpenHelper {

    public static final String DB_FILE = "styli.db";
    public static final String TEST_DB_FILE = "test.db";

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

    /**
     * Creates a new {@link DataHelper} object.
     */
    public DataHelper() {
        super(STimerApp.getContext(), DB_FILE, null, 1);
    }

    public DataHelper(String dbFile) {
        super(STimerApp.getContext(), dbFile, null, 1);
    }

    /**
     * Retrieves a single {@link Stylus} object from the database.
     * @param id stylus ID
     * @return the stylus object retrieved from the database
     */
    public Stylus getStylus(int id) {
        String query = "SELECT * FROM " + STYLUS_TABLE + " WHERE " + COLUMN_STYLUS_ID + " = ? ;";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            String name = cursor.getString(1);
            int profileId = cursor.getInt(2);
            double trackingForce = cursor.getDouble(3);
            double hours = cursor.getDouble(4);
            int customThreshold = cursor.getInt(5);
            Stylus stylus = new Stylus(id, name, profileId, trackingForce, customThreshold);
            stylus.setHours(hours);
            cursor.close();
            return stylus;
        }

        return null;
    }

    /**
     * Returns all {@link Stylus} objects stored in the local database
     * @return an {@link ArrayList} containing all styli stored in the database
     */
    public ArrayList<Stylus> getAllStyli() {
        ArrayList<Stylus> result = new ArrayList<>();
        String query = "SELECT * FROM " + STYLUS_TABLE + " ORDER BY " + COLUMN_STYLUS_ID + " ASC;";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                int profileId = cursor.getInt(2);
                double trackingForce = cursor.getDouble(3);
                double hours = cursor.getDouble(4);
                int customThreshold = cursor.getInt(5);
                Stylus stylus = new Stylus(id, name, profileId, trackingForce, customThreshold);
                stylus.setHours(hours);
                result.add(stylus);
            } while(cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    /**
     * Returns all {@link StylusProfile} objects stored in the local database
     * @return an {@link ArrayList} containing all stylus profiles stored in the database
     */
    public ArrayList<StylusProfile> getAllProfiles() {
        ArrayList<StylusProfile> result = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + PROFILE_TABLE + " ORDER BY " + COLUMN_PROFILE_ID + " ASC;";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                int threshold = cursor.getInt(2);
                StylusProfile profile = new StylusProfile(id, name, threshold);
                result.add(profile);
            } while (cursor.moveToNext());

        }
        cursor.close();
        return result;
    }

    /**
     * Retrieves a single {@link StylusProfile} object from the database.
     * @param id profile ID
     * @return {@link StylusProfile} object retrieved from the database
     */
    public StylusProfile getProfile(int id) {
        String query = "SELECT * FROM " + PROFILE_TABLE + " WHERE " + COLUMN_PROFILE_ID + " = ? ;";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            String name = cursor.getString(1);
            int threshold = cursor.getInt(2);
            cursor.close();
            return new StylusProfile(id, name, threshold);
        }
        return null;
    }

    /**
     * Creates a new DB record containing a {@link StylusProfile} object.
     * @param profile the {@link StylusProfile} object to store in the database
     */
    public void insertProfile(StylusProfile profile) {
        assert profile != null;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PROFILE_ID, profile.getId());
        cv.put(COLUMN_PROFILE_NAME, profile.getName());
        cv.put(COLUMN_PROFILE_THRESHOLD, profile.getThreshold());
        db.insert(PROFILE_TABLE, null, cv);
    }

    /**
     * Called when the database is accessed for the first time.
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createProfileTableStatement = "CREATE TABLE " + PROFILE_TABLE + " (" + COLUMN_PROFILE_ID + " INTEGER PRIMARY KEY, " + COLUMN_PROFILE_NAME + " TEXT, " + COLUMN_PROFILE_THRESHOLD + " INTEGER);";
        String createStylusTableStatement = "CREATE TABLE " + STYLUS_TABLE + " (" + COLUMN_STYLUS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_STYLUS_NAME + " TEXT, " + COLUMN_STYLUS_PROFILE + " INTEGER NOT NULL, " + COLUMN_STYLUS_TF + " FLOAT, " + COLUMN_STYLUS_HOURS + " FLOAT, " + COLUMN_STYLUS_CUSTOM_THRESHOLD + " INTEGER, FOREIGN KEY(" + COLUMN_STYLUS_PROFILE + ") REFERENCES " + PROFILE_TABLE + "(" + COLUMN_PROFILE_ID + "));";
        db.execSQL(createProfileTableStatement);
        db.execSQL(createStylusTableStatement);

    }

    /**
     * Creates a new DB record containing a {@link Stylus} object.
     * @param stylus {@link Stylus} object to be saved in the database
     * @return the ID (primary key) of the saved record
     */
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

    /**
     * Updates the DB record containing {@code stylus}
     * @param stylus {@link Stylus} object to be updated
     */
    public void updateStylus(Stylus stylus) {
        assert stylus != null;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_STYLUS_NAME, stylus.getName());
        cv.put(COLUMN_STYLUS_PROFILE, stylus.getProfileId());
        cv.put(COLUMN_STYLUS_TF, stylus.getTrackingForce());
        cv.put(COLUMN_STYLUS_CUSTOM_THRESHOLD, stylus.getCustomThreshold());
        cv.put(COLUMN_STYLUS_HOURS, stylus.getHours());
        db.update(STYLUS_TABLE, cv, COLUMN_STYLUS_ID + " = ?", new String[]{String.valueOf(stylus.getId())});
        db.close();
    }

    /**
     * Delete a record containing a {@link Stylus} from the database
     * @param id stylus ID
     */
    public void deleteStylus(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(STYLUS_TABLE, COLUMN_STYLUS_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void deleteAllStyli() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(STYLUS_TABLE, null, null);
    }

    public void deleteProfile(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(PROFILE_TABLE, COLUMN_PROFILE_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void deleteAllProfiles() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(PROFILE_TABLE, null, null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
