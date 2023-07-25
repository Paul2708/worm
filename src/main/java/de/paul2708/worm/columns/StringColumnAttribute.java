package de.paul2708.worm.columns;

public class StringColumnAttribute extends ColumnAttribute {

    private final int maxLength;

    public StringColumnAttribute(String columnName, int maxLength) {
        super(columnName, String.class);

        this.maxLength = maxLength;
    }

    public StringColumnAttribute(String columnName) {
        this(columnName, -1);
    }

    public boolean hasMaximumLength() {
        return maxLength != -1;
    }

    public int getMaxLength() {
        return maxLength;
    }
}