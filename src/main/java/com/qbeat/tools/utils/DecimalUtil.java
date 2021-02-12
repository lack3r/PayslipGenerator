package com.qbeat.tools.utils;

public class DecimalUtil {
    public static double round(double value) {
        return Math.round(value * 100) / 100.0;
    }
}
