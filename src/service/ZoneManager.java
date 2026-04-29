package service;

import model.Zone;
import database.ZoneDAO;
import database.DBConnection;
import java.util.*;

public class ZoneManager {

    public static List<Zone> loadZones() {
        DBConnection.initialize();

        List<Zone> zones = ZoneDAO.getAllZones();
        if (zones.isEmpty()) {
            ZoneDAO.insertDefaultZones();
            zones = ZoneDAO.getAllZones();
        }
        return zones;
    }
}