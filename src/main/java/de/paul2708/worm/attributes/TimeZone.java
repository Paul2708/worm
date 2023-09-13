package de.paul2708.worm.attributes;

import de.paul2708.worm.attributes.properties.TimeZoneProperty;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface TimeZone {

    String value() default TimeZoneProperty.DEFAULT_SYSTEM_TIME_ZONE;
}
