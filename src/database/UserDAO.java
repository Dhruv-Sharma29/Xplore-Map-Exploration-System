package database;

import java.sql.*;

public class UserDAO {

    public static boolean register(String username, String password) {
        String sql = "INSERT INTO accounts(username, password) VALUES(?, ?)";
        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, PasswordHash.hash(password));
            pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean login(String username, String password) {
        String sql = "SELECT password FROM accounts WHERE username = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && PasswordHash.verify(password, rs.getString("password"));
            }
        } catch (Exception e) {
            return false;
        }
    }

    public static void saveUserProgress(String name, double progress) {
        // We still save progress to the old users table or update accounts
        // We'll insert into users for progress history for now
        String sql = "INSERT INTO users(name, progress) VALUES(?, ?)";
        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, progress);
            pstmt.executeUpdate();
            System.out.println("User progress saved to DB: " + name + " (" + progress + "%)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
