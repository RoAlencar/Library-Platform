package br.com.fiap.shared.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateUtils {

    private DateUtils() {}

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    public static String formatISO(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
