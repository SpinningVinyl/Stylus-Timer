package net.prsv.stimer;

import android.app.Application;

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

}
