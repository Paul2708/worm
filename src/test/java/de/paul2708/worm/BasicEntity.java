package de.paul2708.worm;

import de.paul2708.worm.columns.AutoGenerated;
import de.paul2708.worm.columns.Column;
import de.paul2708.worm.columns.Identifier;
import de.paul2708.worm.columns.Entity;
import de.paul2708.worm.columns.generator.IntegerGenerator;

import java.util.UUID;

@Entity("basic_entities")
public class BasicEntity {

    @Identifier
    @AutoGenerated(IntegerGenerator.class)
    @Column("id")
    private int id;

    @Column("a_boolean")
    private boolean aBoolean;

    @Column("a_byte")
    private byte aByte;

    @Column("a_double")
    private double aDouble;

    @Column("an_int")
    private int anInt;

    @Column("a_long")
    private long aLong;

    @Column("a_short")
    private short aShort;

    @Column("a_string")
    private String string;

    @Column("a_uuid")
    private UUID uuid;

    @Column("an_enum")
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
