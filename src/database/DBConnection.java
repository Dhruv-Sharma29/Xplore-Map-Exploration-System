package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DBConnection {

    public static Connection connect() {
        try {
            return DriverManager.getConnection(System.getProperty("xplore.database.url", "jdbc:sqlite:explore.db"));
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
            // Upgrade all legacy rows before the login screen accepts credentials.
            // A transaction prevents a partially upgraded database on failure.
            conn.setAutoCommit(false);
            try (var select = conn.prepareStatement("SELECT username, password FROM accounts");
                 var update = conn.prepareStatement("UPDATE accounts SET password = ? WHERE username = ?");
                 var rows = select.executeQuery()) {
                while (rows.next()) {
                    String stored = rows.getString("password");
                    if (!stored.startsWith(PasswordHash.PREFIX)) {
                        update.setString(1, PasswordHash.hash(stored));
                        update.setString(2, rows.getString("username"));
                        update.addBatch();
                    }
                }
                update.executeBatch();
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (Exception e) {
            throw new IllegalStateException("Database initialization failed", e);
        }
    }
}