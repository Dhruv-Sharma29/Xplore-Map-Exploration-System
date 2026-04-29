import java.sql.*;

public class PrintDB {
    public static void main(String[] args) {
        System.out.println("\n--- DATABASE CONTENTS ---");
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:explore.db")) {
            System.out.println("\nACCOUNTS TABLE:");
            ResultSet rs1 = conn.createStatement().executeQuery("SELECT * FROM accounts");
            while (rs1.next()) {
                System.out.println("Username: " + rs1.getString("username") + " | Password: " + rs1.getString("password"));
            }

            System.out.println("\nUSERS (PROGRESS) TABLE:");
            ResultSet rs2 = conn.createStatement().executeQuery("SELECT * FROM users");
            while (rs2.next()) {
                System.out.println("ID: " + rs2.getInt("id") + " | Name: " + rs2.getString("name") + " | Progress: " + rs2.getDouble("progress") + "%");
            }

            System.out.println("\nZONES TABLE:");
            ResultSet rs3 = conn.createStatement().executeQuery("SELECT COUNT(*) FROM zones");
            if (rs3.next()) {
                System.out.println("Total Zones Stored: " + rs3.getInt(1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("-------------------------\n");
    }
}
