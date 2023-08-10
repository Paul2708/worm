package de.paul2708.worm.database.sql.datatypes;

import java.nio.ByteBuffer;
import java.util.UUID;

public final class UUIDConverter {

    private UUIDConverter() {
        throw new IllegalAccessError("Illegal access of UUIDConverter - No instantiation!");
    }

    public static byte[] convert(UUID uuid) {
        return ByteBuffer.wrap(new byte[16])
                .putLong(uuid.getMostSignificantBits())
                .putLong(uuid.getLeastSignificantBits())
                .array();
    }

    public static UUID convert(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
    }
}
