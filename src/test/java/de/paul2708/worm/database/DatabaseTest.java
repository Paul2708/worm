package de.paul2708.worm.database;

import de.paul2708.worm.*;
import de.paul2708.worm.repository.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public abstract class DatabaseTest {

    private PersonRepository personRepository;
    private CarRepository carRepository;
    private FleetRepository fleetRepository;
    private RoundRepository roundRepository;
    private CollectorRepository collectorRepository;
    private BasicEntityRepository basicEntityRepository;
    private CollectionEntityRepository collectionEntityRepository;
    private SmallEntityRepository smallEntityRepository;

    public abstract Database createEmptyDatabase();

    @BeforeEach
    void resetDatabase() {
        Database emptyDatabase = createEmptyDatabase();
        emptyDatabase.connect();

        this.personRepository = Repository.create(PersonRepository.class, Person.class, emptyDatabase);
        this.carRepository = Repository.create(CarRepository.class, Car.class, emptyDatabase);
        this.fleetRepository = Repository.create(FleetRepository.class, Fleet.class, emptyDatabase);
        this.roundRepository = Repository.create(RoundRepository.class, Round.class, emptyDatabase);
        this.collectorRepository = Repository.create(CollectorRepository.class, Collector.class, emptyDatabase);
        this.basicEntityRepository = Repository.create(BasicEntityRepository.class, BasicEntity.class, emptyDatabase);
        this.collectionEntityRepository = Repository.create(CollectionEntityRepository.class, CollectionEntity.class,
                emptyDatabase);
        this.smallEntityRepository = Repository.create(SmallEntityRepository.class, SmallEntity.class, emptyDatabase);

        assumeTrue(personRepository.findAll().isEmpty());
        assumeTrue(carRepository.findAll().isEmpty());
        assumeTrue(fleetRepository.findAll().isEmpty());
        assumeTrue(roundRepository.findAll().isEmpty());
        assumeTrue(collectorRepository.findAll().isEmpty());
        assumeTrue(basicEntityRepository.findAll().isEmpty());
        assumeTrue(collectionEntityRepository.findAll().isEmpty());
        assumeTrue(smallEntityRepository.findAll().isEmpty());
    }

    @Test
    void testBasicSave() {
        Person person = new Person("Max", 42);
        personRepository.save(person);

        assertEquals(1, personRepository.findAll().size());
    }

    @Test
    void testGeneratedKeyOnSave() {
        Person person = new Person("Max", 42);

        assumeTrue(person.getId() == 0);

        Person savedPerson = personRepository.save(person);
        assertTrue(savedPerson.getId() != 0);
    }

    @Test
    void testSavedAttributes() {
        Person person = new Person("Max", 42);
        Person savedPerson = personRepository.save(person);

        assertEquals(person.getName(), savedPerson.getName());
        assertEquals(person.getAge(), savedPerson.getAge());
    }

    @Test
    void testFindByValidId() {
        Person person = personRepository.save(new Person("Max", 42));
        int id = person.getId();

        Optional<Person> optionalPerson = personRepository.findById(id);
        assertTrue(optionalPerson.isPresent());

        Person foundPerson = optionalPerson.get();
        assertEquals("Max", foundPerson.getName());
        assertEquals(42, foundPerson.getAge());
        assertEquals(id, foundPerson.getId());
    }

    @Test
    void testFindByInvalidId() {
        Optional<Person> optionalPerson = personRepository.findById(42);
        assertTrue(optionalPerson.isEmpty());
    }

    @Test
    void testUpdate() {
        Person person = personRepository.save(new Person("Max", 42));
        int id = person.getId();

        Optional<Person> optionalPerson = personRepository.findById(id);
        assertTrue(optionalPerson.isPresent());

        Person existingPerson = optionalPerson.get();
        existingPerson.setName("Paul");

        personRepository.save(existingPerson);

        assertEquals(1, personRepository.findAll().size());
        assertTrue(personRepository.findById(existingPerson.getId()).isPresent());
        assertEquals("Paul", personRepository.findById(existingPerson.getId()).get().getName());
    }

    @Test
    void testSuccessfulDeletion() {
        Person person = personRepository.save(new Person("Max", 42));

        assumeTrue(personRepository.findById(person.getId()).isPresent());

        personRepository.delete(person);
        assertTrue(personRepository.findById(person.getId()).isEmpty());
    }

    @Test
    void testNonExistingDeletion() {
        personRepository.save(new Person("Max", 42));
        assumeTrue(personRepository.findAll().size() == 1);

        personRepository.delete(new Person("Max", 42));

        assertEquals(1, personRepository.findAll().size());
    }

    @Test
    void testCarDatabase() {
        UUID ownerId = UUID.randomUUID();
        Car car = new Car("red", ownerId);

        assumeTrue(car.id() == null);

        Car savedCar = carRepository.save(car);
        assertNotNull(savedCar.id());

        Optional<Car> carOptional = carRepository.findById(savedCar.id());
        assertTrue(carOptional.isPresent());

        assertEquals(savedCar.id(), carOptional.get().id());
        assertEquals("red", carOptional.get().color());
        assertEquals(ownerId, carOptional.get().ownerId());
    }

    @Test
    void testBooleanDataTypeInPerson() {
        Person person = personRepository.save(new Person("Max", 42));
        assumeFalse(person.isBlocked());

        person.setBlocked(true);
        personRepository.save(person);

        Optional<Person> personOptional = personRepository.findById(person.getId());
        assumeTrue(personOptional.isPresent());

        assertTrue(personOptional.get().isBlocked());
    }

    @Test
    void testForeignKey() {
        assumeTrue(personRepository.findAll().isEmpty());

        // Save fleet
        Fleet fleet = fleetRepository.save(new Fleet("My Fleet", new Person("Max", 42)));

        assertEquals("Max", fleet.getPerson().getName());
        assertEquals(42, fleet.getPerson().getAge());
        assertNotEquals(0, fleet.getPerson().getId());

        // Test saved person
        assertFalse(personRepository.findAll().isEmpty());
        Optional<Person> optionalPerson = personRepository.findById(fleet.getPerson().getId());
        assertTrue(optionalPerson.isPresent());

        Person person = optionalPerson.get();

        assertEquals("Max", person.getName());
        assertEquals(42, person.getAge());
        assertEquals(fleet.getPerson().getId(), person.getId());

        // Test saved person in fleet
        assertFalse(fleetRepository.findAll().isEmpty());
        Optional<Fleet> optionalFleet = fleetRepository.findById(fleet.getId());
        assertTrue(optionalFleet.isPresent());

        Fleet restoredFleet = optionalFleet.get();

        assertNotNull(restoredFleet.getPerson());
        assertEquals(person, restoredFleet.getPerson());
    }

    @Test
    void testAlreadyExistingForeignKey() {
        Person person = personRepository.save(new Person("Max", 42));
        Fleet fleet = fleetRepository.save(new Fleet("My Fleet", person));

        // Test stored fleet
        assertFalse(fleetRepository.findAll().isEmpty());
        Optional<Fleet> optionalFleet = fleetRepository.findById(fleet.getId());
        assertTrue(optionalFleet.isPresent());

        Fleet restoredFleet = optionalFleet.get();

        assertNotNull(restoredFleet.getPerson());
        assertEquals(person, restoredFleet.getPerson());

        // Test duplicated foreign entity
        assertEquals(1, personRepository.findAll().size());
    }

    @Test
    void testDateTimeDataType() {
        LocalDateTime startTime = LocalDateTime.of(2023, 8, 27, 10, 0);
        LocalDateTime endTime = LocalDateTime.of(2023, 8, 27, 10, 45);

        int id = roundRepository.save(new Round(startTime, endTime)).getId();

        Optional<Round> optionalRound = roundRepository.findById(id);
        assertTrue(optionalRound.isPresent());

        Round round = optionalRound.get();
        assertEquals(startTime, round.getStartTime());
        assertEquals(endTime, round.getEndTime());
    }

    @Test
    void testCreatedAt() {
        LocalDateTime startOperationTime = LocalDateTime.now();

        Round round = roundRepository.save(new Round(LocalDateTime.now(), LocalDateTime.now().plusHours(1)));

        assertNotNull(round.getCreatedAt());

        assertInBetween(startOperationTime, LocalDateTime.now(), round.getCreatedAt());
    }

    @Test
    void testUnmodifiedCreatedAt() {
        Round round = roundRepository.save(new Round(LocalDateTime.now(), LocalDateTime.now().plusHours(1)));
        LocalDateTime createdAt = round.getCreatedAt();

        round.setEndTime(LocalDateTime.now().plusHours(2));
        Round updatedRound = roundRepository.save(round);

        assertNotNull(createdAt);
        assertEquals(createdAt, updatedRound.getCreatedAt());
    }

    @Test
    void testInitialUpdatedAt() {
        LocalDateTime startOperationTime = LocalDateTime.now();
        Round round = roundRepository.save(new Round(LocalDateTime.now(), LocalDateTime.now().plusHours(1)));

        assertInBetween(startOperationTime, LocalDateTime.now(), round.getUpdatedAt());
    }

    @Test
    void testUpdatedAt() {
        Round round = roundRepository.save(new Round(LocalDateTime.now(), LocalDateTime.now().plusHours(1)));
        LocalDateTime updatedAt = round.getUpdatedAt();

        // Ensure that time passed between the last updated
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        round.setEndTime(LocalDateTime.now().plusHours(2));
        Round updatedRound = roundRepository.save(round);

        assertNotEquals(updatedAt, updatedRound.getUpdatedAt());
        assertInBetween(updatedAt, LocalDateTime.now(), updatedRound.getUpdatedAt());
    }

    private void assertInBetween(LocalDateTime min, LocalDateTime max, LocalDateTime current) {
        if (min.isEqual(current) || max.isEqual(current) || (current.isAfter(min) && current.isBefore(max))) {
            return;
        }

        fail("Date %s should be between %s and %s".formatted(current, min, max));
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

    // TODO: Test entity with exactly one primary key


    @Test
    void testFindByNameAndAge() {
        Person youngAlice = personRepository.save(new Person("Alice", 24));
        personRepository.save(new Person("Bob", 24));
        personRepository.save(new Person("Alice", 42));

        assumeTrue(personRepository.findAll().size() == 3);

        Optional<Person> aliceOpt = personRepository.findByNameAndAge("Alice", 24);

        assertTrue(aliceOpt.isPresent());
        assertEquals(youngAlice, aliceOpt.get());
    }

    @Test
    void testEmptyFindByNameAndAge() {
        personRepository.save(new Person("Alice", 24));
        personRepository.save(new Person("Bob", 24));
        personRepository.save(new Person("Alice", 42));

        assumeTrue(personRepository.findAll().size() == 3);

        Optional<Person> aliceOpt = personRepository.findByNameAndAge("Bob", 42);
        assertTrue(aliceOpt.isEmpty());
    }

    @Test
    void testMultipleFindByNameAndAge() {
        personRepository.save(new Person("Alice", 24));
        personRepository.save(new Person("Bob", 24));
        personRepository.save(new Person("Alice", 24));

        assumeTrue(personRepository.findAll().size() == 3);

        assertThrows(RuntimeException.class, () -> {
            personRepository.findByNameAndAge("Alice", 24);
        });
    }

    @Test
    void testFindByName() {
        Person aliceA = personRepository.save(new Person("Alice", 24));
        personRepository.save(new Person("Bob", 24));
        Person aliceB = personRepository.save(new Person("Alice", 42));

        assumeTrue(personRepository.findAll().size() == 3);

        List<Person> alices = personRepository.findByName("Alice");
        assertIgnoringOrder(List.of(aliceA, aliceB), alices);
    }

    @Test
    void testEmptyFindByName() {
        personRepository.save(new Person("Alice", 24));
        personRepository.save(new Person("Bob", 24));
        personRepository.save(new Person("Sam", 42));

        assumeTrue(personRepository.findAll().size() == 3);

        List<Person> alices = personRepository.findByName("Paul");
        assertTrue(alices.isEmpty());
    }

    @Test
    void testFindByUnknownColumn() {
        personRepository.save(new Person("Alice", 24));
        personRepository.save(new Person("Bob", 24));
        personRepository.save(new Person("Sam", 42));

        assumeTrue(personRepository.findAll().size() == 3);

        assertThrows(IllegalArgumentException.class, () -> {
            personRepository.findByInvalid("unknown");
        });
    }

    private <T> void assertIgnoringOrder(List<T> expected, List<T> actual) {
        Set<T> expectedSet = new HashSet<>(expected);
        Set<T> actualSet = new HashSet<>(actual);

        assertEquals(expectedSet, actualSet);
    }

    @Test
    void testDefaultMethod() {
        personRepository.save(new Person("Alice", 24));
        Person bob = personRepository.save(new Person("Bob", 24));
        personRepository.save(new Person("Sam", 42));

        assumeTrue(personRepository.findAll().size() == 3);

        List<Person> persons = personRepository.findByNameBob();
        assertIgnoringOrder(List.of(bob), persons);
    }

    @Test
    void testArbitraryDefaultMethod() {
        assertEquals("bar", personRepository.foo());
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
        BasicEntity stored = basicEntityRepository.save(entity);
        Optional<BasicEntity> entityOpt = basicEntityRepository.findById(stored.getId());
        assertTrue(entityOpt.isPresent());

        return entityOpt.get();
    }

    // TODO: Test reserved keywords


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
        CollectionEntity stored = collectionEntityRepository.save(entity);
        Optional<CollectionEntity> entityOpt = collectionEntityRepository.findById(stored.getId());
        assertTrue(entityOpt.isPresent());

        return entityOpt.get();
    }

    @Test
    void testOnlyPrimaryKey() {
        SmallEntity entity = new SmallEntity("Sam");
        smallEntityRepository.save(entity);

        Optional<SmallEntity> entityOpt = smallEntityRepository.findById("Sam");
        assertTrue(entityOpt.isPresent());
        assertEquals("Sam", entityOpt.get().getName());
    }
}
