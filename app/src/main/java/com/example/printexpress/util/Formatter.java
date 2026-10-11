package com.example.printexpress.util;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Turns raw API values into nice text for the screen. */
public final class Formatter {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("d MMM yyyy, h:mm a", Locale.ENGLISH);

    private Formatter() {
    }

    /** 1234.5 -> "LKR 1,234.50" */
    public static String money(double amount) {
        NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        return "LKR " + format.format(amount);
    }

    /** "2026-10-15" -> "15 Oct 2026" */
    public static String date(String isoDate) {
        if (isoDate == null) return "";
        try {
            return LocalDate.parse(isoDate).format(DATE);
        } catch (Exception e) {
            return isoDate;
        }
    }

    /** "2026-10-10 14:30:00" (SQLite format) -> "10 Oct 2026, 2:30 PM" */
    public static String dateTime(String isoDateTime) {
        if (isoDateTime == null) return "";
        try {
            return LocalDateTime.parse(isoDateTime.replace(' ', 'T')).format(DATE_TIME);
        } catch (Exception e) {
            return isoDateTime;
        }
    }

    /** LocalDate -> "15 Oct 2026" */
    public static String date(LocalDate date) {
        return date.format(DATE);
    }

    /** "PROCESSING" -> "Processing" */
    public static String capitalize(String text) {
        if (text == null || text.isEmpty()) return "";
        String lower = text.toLowerCase(Locale.ENGLISH).replace('_', ' ');
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }
}
