package io.qbeat.utils;

public class DecimalUtil {
    public static double round(double value) {
        return Math.round(value * 100) / 100.0;
    }

    public static String strFormatted(double value) {
        return String.format("%.2f", value);
    }
}
