package de.paul2708.worm.example;

import de.paul2708.worm.columns.ForeignKey;
import de.paul2708.worm.columns.Column;
import de.paul2708.worm.columns.PrimaryKey;
import de.paul2708.worm.columns.Table;

import java.util.UUID;

@Table("fleets")
public class Fleet {

    @PrimaryKey
    @Column("id")
    private UUID id;

    @Column("name")
    private String name;

    @ForeignKey
    @Column("owner_id")
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
