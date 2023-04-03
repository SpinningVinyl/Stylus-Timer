package net.prsv.stimer;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

/**
 * {@link Stylus} represents a single stylus/cartridge in the database.
 * Each stylus has six properties: stylus ID, stylus name, profile ID, tracking force (in grams), hours used and custom threshold for replacement.
 * If the custom threshold is equal to zero, the app uses profile's default threshold.
 * @author Pavel Urusov 2023
 */
public class Stylus implements Comparable<Stylus>, Serializable {
    private int id;
    private String name;
    private int profileId;
    private double trackingForce;
    private double hours;
    private int customThreshold;

    private static final long serialVersionUID = 246213005L;

    /**
     * This empty default constructor is required for calls to DataSnapshot.getValue(Stylus.class)
     * */
    public Stylus() {}

    /**
     * Indicates whether some other object is "equal to" this one.
     * @param that the reference object with which to compare.
     * @return {@code true} if this object is the same as the {@code that} argument; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;
        Stylus stylus = (Stylus) that;
        return profileId == stylus.profileId && Double.compare(stylus.trackingForce, trackingForce) == 0 && Double.compare(stylus.hours, hours) == 0 && customThreshold == stylus.customThreshold && name.equals(stylus.name);
    }

    /**
     * Returns a hash code value for the {@link Stylus} object.
     * @return a hash code value for the object
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, profileId, trackingForce, hours, customThreshold);
    }

    /**
     * Create a new Stylus object.
     * @param id ID of the stylus (also used as primary key in the database)
     * @param name name of the stylus
     * @param profileId ID of the stylus profile
     * @param trackingForce VTF of the stylus (in grams)
     * @param customThreshold custom threshold (in hours) for stylus replacement. If the custom threshold is equal to zero, the app uses profile's default threshold.
     */
    public Stylus(int id, String name, int profileId, double trackingForce, int customThreshold) {
        if (customThreshold < 0) throw new IllegalArgumentException("Custom threshold cannot be negative!");
        this.id = id;
        this.name = name;
        this.profileId = profileId;
        this.trackingForce = trackingForce;
        this.customThreshold = customThreshold;
        this.hours = 0d;
    }

    /**
     * Returns the ID of the stylus.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the name of the stylus.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the stylus.
     * @param name stylus name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the ID of the stylus profile.
     */
    public int getProfileId() {
        return profileId;
    }

    /**
     * Sets the ID of the stylus profile.
     * @param profileId ID of the stylus profile
     */
    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    /**
     * Returns the tracking force (in grams).
     */
    public double getTrackingForce() {
        return trackingForce;
    }

    /**
     * Sets the tracking force.
     * @param trackingForce VTF of the stylus (in grams)
     */
    public void setTrackingForce(double trackingForce) {
        this.trackingForce = trackingForce;
    }

    /**
     * Returns the custom threshold after which the stylus should be replaced.
     */
    public int getCustomThreshold() {
        return customThreshold;
    }

    /**
     * Sets the custom threshold after which the stylus should be replaced.
     * @param customThreshold custom threshold for replacement (in hours)
     */
    public void setCustomThreshold(int customThreshold) {
        if (customThreshold < 0) throw new IllegalArgumentException("Custom threshold cannot be negative!");
        this.customThreshold = customThreshold;
    }

    /**
     * Returns the number of hours the stylus has been used
     */
    public double getHours() {
        return hours;
    }

    /**
     * Sets the number of hours the stylus has been used
     * @param hours -- number of hours
     */
    public void setHours(double hours) {
        if (hours < 0) {
            throw new IllegalArgumentException("Number of hours can't be negative");
        }
        this.hours = hours;
    }

    /**
     * Sets the id of the stylus.
     * @param id stylus ID
     */
    public void setId(int id) {
        this.id = id;
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
    public int compareTo(Stylus that) {
        return this.name.compareTo(that.name);
    }

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }


}
