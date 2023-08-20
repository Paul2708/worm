package de.paul2708.worm.columns.properties;

import java.time.ZoneId;

public class TimeZoneProperty implements ColumnProperty {

    public static final String DEFAULT_SYSTEM_TIME_ZONE = "DEFAULT_SYSTEM_ZONE_ID";

    private final String timeZone;

    public TimeZoneProperty(String timeZone) {
        this.timeZone = timeZone;
    }

    public ZoneId toZone() {
        if (timeZone.equals(DEFAULT_SYSTEM_TIME_ZONE)) {
            return ZoneId.systemDefault();
        }

        return ZoneId.of(timeZone);
    }
}
