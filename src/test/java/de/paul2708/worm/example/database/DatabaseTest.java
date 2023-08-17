package de.paul2708.worm.example.database;

import de.paul2708.worm.database.Database;
import de.paul2708.worm.example.Car;
import de.paul2708.worm.example.CarRepository;
import de.paul2708.worm.example.Person;
import de.paul2708.worm.example.PersonRepository;
import de.paul2708.worm.repository.CrudRepository;
import de.paul2708.worm.repository.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public abstract class DatabaseTest {

    private CrudRepository<Person, Integer> repository;
    private CarRepository carRepository;

    public abstract Database createEmptyDatabase();

    @BeforeEach
    void resetDatabase() {
        Database emptyDatabase = createEmptyDatabase();
        emptyDatabase.connect();

        this.repository = Repository.create(PersonRepository.class, Person.class, emptyDatabase);
        this.carRepository = Repository.create(CarRepository.class, Car.class, emptyDatabase);

        assumeTrue(repository.findAll().isEmpty());
        assumeTrue(carRepository.findAll().isEmpty());
    }

    @Test
    void testBasicSave() {
        Person person = new Person("Max", 42);
        repository.save(person);

        assertEquals(1, repository.findAll().size());
    }

    @Test
    void testGeneratedKeyOnSave() {
        Person person = new Person("Max", 42);

        assumeTrue(person.getId() == 0);

        Person savedPerson = repository.save(person);
        assertTrue(savedPerson.getId() != 0);
    }

    @Test
    void testSavedAttributes() {
        Person person = new Person("Max", 42);
        Person savedPerson = repository.save(person);

        assertEquals(person.getName(), savedPerson.getName());
        assertEquals(person.getAge(), savedPerson.getAge());
    }

    @Test
    void testFindByValidId() {
        Person person = repository.save(new Person("Max", 42));
        int id = person.getId();

        Optional<Person> optionalPerson = repository.findById(id);
        assertTrue(optionalPerson.isPresent());

        Person foundPerson = optionalPerson.get();
        assertEquals("Max", foundPerson.getName());
        assertEquals(42, foundPerson.getAge());
        assertEquals(id, foundPerson.getId());
    }

    @Test
    void testFindByInvalidId() {
        Optional<Person> optionalPerson = repository.findById(42);
        assertTrue(optionalPerson.isEmpty());
    }

    @Test
    void testUpdate() {
        Person person = repository.save(new Person("Max", 42));
        int id = person.getId();

        Optional<Person> optionalPerson = repository.findById(id);
        assertTrue(optionalPerson.isPresent());

        Person existingPerson = optionalPerson.get();
        existingPerson.setName("Paul");
        System.out.println(existingPerson.getId());

        repository.save(existingPerson);

        assertEquals(1, repository.findAll().size());
        assertEquals("Paul", repository.findById(existingPerson.getId()).get().getName());
    }

    @Test
    void testSuccessfulDeletion() {
        Person person = repository.save(new Person("Max", 42));

        assumeTrue(repository.findById(person.getId()).isPresent());

        repository.delete(person);
        assertTrue(repository.findById(person.getId()).isEmpty());
    }

    @Test
    void testNonExistingDeletion() {
        repository.save(new Person("Max", 42));
        assumeTrue(repository.findAll().size() == 1);

        repository.delete(new Person("Max", 42));

        assertEquals(1, repository.findAll().size());
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
    void testBooleanDataType() {
        Person person = repository.save(new Person("Max", 42));
        assumeFalse(person.isBlocked());

        person.setBlocked(true);
        repository.save(person);

        Optional<Person> personOptional = repository.findById(person.getId());
        assumeTrue(personOptional.isPresent());

        assertTrue(personOptional.get().isBlocked());
    }

    @Test
    void testColumnMaxLength() {
        Person person = repository.save(new Person("Max", 42));
        assumeTrue(person.getName().length() <= 255);

        assertThrows(IllegalStateException.class, () -> {
            person.setName("a".repeat(256));
            repository.save(person);
        });

        Optional<Person> personOptional = repository.findById(person.getId());
        assumeTrue(personOptional.isPresent());

        assertEquals("Max", personOptional.get().getName());
    }

    @Test
    void testByteArray() {
        Person person = repository.save(new Person("Max", 42));

        // Fill random bytes into the array
        for (int i = 0; i < 255; i++) {
            byte random = (byte) (Math.random() * 255);
            person.getImage()[i] = random;
        }

        repository.save(person);

        Optional<Person> personOptional = repository.findById(person.getId());
        assumeTrue(personOptional.isPresent());

        assertEquals(255, personOptional.get().getImage().length);

        assertThrows(IllegalStateException.class, () -> {
            person.setImage(new byte[256]);
            repository.save(person);
        });
    }
}
