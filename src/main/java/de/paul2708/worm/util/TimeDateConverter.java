package de.paul2708.worm.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public final class TimeDateConverter {

    public static LocalDateTime convertFromUTC(LocalDateTime dateTime, ZoneId targetZone) {
        ZonedDateTime utcDateTime = dateTime.atZone(ZoneId.of("UTC"));
        ZonedDateTime localDateTime = utcDateTime.withZoneSameInstant(targetZone);

        return localDateTime.toLocalDateTime();
    }

    public static LocalDateTime convertToUTC(LocalDateTime dateTime, ZoneId currentZone) {
        ZonedDateTime utcDateTime = dateTime.atZone(currentZone);
        ZonedDateTime localDateTime = utcDateTime.withZoneSameInstant(ZoneId.of("UTC"));

        return localDateTime.toLocalDateTime();
    }
}
