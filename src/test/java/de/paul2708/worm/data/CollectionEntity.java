package de.paul2708.worm.data;

import de.paul2708.worm.attributes.Attribute;
import de.paul2708.worm.attributes.Identifier;
import de.paul2708.worm.attributes.Entity;
import de.paul2708.worm.attributes.MaxLength;
import de.paul2708.worm.attributes.generator.IntegerGenerator;

import java.util.HashMap;
import java.util.Map;

@Entity("collection_entities")
public class CollectionEntity {

    @Identifier(generator = IntegerGenerator.class)
    @Attribute("id")
    private int id;

    @Attribute("mapping")
    private Map<String, Integer> map;

    @MaxLength(1024)
    @Attribute("array")
    private long[] array;

    public CollectionEntity() {
        this.map = new HashMap<>();
        this.array = new long[0];
    }

    public void resetId() {
        this.id = 0;
    }

    public void setMap(Map<String, Integer> map) {
        this.map = map;
    }

    public void setArray(long[] array) {
        this.array = array;
    }

    public Map<String, Integer> getMap() {
        return map;
    }

    public long[] getArray() {
        return array;
    }

    public int getId() {
        return id;
    }
}
