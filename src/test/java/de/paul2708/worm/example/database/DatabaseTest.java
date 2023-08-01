package de.paul2708.worm.example.database;

import de.paul2708.worm.database.Database;
import de.paul2708.worm.example.Person;
import de.paul2708.worm.example.PersonRepository;
import de.paul2708.worm.repository.CrudRepository;
import de.paul2708.worm.repository.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public abstract class DatabaseTest {

    private CrudRepository<Person, Integer> repository;

    public abstract Database createEmptyDatabase();

    @BeforeEach
    void resetDatabase() {
        Database emptyDatabase = createEmptyDatabase();
        emptyDatabase.connect();

        this.repository = Repository.create(PersonRepository.class, Person.class, emptyDatabase);

        assumeTrue(repository.findAll().isEmpty());
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
        Person person = repository.save(new Person("Max", 42));
        assumeTrue(repository.findAll().size() == 1);

        int id = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
        assumeTrue(person.getId() != id);
    }
}