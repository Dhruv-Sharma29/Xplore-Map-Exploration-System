package database;

import model.Zone;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ZoneDAO {

    public static void insertDefaultZones() {
        String insert = "INSERT INTO zones(id, name, lat, lon, radius) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(insert)) {

            Object[][] defaultZones = {
                    {1, "Clock Tower", 30.3255, 78.0437, 2.0},
                    {2, "FRI", 30.3430, 77.9990, 2.0},
                    {3, "Robbers Cave", 30.3773, 78.0796, 2.0},
                    {4, "Tapkeshwar Temple", 30.3647, 78.0336, 2.0},
                    {5, "Railway Station", 30.3165, 78.0322, 2.0},
                    {6, "ISBT", 30.2880, 77.9960, 2.0},
                    {7, "Rajpur Road", 30.3660, 78.0700, 2.0},
                    {8, "Malsi Deer Park", 30.3950, 78.0850, 2.0}
            };

            for (Object[] z : defaultZones) {
                pstmt.setInt(1, (int)z[0]);
                pstmt.setString(2, (String)z[1]);
                pstmt.setDouble(3, (double)z[2]);
                pstmt.setDouble(4, (double)z[3]);
                pstmt.setDouble(5, (double)z[4]);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Zones likely already exist or DB error: " + e.getMessage());
        }
    }

    public static List<Zone> getAllZones() {
        List<Zone> zones = new ArrayList<>();
        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM zones")) {

            while (rs.next()) {
                zones.add(new Zone(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("lat"),
                        rs.getDouble("lon"),
                        rs.getDouble("radius")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return zones;
    }
}
