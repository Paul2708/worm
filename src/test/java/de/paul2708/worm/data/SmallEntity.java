package de.paul2708.worm.data;

import de.paul2708.worm.attributes.Attribute;
import de.paul2708.worm.attributes.MaxLength;
import de.paul2708.worm.attributes.Identifier;
import de.paul2708.worm.attributes.Entity;

@Entity("small_entities")
public class SmallEntity {

    @Identifier
    @MaxLength(16)
    @Attribute("name")
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
