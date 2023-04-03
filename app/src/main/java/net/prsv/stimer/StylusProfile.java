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

    /**
     * Compares this object with the specified object for order.
     * Returns a negative integer, zero, or a positive integer as this object is less than,
     * equal to, or greater than the specified object.
     * @param that the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object is less than,
     * equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(StylusProfile that) {
        if (this.threshold == that.threshold) {
            return this.name.compareTo(that.name);
        }
        return Integer.compare(this.threshold, that.threshold);
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * @param that the reference object with which to compare.
     * @return {@code true} if this object is the same as the {@code that} argument; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;
        StylusProfile profile = (StylusProfile) that;
        return id == profile.id && threshold == profile.threshold && Objects.equals(name, profile.name);
    }

    /**
     * Returns a hash code value for the {@link StylusProfile} object.
     * @return a hash code value for the object
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, name, threshold);
    }

}
