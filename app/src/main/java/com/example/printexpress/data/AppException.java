package com.example.printexpress.data;

/**
 * A rule was broken (wrong password, quantity too small, date too early ...).
 * The message is friendly and is shown to the customer as it is.
 *
 *   throw new AppException("The earliest available date is 12 Oct 2026");
 */
public class AppException extends RuntimeException {

    public AppException(String message) {
        super(message);
    }

    /** Throws when a Validator check returned an error message (null means OK). */
    public static void check(String error) {
        if (error != null) throw new AppException(error);
    }
}
