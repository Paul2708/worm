package de.paul2708.worm;

import de.paul2708.worm.columns.AutoGenerated;
import de.paul2708.worm.columns.Column;
import de.paul2708.worm.columns.PrimaryKey;
import de.paul2708.worm.columns.Table;
import de.paul2708.worm.columns.generator.IntegerGenerator;

import java.util.Map;

@Table("collection_entities")
public class CollectionEntity {

    @PrimaryKey
    @AutoGenerated(IntegerGenerator.class)
    @Column("id")
    private int id;

    // TODO: Should be removed after we fixed the issue that at least one "normal" column must be present
    @Column("to_be_deleted")
    private String toBeDeleted = "to be deleted";

    @Column("mapping")
    private Map<String, Integer> map;

    public CollectionEntity() {

    }

    public void setMap(Map<String, Integer> map) {
        this.map = map;
    }

    public Map<String, Integer> getMap() {
        return map;
    }

    public int getId() {
        return id;
    }
}
