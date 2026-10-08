package model;

public class User {

    public double lat;
    public double lon;
    /** Route completion fraction, written by the simulator and read on the UI thread. */
    public volatile double progress;

    public void updateLocation(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }
}