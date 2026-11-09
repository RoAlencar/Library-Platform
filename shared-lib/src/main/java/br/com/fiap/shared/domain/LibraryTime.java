package br.com.fiap.shared.domain;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

/**
 * Official time zone of the library: {@code America/Sao_Paulo} (GMT-3, no DST since 2019).
 * Every {@link LocalDateTime} in the domain is a wall-clock time in this zone and is exposed
 * with second precision and by the API as ISO-8601 with offset (e.g. {@code 2026-11-09T10:15:30-03:00}).
 */
public final class LibraryTime {

    public static final ZoneId ZONE_ID = ZoneId.of("America/Sao_Paulo");

    private LibraryTime() {
    }

    public static Clock systemClock() {
        return Clock.system(ZONE_ID);
    }

    public static LocalDateTime now(Clock clock) {
        return LocalDateTime.now(clock.withZone(ZONE_ID)).truncatedTo(ChronoUnit.SECONDS);
    }
}
