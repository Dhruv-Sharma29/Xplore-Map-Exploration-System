package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DBConnection {

    public static Connection connect() {
        try {
            return DriverManager.getConnection("jdbc:sqlite:explore.db");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void initialize() {
        String createZones = "CREATE TABLE IF NOT EXISTS zones (" +
                "id INTEGER PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "lat REAL NOT NULL, " +
                "lon REAL NOT NULL, " +
                "radius REAL NOT NULL);";

        String createUsers = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "progress REAL" +
                ");";

        String createAccounts = "CREATE TABLE IF NOT EXISTS accounts (" +
                "username TEXT PRIMARY KEY, " +
                "password TEXT NOT NULL" +
                ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createZones);
            stmt.execute(createUsers);
            stmt.execute(createAccounts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}