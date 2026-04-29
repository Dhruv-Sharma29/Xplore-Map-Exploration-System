package model;

public class User {

    public double lat;
    public double lon;

    public void updateLocation(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }
}