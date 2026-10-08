package database;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class PasswordSecurityTest {
    @TempDir Path temporary;

    @Test void hashesAreSaltedAndWrongPasswordsAndMalformedHashesFail() {
        String first = PasswordHash.hash("correct 🔒");
        assertNotEquals(first, PasswordHash.hash("correct 🔒"));
        assertTrue(PasswordHash.verify("correct 🔒", first));
        assertFalse(PasswordHash.verify("incorrect", first));
        for (String bad : new String[]{"plaintext", "pbkdf2-sha256$1$AA==$AA==", "pbkdf2-sha256$invalid", "pbkdf2-sha256$600000$?$?"}) {
            assertFalse(PasswordHash.verify("plaintext", bad));
        }
    }

    @Test void registrationAndLegacyUpgradeNeverLeavePlaintext() throws Exception {
        System.setProperty("xplore.database.url", "jdbc:sqlite:" + temporary.resolve("test.db"));
        try {
            DBConnection.initialize();
            assertTrue(UserDAO.register("new", "secret"));
            assertTrue(UserDAO.login("new", "secret"));
            assertFalse(UserDAO.login("new", "wrong"));
            assertFalse(UserDAO.login("new' OR 1=1 --", "secret"));
            try (var c = DBConnection.connect(); var stmt = c.createStatement()) {
                stmt.executeUpdate("INSERT INTO accounts VALUES ('legacy', 'old-secret')");
            }
            assertFalse(UserDAO.login("legacy", "old-secret"));
            DBConnection.initialize();
            assertTrue(UserDAO.login("legacy", "old-secret"));
            String migrated;
            try (var c = DBConnection.connect(); var stmt = c.createStatement(); var rows = stmt.executeQuery("SELECT password FROM accounts WHERE username = 'legacy'")) {
                assertTrue(rows.next());
                migrated = rows.getString(1);
                assertTrue(migrated.startsWith(PasswordHash.PREFIX));
                assertNotEquals("old-secret", migrated);
            }
            DBConnection.initialize();
            try (var c = DBConnection.connect(); var stmt = c.createStatement(); var rows = stmt.executeQuery("SELECT password FROM accounts WHERE username = 'legacy'")) {
                assertTrue(rows.next());
                assertEquals(migrated, rows.getString(1));
            }
        } finally { System.clearProperty("xplore.database.url"); }
    }
}
