package net.prsv.stimer;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * A singleton class that represents user details that are stored in {@link SharedPreferences}
 * @author Pavel Urusov 2023
 */
public class STimerPreferences {

    private static STimerPreferences instance = null;
    private final SharedPreferences preferences;
    private int customSide, customLP;
    private int dbVersion;
    private boolean setupComplete;

    private final String CUSTOM_SIDE_KEY = "CUSTOM_SIDE";
    private final String CUSTOM_LP_KEY = "CUSTOM_LP";
    private final String SETUP_COMPLETE_KEY = "SETUP_COMPLETE";
    private final String DB_VERSION_KEY = "DB_VERSION";

    /**
     * Private constructor means that only one instance of this class can exist.
     */
    private STimerPreferences() {
        Context context = STimerApp.getContext();
        preferences = context.getSharedPreferences(context.getString(R.string.preference_file_key),
                Context.MODE_PRIVATE);
        customSide = preferences.getInt(CUSTOM_SIDE_KEY, 0);
        customLP = preferences.getInt(CUSTOM_LP_KEY, 0);
        dbVersion = preferences.getInt(DB_VERSION_KEY, 0);
        setupComplete = preferences.getBoolean(SETUP_COMPLETE_KEY, false);
    }

    /**
     * If the single instance does not exist, instantiate it and return to the caller.
     * @return The single instance of the {@link STimerPreferences} class
     */
    protected static STimerPreferences getInstance() {
        if (instance == null) {
            instance = new STimerPreferences();
        }
        return instance;
    }

    protected int getCustomSide() {
        return customSide;
    }

    protected int getCustomLP() {
        return customLP;
    }

    protected int getDbVersion() {
        return dbVersion;
    }

    private boolean isSetupComplete() {
        return setupComplete;
    }

    protected void setCustomSide(int length) {
        customSide = length;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(CUSTOM_SIDE_KEY, customSide);
        editor.apply();
    }

    protected void setDbVersion(int dbVersion) {
        this.dbVersion = dbVersion;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(DB_VERSION_KEY, dbVersion);
        editor.apply();
    }

    protected void setCustomLP(int length) {
        customLP = length;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(CUSTOM_LP_KEY, customLP);
        editor.apply();
    }

    protected void completeSetup() {
        SharedPreferences.Editor editor = preferences.edit();
        setupComplete = true;
        editor.putBoolean(SETUP_COMPLETE_KEY, setupComplete);
        editor.apply();
    }

}
