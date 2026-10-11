package com.example.printexpress.util;

import java.util.regex.Pattern;

/**
 * Input validation rules used by every form.
 * Each method returns an error message, or null when the value is OK.
 * The repositories check the SAME rules again before saving (never trust only the screen).
 *
 * Pure Java (no Android code) so it can be unit tested - see ValidatorTest.
 */
public final class Validator {

    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE = Pattern.compile("^(0|\\+94)7\\d{8}$");
    private static final Pattern PASSWORD = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{8,64}$");
    private static final Pattern NAME = Pattern.compile("^[A-Za-z .']{3,100}$");
    private static final Pattern POSTAL_CODE = Pattern.compile("^\\d{5}$");

    private Validator() {
    }

    public static String required(String value, String fieldName) {
        return isEmpty(value) ? fieldName + " is required" : null;
    }

    public static String name(String value) {
        if (isEmpty(value)) return "Full name is required";
        if (!NAME.matcher(value.trim()).matches()) return "Name must be 3-100 letters";
        return null;
    }

    public static String email(String value) {
        if (isEmpty(value)) return "Email is required";
        if (!EMAIL.matcher(value.trim()).matches()) return "Enter a valid email address";
        return null;
    }

    public static String phone(String value) {
        if (isEmpty(value)) return "Mobile number is required";
        if (!PHONE.matcher(value.trim().replace(" ", "")).matches()) {
            return "Enter a valid Sri Lankan mobile number (07XXXXXXXX)";
        }
        return null;
    }

    /** Login accepts an email OR a phone number. */
    public static String emailOrPhone(String value) {
        if (isEmpty(value)) return "Enter your email or mobile number";
        return value.contains("@") ? email(value) : phone(value);
    }

    public static String password(String value) {
        if (isEmpty(value)) return "Password is required";
        if (!PASSWORD.matcher(value).matches()) return "Use 8+ characters with letters and numbers";
        return null;
    }

    public static String confirmPassword(String password, String confirm) {
        if (isEmpty(confirm)) return "Please confirm your password";
        if (!confirm.equals(password)) return "Passwords do not match";
        return null;
    }

    public static String postalCode(String value) {
        if (isEmpty(value)) return "Postal code is required";
        if (!POSTAL_CODE.matcher(value.trim()).matches()) return "Postal code must be 5 digits";
        return null;
    }

    public static String length(String value, String fieldName, int min, int max) {
        int len = value == null ? 0 : value.trim().length();
        if (len < min) return fieldName + " must be at least " + min + " characters";
        if (len > max) return fieldName + " must be at most " + max + " characters";
        return null;
    }

    public static String quantity(int value, int min, int max) {
        if (value < min) return "Minimum order is " + min;
        if (value > max) return "Maximum order is " + max;
        return null;
    }

    private static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
