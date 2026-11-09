package br.com.fiap.shared.common;

import br.com.fiap.shared.domain.LibraryTime;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public final class DateUtils {

    private DateUtils() {}

    public static LocalDateTime now() {
        return LibraryTime.now(LibraryTime.systemClock());
    }

    public static String formatISO(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public static OffsetDateTime toOffsetDateTime(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.atZone(LibraryTime.ZONE_ID).toOffsetDateTime();
    }
}
