package de.paul2708.worm.database.sql.datatypes.impl;

import de.paul2708.worm.attributes.AttributeInformation;
import de.paul2708.worm.attributes.CreatedAt;
import de.paul2708.worm.attributes.UpdatedAt;
import de.paul2708.worm.attributes.properties.TimeZoneProperty;
import de.paul2708.worm.database.sql.datatypes.ColumnDataType;
import de.paul2708.worm.util.DateTimeConverter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;

public final class LocalDateTimeColumnDataType implements ColumnDataType<LocalDateTime> {

    @Override
    public boolean matches(Class<?> clazz) {
        return clazz.equals(LocalDateTime.class);
    }

    @Override
    public LocalDateTime from(ResultSet resultSet, AttributeInformation attribute, String column) throws SQLException {
        LocalDateTime localDateTime = resultSet.getTimestamp(column).toLocalDateTime();
        ZoneId targetZone = attribute.getProperty(TimeZoneProperty.class).toZone();

        return DateTimeConverter.convertFromUTC(localDateTime, targetZone);
    }

    @Override
    public void to(PreparedStatement statement, int index, AttributeInformation attribute, LocalDateTime value) throws SQLException {
        ZoneId localZone = attribute.getProperty(TimeZoneProperty.class).toZone();
        LocalDateTime localDateTimeUTC = DateTimeConverter.convertToUTC(value, localZone);

        statement.setTimestamp(index, Timestamp.valueOf(localDateTimeUTC));
    }

    @Override
    public String getSqlType(AttributeInformation attribute) {
        if (attribute == null) {
            return "DATETIME(6)";
        }

        if (attribute.hasAnnotation(CreatedAt.class)) {
            return "DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6)";
        } else if (attribute.hasAnnotation(UpdatedAt.class)) {
            return "DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)";
        }

        return "DATETIME(6)";
    }
}
