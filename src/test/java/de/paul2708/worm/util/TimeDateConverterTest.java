package de.paul2708.worm.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TimeDateConverterTest {

    @Test
    void testConversionFromUTCtoEuropeBerlin() {
        LocalDateTime timeInUTC = LocalDateTime.now();

        // Europe/Berlin has a +2 hours offset to UTC
        ZoneId targetZone = ZoneId.of("Europe/Berlin");

        LocalDateTime timeInBerlin = TimeDateConverter.convertFromUTC(timeInUTC, targetZone);

        assertEquals(timeInBerlin, timeInUTC.plusHours(2));
    }

    @Test
    void testConversionFromEuropeBerlinToUTC() {
        LocalDateTime timeInBerlin = LocalDateTime.now();

        // Europe/Berlin has a +2 hours offset to UTC
        ZoneId currentZone = ZoneId.of("Europe/Berlin");

        LocalDateTime timeInUTC = TimeDateConverter.convertToUTC(timeInBerlin, currentZone);

        assertEquals(timeInUTC, timeInBerlin.minusHours(2));
    }
}
