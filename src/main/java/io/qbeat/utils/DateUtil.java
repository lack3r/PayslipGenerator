package io.qbeat.utils;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    public static LocalDate now() {
        return LocalDate.now();
    }

    public static LocalDate dateStrToLocalDate(String date, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDate.parse(date, formatter);
    }

    public static String localDateToDateStr(LocalDate date, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return date.format(formatter);
    }

    public static boolean isInCurrentMonth(LocalDate date) {
        LocalDate dateNow = now();

        return dateNow.getYear() == date.getYear() && dateNow.getMonth() == date.getMonth();
    }

    public static int monthsDifference(LocalDate dateStart, LocalDate dateEnd) {
        LocalDate date1 = LocalDate.of(dateStart.getYear(), dateStart.getMonth(), 1);
        LocalDate date2 = LocalDate.of(dateEnd.getYear(), dateEnd.getMonth(), 1);

        Period diff = Period.between(date1, date2);
        return diff.getMonths();
    }
}
