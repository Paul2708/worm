package de.paul2708.worm.database.functionality;

import de.paul2708.worm.data.Person;
import de.paul2708.worm.data.PersonRepository;
import de.paul2708.worm.data.SmallEntity;
import de.paul2708.worm.data.SmallEntityRepository;
import de.paul2708.worm.database.Database;
import de.paul2708.worm.repository.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public abstract class BasicTest extends DatabaseTestBase {

    private PersonRepository repository;
    private SmallEntityRepository smallEntityRepository;

    @BeforeEach
    void resetDatabase() {
        Database emptyDatabase = provider().createEmptyDatabase();
        emptyDatabase.connect();

        this.repository = Repository.create(PersonRepository.class, Person.class, emptyDatabase);
        this.smallEntityRepository = Repository.create(SmallEntityRepository.class, SmallEntity.class, emptyDatabase);

        assumeTrue(repository.findAll().isEmpty());
        assumeTrue(smallEntityRepository.findAll().isEmpty());
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

        repository.save(existingPerson);

        assertEquals(1, repository.findAll().size());
        assertTrue(repository.findById(existingPerson.getId()).isPresent());
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
    void testOnlyIdentifier() {
        SmallEntity entity = new SmallEntity("Sam");
        smallEntityRepository.save(entity);

        Optional<SmallEntity> entityOpt = smallEntityRepository.findById("Sam");
        assertTrue(entityOpt.isPresent());
        assertEquals("Sam", entityOpt.get().getName());
    }

    @Test
    void testNullableAttribute() {
        Person person = new Person(null, 42);
        int id = repository.save(person).getId();

        Optional<Person> personOpt = repository.findById(id);
        assertTrue(personOpt.isPresent());
        assertNull(personOpt.get().getName());
    }
}
