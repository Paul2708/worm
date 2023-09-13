package de.paul2708.worm;

import de.paul2708.worm.columns.Column;
import de.paul2708.worm.columns.MaxLength;
import de.paul2708.worm.columns.Identifier;
import de.paul2708.worm.columns.Table;

@Table("small_entities")
public class SmallEntity {

    @Identifier
    @MaxLength(16)
    @Column("name")
    private String name;

    public SmallEntity() {

    }

    public SmallEntity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
