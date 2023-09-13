package de.paul2708.worm;

import de.paul2708.worm.columns.AutoGenerated;
import de.paul2708.worm.columns.Column;
import de.paul2708.worm.columns.Identifier;
import de.paul2708.worm.columns.Table;
import de.paul2708.worm.columns.generator.UUIDGenerator;

import java.util.Objects;
import java.util.UUID;

@Table("cars")
public final class Car {

    @Identifier
    @AutoGenerated(UUIDGenerator.class)
    @Column("id")
    private UUID id;

    @Column("color")
    private String color;

    @Column("owner")
    private UUID ownerId;

    public Car() {

    }

    public Car(String color, UUID ownerId) {
        this.color = color;
        this.ownerId = ownerId;
    }

    public UUID id() {
        return id;
    }

    public String color() {
        return color;
    }

    public UUID ownerId() {
        return ownerId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Car) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.color, that.color) &&
                Objects.equals(this.ownerId, that.ownerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, color, ownerId);
    }

    @Override
    public String toString() {
        return "Car[" +
                "id=" + id + ", " +
                "color=" + color + ", " +
                "ownerId=" + ownerId + ']';
    }
}
