package net.prsv.stimer;

import androidx.annotation.NonNull;

public class Stylus implements Comparable<Stylus> {
    private int id;
    private String name;
    private int profileId;
    private double trackingForce;
    private double hours;
    private int customThreshold;

    private Stylus() {
        // empty default constructor required for calls to DataSnapshot.getValue(Stylus.class)
    }

    public Stylus(int id, String name, int profileId, double trackingForce, int customThreshold) {
        this.id = id;
        this.name = name;
        this.profileId = profileId;
        this.trackingForce = trackingForce;
        this.customThreshold = customThreshold;
        this.hours = 0d;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public double getTrackingForce() {
        return trackingForce;
    }

    public void setTrackingForce(double trackingForce) {
        this.trackingForce = trackingForce;
    }

    public int getCustomThreshold() {
        return customThreshold;
    }

    public void setCustomThreshold(int customThreshold) {
        this.customThreshold = customThreshold;
    }

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }

    @Override
    public int compareTo(Stylus that) {
        return this.name.compareTo(that.name);
    }

    @NonNull
    @Override
    public String toString() {
        return this.name + ", profileId: " + this.profileId + ", tf: " + this.trackingForce;
    }

}
