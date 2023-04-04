package net.prsv.stimer;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;

/**
 * Represents the Stylus Timer application. Provides global context for use in custom classes.
 * @author Pavel Urusov 2023
 */
public class STimerApp extends Application {

    private static STimerApp mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        // grab the application context
        mContext = this;
    }

    /**
     * Use this method to get application context for use in custom classes.
     * @return the application context.
     */
    public static STimerApp getContext() {
        return mContext;
    }

    public static void restart() {
        PackageManager packageManager = getContext().getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(getContext().getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        getContext().startActivity(mainIntent);
        System.exit(0);
    }

    /**
     * Checks whether a {@code String} can be safely converted to an {@code int} using {@code Integer.parseInt()}.
     * @param str a String to be checked
     * @return {@code true} if {@code str} can be safely converted to an {@code int}, or {@code false} otherwise.
     */
    public static boolean isInteger(String str) {
        if (str == null) return false;
        try {
            Integer.parseInt(str);
        } catch(NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * Checks whether a {@code String} can be safely converted to a {@code double} using {@code Double.parseDouble()}.
     * @param str a String to be checked
     * @return {@code true} if {@code str} can be safely converted to an {@code double}, or {@code false} otherwise.
     */
    public static boolean isDouble(String str) {
        if (str == null) return false;
        try {
            Double.parseDouble(str);
        } catch(NumberFormatException e) {
            return false;
        }
        return true;
    }

}
