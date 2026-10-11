package com.example.printexpress;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.example.printexpress.util.Validator;

import org.junit.Test;

/** Unit tests for the form validation rules (test plan section "Validation"). */
public class ValidatorTest {

    @Test
    public void validEmailPasses() {
        assertNull(Validator.email("kamal@printxpress.lk"));
    }

    @Test
    public void invalidEmailFails() {
        assertEquals("Enter a valid email address", Validator.email("kamal@"));
        assertEquals("Email is required", Validator.email("  "));
    }

    @Test
    public void sriLankanPhoneNumbers() {
        assertNull(Validator.phone("0771234567"));
        assertNull(Validator.phone("+94771234567"));
        assertNotNull(Validator.phone("0112345678"));   // landline, not mobile
        assertNotNull(Validator.phone("077123"));        // too short
        assertNotNull(Validator.phone("07712345678"));   // too long
    }

    @Test
    public void loginAcceptsEmailOrPhone() {
        assertNull(Validator.emailOrPhone("demo@printxpress.lk"));
        assertNull(Validator.emailOrPhone("0771234567"));
        assertNotNull(Validator.emailOrPhone("demo"));
    }

    @Test
    public void passwordNeedsLettersAndNumbers() {
        assertNull(Validator.password("Demo@123"));
        assertNotNull(Validator.password("short1"));       // under 8 characters
        assertNotNull(Validator.password("onlyletters"));  // no number
        assertNotNull(Validator.password("12345678"));     // no letter
    }

    @Test
    public void confirmPasswordMustMatch() {
        assertNull(Validator.confirmPassword("Demo@123", "Demo@123"));
        assertEquals("Passwords do not match", Validator.confirmPassword("Demo@123", "Demo@124"));
    }

    @Test
    public void nameRules() {
        assertNull(Validator.name("Kamal Perera"));
        assertNotNull(Validator.name("Ka"));        // too short
        assertNotNull(Validator.name("Kamal123"));  // digits
    }

    @Test
    public void postalCodeIsFiveDigits() {
        assertNull(Validator.postalCode("10250"));
        assertNotNull(Validator.postalCode("1025"));
        assertNotNull(Validator.postalCode("ABCDE"));
    }

    @Test
    public void quantityBoundaries() {
        assertNull(Validator.quantity(50, 50, 5000));       // minimum is allowed
        assertNull(Validator.quantity(5000, 50, 5000));     // maximum is allowed
        assertEquals("Minimum order is 50", Validator.quantity(49, 50, 5000));
        assertEquals("Maximum order is 5000", Validator.quantity(5001, 50, 5000));
    }

    @Test
    public void lengthRule() {
        assertNull(Validator.length("Hello", "Text", 2, 200));
        assertNotNull(Validator.length("H", "Text", 2, 200));
    }
}
