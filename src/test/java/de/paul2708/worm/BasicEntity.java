package de.paul2708.worm;

import de.paul2708.worm.attributes.Attribute;
import de.paul2708.worm.attributes.Identifier;
import de.paul2708.worm.attributes.Entity;
import de.paul2708.worm.attributes.generator.IntegerGenerator;

import java.util.UUID;

@Entity("basic_entities")
public class BasicEntity {

    @Identifier(generator = IntegerGenerator.class)
    @Attribute("id")
    private int id;

    @Attribute("a_boolean")
    private boolean aBoolean;

    @Attribute("a_byte")
    private byte aByte;

    @Attribute("a_double")
    private double aDouble;

    @Attribute("an_int")
    private int anInt;

    @Attribute("a_long")
    private long aLong;

    @Attribute("a_short")
    private short aShort;

    @Attribute("a_string")
    private String string;

    @Attribute("a_uuid")
    private UUID uuid;

    @Attribute("an_enum")
    private Type aEnum;

    public BasicEntity() {
        this.string = "";
        this.uuid = UUID.randomUUID();
        this.aEnum = Type.MEDIUM;
    }

    public void setBoolean(boolean aBoolean) {
        this.aBoolean = aBoolean;
    }

    public void setByte(byte aByte) {
        this.aByte = aByte;
    }

    public void setDouble(double aDouble) {
        this.aDouble = aDouble;
    }

    public void setInt(int anInt) {
        this.anInt = anInt;
    }

    public void setLong(long aLong) {
        this.aLong = aLong;
    }

    public void setShort(short aShort) {
        this.aShort = aShort;
    }

    public void setString(String string) {
        this.string = string;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setEnum(Type aEnum) {
        this.aEnum = aEnum;
    }

    public int getId() {
        return id;
    }

    public boolean getBoolean() {
        return aBoolean;
    }

    public byte getByte() {
        return aByte;
    }

    public double getDouble() {
        return aDouble;
    }

    public int getInt() {
        return anInt;
    }

    public long getLong() {
        return aLong;
    }

    public short getShort() {
        return aShort;
    }

    public String getString() {
        return string;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Type getEnum() {
        return aEnum;
    }

    public enum Type {
        SMALL(0),
        MEDIUM(1),
        HUGE(2);

        private final int index;

        Type(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }
}
