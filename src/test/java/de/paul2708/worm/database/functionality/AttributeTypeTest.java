package de.paul2708.worm.database.functionality;

import de.paul2708.worm.data.BasicEntity;
import de.paul2708.worm.data.BasicEntityRepository;
import de.paul2708.worm.database.Database;
import de.paul2708.worm.repository.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public abstract class AttributeTypeTest extends DatabaseTestBase {

    private BasicEntityRepository repository;

    @BeforeEach
    void resetDatabase() {
        Database emptyDatabase = provider().createEmptyDatabase();
        emptyDatabase.connect();

        this.repository = Repository.create(BasicEntityRepository.class, BasicEntity.class, emptyDatabase);
        assumeTrue(repository.findAll().isEmpty());
    }

    @Test
    void testBooleanDataType() {
        BasicEntity entity = new BasicEntity();

        entity.setBoolean(true);
        assertTrue(saveAndFind(entity).getBoolean());

        entity.setBoolean(false);
        assertFalse(saveAndFind(entity).getBoolean());
    }

    @Test
    void testByteDataType() {
        BasicEntity entity = new BasicEntity();

        entity.setByte((byte) 42);
        assertEquals((byte) 42, saveAndFind(entity).getByte());

        entity.setByte(Byte.MIN_VALUE);
        assertEquals(Byte.MIN_VALUE, saveAndFind(entity).getByte());

        entity.setByte(Byte.MAX_VALUE);
        assertEquals(Byte.MAX_VALUE, saveAndFind(entity).getByte());
    }

    @Test
    void testDoubleDataType() {
        BasicEntity entity = new BasicEntity();

        entity.setDouble(Math.PI);
        assertEquals(Math.PI, saveAndFind(entity).getDouble(), 0.0001);

        entity.setDouble(Double.MIN_VALUE);
        assertEquals(Double.MIN_VALUE, saveAndFind(entity).getDouble(), 0.0001);

        entity.setDouble(Double.MAX_VALUE);
        assertEquals(Double.MAX_VALUE, saveAndFind(entity).getDouble(), 0.0001);
    }

    @Test
    void testIntDataType() {
        BasicEntity entity = new BasicEntity();

        entity.setInt(133742);
        assertEquals(133742, saveAndFind(entity).getInt());

        entity.setInt(Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, saveAndFind(entity).getInt());

        entity.setInt(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, saveAndFind(entity).getInt());
    }

    @Test
    void testLongDataType() {
        BasicEntity entity = new BasicEntity();

        entity.setLong(((long) Integer.MAX_VALUE) + 1337L);
        assertEquals(((long) Integer.MAX_VALUE) + 1337L, saveAndFind(entity).getLong());

        entity.setLong(Long.MIN_VALUE);
        assertEquals(Long.MIN_VALUE, saveAndFind(entity).getLong());

        entity.setLong(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, saveAndFind(entity).getLong());
    }

    @Test
    void testShortDataType() {
        BasicEntity entity = new BasicEntity();

        entity.setShort((short) 42);
        assertEquals((short) 42, saveAndFind(entity).getShort());

        entity.setShort(Short.MIN_VALUE);
        assertEquals(Short.MIN_VALUE, saveAndFind(entity).getShort());

        entity.setShort(Short.MAX_VALUE);
        assertEquals(Short.MAX_VALUE, saveAndFind(entity).getShort());
    }

    @Test
    void testStringDataType() {
        BasicEntity entity = new BasicEntity();

        entity.setString("Hello World");
        assertEquals("Hello World", saveAndFind(entity).getString());

        entity.setString("");
        assertEquals("", saveAndFind(entity).getString());

        entity.setString("τϠĄ@");
        assertEquals("τϠĄ@", saveAndFind(entity).getString());
    }

    @Test
    void testUuidDataType() {
        BasicEntity entity = new BasicEntity();

        entity.setUuid(UUID.fromString("bc3b3f26-e70c-4c66-bdec-5bd7246967bc"));
        assertEquals(UUID.fromString("bc3b3f26-e70c-4c66-bdec-5bd7246967bc"), saveAndFind(entity).getUuid());
    }

    @Test
    void testEnumDataType() {
        BasicEntity entity = new BasicEntity();

        entity.setEnum(BasicEntity.Type.SMALL);
        assertEquals(BasicEntity.Type.SMALL, saveAndFind(entity).getEnum());

        entity.setEnum(BasicEntity.Type.MEDIUM);
        assertEquals(BasicEntity.Type.MEDIUM, saveAndFind(entity).getEnum());

        entity.setEnum(BasicEntity.Type.HUGE);
        assertEquals(BasicEntity.Type.HUGE, saveAndFind(entity).getEnum());
    }

    private BasicEntity saveAndFind(BasicEntity entity) {
        BasicEntity stored = repository.save(entity);
        Optional<BasicEntity> entityOpt = repository.findById(stored.getId());
        assertTrue(entityOpt.isPresent());

        return entityOpt.get();
    }

    // TODO: Test reserved keywords
}
