package net.prsv.stimer;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * {@link StylusProfile} represents a single stylus profile in the database.
 * Each stylus profile has three parameters: ID, name and threshold (the number of hours a stylus of this profile can be used before replacement)
 * @author Pavel Urusov 2023
 */
public class StylusProfile implements Comparable<StylusProfile> {

    private int id;
    private String name;
    private int threshold;

    /**
     * This empty default constructor is required for calls to DataSnapshot.getValue(StylusProfile.class)
     */
    public StylusProfile() {}

    /**
     * Create a new {@link StylusProfile} object.
     * @param id profile ID
     * @param name name of the profile
     * @param threshold threshold (the number of hours a stylus of this profile can be used before replacement)
     */
    public StylusProfile(int id, String name, int threshold) {
        this.name = name;
        this.threshold = threshold;
        this.id = id;
    }

    /**
     * Returns the ID of the profile
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the name of the profile
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the threshold (the number of hours a stylus of this profile can be used before replacement)
     */
    public int getThreshold() {
        return threshold;
    }

    @NonNull
    @Override
    public String toString() {
        return this.name + " (" + this.threshold + " hrs)";
    }

    @Override
    public int compareTo(StylusProfile that) {
        if (this.threshold == that.threshold) {
            return this.name.compareTo(that.name);
        }
        return Integer.compare(this.threshold, that.threshold);
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;
        StylusProfile profile = (StylusProfile) that;
        return id == profile.id && threshold == profile.threshold && Objects.equals(name, profile.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, threshold);
    }

}
