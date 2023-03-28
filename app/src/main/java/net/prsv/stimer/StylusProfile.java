package net.prsv.stimer;

import androidx.annotation.NonNull;

public class StylusProfile {

    private int id;
    private String name;
    private int threshold;

    public StylusProfile() {
        // empty default constructor required for calls to DataSnapshot.getValue(StylusProfile.class)
    }

    public StylusProfile(int id, String name, int threshold) {
        this.name = name;
        this.threshold = threshold;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getThreshold() {
        return threshold;
    }

    @NonNull
    @Override
    public String toString() {
        return this.id + ": " + this.name + ", " + threshold + " hours";
    }

}
