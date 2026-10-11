package com.example.printexpress;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.example.printexpress.util.PasswordHasher;

import org.junit.Test;

/** Passwords are stored as salted PBKDF2 hashes - never as plain text. */
public class PasswordHasherTest {

    @Test
    public void correctPasswordMatches() {
        String stored = PasswordHasher.hash("Demo@123");
        assertTrue(PasswordHasher.matches("Demo@123", stored));
    }

    @Test
    public void wrongPasswordDoesNotMatch() {
        String stored = PasswordHasher.hash("Demo@123");
        assertFalse(PasswordHasher.matches("demo@123", stored));   // case matters
        assertFalse(PasswordHasher.matches("", stored));
    }

    @Test
    public void samePasswordGivesDifferentHashes() {
        // Random salt: two users with the same password do not have the same hash
        assertNotEquals(PasswordHasher.hash("Demo@123"), PasswordHasher.hash("Demo@123"));
    }

    @Test
    public void hashDoesNotContainPassword() {
        assertFalse(PasswordHasher.hash("Demo@123").contains("Demo@123"));
    }

    @Test
    public void brokenStoredValueNeverMatches() {
        assertFalse(PasswordHasher.matches("Demo@123", null));
        assertFalse(PasswordHasher.matches("Demo@123", "not-a-hash"));
    }
}
