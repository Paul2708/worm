package de.paul2708.worm.data;

import de.paul2708.worm.attributes.*;
import de.paul2708.worm.attributes.generator.UUIDGenerator;

import java.util.UUID;

@Entity("fleets")
public class Fleet {

    @Identifier(generator = UUIDGenerator.class)
    @Attribute("id")
    private UUID id;

    @Attribute("name")
    private String name;

    @Reference
    @Attribute("owner_id")
    private Person person;

    public Fleet() {

    }

    public Fleet(String name, Person person) {
        this.name = name;
        this.person = person;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Person getPerson() {
        return person;
    }
}
