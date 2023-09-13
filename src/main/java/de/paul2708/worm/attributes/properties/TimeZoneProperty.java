package de.paul2708.worm.attributes.properties;

import java.time.ZoneId;

public class TimeZoneProperty implements AttributeProperty {

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
