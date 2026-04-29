package model;

public class Zone {

    public int id;
    public String name;
    public double lat;
    public double lon;
    public double radius;
    public boolean unlocked = false;

    public Zone(int id, String name, double lat, double lon, double radius) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.radius = radius;
    }
}