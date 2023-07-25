package de.paul2708.worm.columns;

public class StringColumnAttribute extends ColumnAttribute {

    private final int maxLength;

    public StringColumnAttribute(String columnName, String fieldType, int maxLength) {
        super(columnName, fieldType, String.class);

        this.maxLength = maxLength;
    }

    public StringColumnAttribute(String columnName, String fieldType) {
        this(columnName, fieldType, -1);
    }

    public boolean hasMaximumLength() {
        return maxLength != -1;
    }

    public int getMaxLength() {
        return maxLength;
    }
}