package de.paul2708.worm.example.database;

import de.paul2708.worm.database.Database;
import de.paul2708.worm.example.Person;
import de.paul2708.worm.example.PersonRepository;
import de.paul2708.worm.repository.CrudRepository;
import de.paul2708.worm.repository.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public abstract class DatabaseTest {

    private final CrudRepository<Person, Integer> repository;

    public DatabaseTest(Database database) {
        database.connect();

        this.repository = Repository.create(PersonRepository.class, Person.class, database);
    }

    public abstract void clearDatabase();

    @BeforeEach
    void resetDatabase() {
        clearDatabase();

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
    void testSavingExistingPerson() {
        Person existingPerson = new Person("Max", 42);
        existingPerson = repository.save(existingPerson);

        assumeTrue(!repository.findAll().isEmpty());

        Person person = new Person("Paul", 19);
        person.setId(existingPerson.getId());

        person = repository.save(person);

        assertEquals(1, repository.findAll().size());
        assertEquals("Paul", repository.findAll().iterator().next().getName());
        assertEquals(19, repository.findAll().iterator().next().getAge());
    }
}
