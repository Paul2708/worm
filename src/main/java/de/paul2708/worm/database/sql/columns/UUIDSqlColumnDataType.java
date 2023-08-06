package de.paul2708.worm.database.sql.columns;

import de.paul2708.worm.columns.ColumnAttribute;

import java.nio.ByteBuffer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

final class UUIDSqlColumnDataType implements SqlColumnDataType<UUID> {
	@Override
	public boolean matches(Class<?> expectedType) {
		return expectedType.equals(UUID.class);
	}

	@Override
	public UUID getValue(ResultSet resultSet, String column) throws SQLException {
		return UUIDConverter.convert(resultSet.getBytes(column));
	}

	@Override
	public void setValue(PreparedStatement statement, int index, UUID value) throws SQLException {
		statement.setBytes(index, UUIDConverter.convert(value));
	}

	@Override
	public String toSqlType(ColumnAttribute attribute) {
		return "BINARY(16)";
	}

	private static final class UUIDConverter {

		private UUIDConverter() {
			throw new IllegalAccessError("Illegal access of UUIDConverter - No instantiation!");
		}

		static byte[] convert(UUID uuid) {
			return ByteBuffer.wrap(new byte[16])
					.putLong(uuid.getMostSignificantBits())
					.putLong(uuid.getLeastSignificantBits())
					.array();
		}

		static UUID convert(byte[] bytes) {
			ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
			return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
		}
	}

}
