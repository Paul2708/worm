package de.paul2708.worm.database.functionality;

import de.paul2708.worm.data.CollectionEntity;
import de.paul2708.worm.data.CollectionEntityRepository;
import de.paul2708.worm.data.Collector;
import de.paul2708.worm.data.CollectorRepository;
import de.paul2708.worm.database.Database;
import de.paul2708.worm.repository.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public abstract class CollectionsTest extends DatabaseTestBase {

    private CollectorRepository collectorRepository;
    private CollectionEntityRepository collectionEntityRepository;

    @BeforeEach
    void resetDatabase() {
        Database emptyDatabase = provider().createEmptyDatabase();
        emptyDatabase.connect();

        this.collectorRepository = Repository.create(CollectorRepository.class, Collector.class, emptyDatabase);
        this.collectionEntityRepository = Repository.create(CollectionEntityRepository.class, CollectionEntity.class,
                emptyDatabase);

        assumeTrue(collectorRepository.findAll().isEmpty());
        assumeTrue(collectionEntityRepository.findAll().isEmpty());
    }

    @Test
    void testStoringCollections() {
        Collector collector = new Collector("Collector 01", Set.of("Badge01", "Badge02"),
                List.of(2, 3, 5, 7, 11));
        collectorRepository.save(collector);

        Collector storedCollector = collectorRepository.findById("Collector 01").get();
        assertEquals(Set.of("Badge01", "Badge02"), storedCollector.getBadges());
        assertEquals(List.of(2, 3, 5, 7, 11), storedCollector.getPrimeNumbers());
    }

    @Test
    void testEmptyCollections() {
        Collector collector = new Collector("Collector 01", Set.of(), List.of());
        collectorRepository.save(collector);

        Collector storedCollector = collectorRepository.findById("Collector 01").get();
        assertTrue(storedCollector.getBadges().isEmpty());
        assertTrue(storedCollector.getPrimeNumbers().isEmpty());
    }

    @Test
    void testModifyingCollections() {
        Collector collector = new Collector("Collector 01", Set.of(),
                List.of(2, 3, 5));
        collectorRepository.save(collector);

        collector.addBadge("Badge 01");
        collector.removePrime(3);

        collectorRepository.save(collector);

        assertEquals(Set.of("Badge 01"), collector.getBadges());
        assertEquals(List.of(2, 5), collector.getPrimeNumbers());
    }

    @Test
    void testDeleteCollections() {
        Collector collector = new Collector("Collector 01", Set.of("Badge 01"), List.of(2, 3, 5));
        collectorRepository.save(collector);
        collectorRepository.delete(collector);

        collectorRepository.save(new Collector("Collector 01", Set.of("Badge 02"), List.of(2, 3)));
        Collector storedCollector = collectorRepository.findById("Collector 01").get();
        assertEquals(Set.of("Badge 02"), storedCollector.getBadges());
        assertEquals(List.of(2, 3), storedCollector.getPrimeNumbers());
    }

    @Test
    void testMapDataType() {
        CollectionEntity entity = new CollectionEntity();

        entity.setMap(Map.of("foo", 1337, "bar", 42));
        assertEquals(Map.of("foo", 1337, "bar", 42), saveAndFind(entity).getMap());

        entity.setMap(Map.of());
        assertEquals(Map.of(), saveAndFind(entity).getMap());

        entity.setMap(Map.of("foo", 1337));
        assertEquals(Map.of("foo", 1337), saveAndFind(entity).getMap());
    }

    @Test
    void testMapDataTypeDeletion() {
        CollectionEntity entity = new CollectionEntity();
        entity.setMap(Map.of("foo", 1337, "bar", 42));

        collectionEntityRepository.delete(collectionEntityRepository.save(entity));

        entity.setMap(Map.of("a", 1, "b", 2, "c", 3));
        assertEquals(Map.of("a", 1, "b", 2, "c", 3), saveAndFind(entity).getMap());
    }

    @Test
    void testArrayDataType() {
        CollectionEntity entity = new CollectionEntity();

        entity.setArray(new long[]{0, 42, 1337});
        assertArrayEquals(new long[]{0, 42, 1337}, saveAndFind(entity).getArray());

        entity.setArray(new long[]{});
        assertArrayEquals(new long[]{}, saveAndFind(entity).getArray());

        entity.setArray(new long[]{-123456789});
        assertArrayEquals(new long[]{-123456789}, saveAndFind(entity).getArray());

        entity.setArray(new long[1337]);
        assertArrayEquals(new long[1337], saveAndFind(entity).getArray());
    }

    @Test
    void testArrayDataTypeDeletion() {
        CollectionEntity entity = new CollectionEntity();
        entity.setArray(new long[]{0, 42, 1337});

        collectionEntityRepository.delete(collectionEntityRepository.save(entity));

        entity.setArray(new long[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
        assertArrayEquals(new long[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}, saveAndFind(entity).getArray());
    }

    private CollectionEntity saveAndFind(CollectionEntity entity) {
        entity.resetId();

        CollectionEntity stored = collectionEntityRepository.save(entity);
        Optional<CollectionEntity> entityOpt = collectionEntityRepository.findById(stored.getId());
        assertTrue(entityOpt.isPresent());

        return entityOpt.get();
    }
}
