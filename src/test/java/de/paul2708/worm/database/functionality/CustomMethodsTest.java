package de.paul2708.worm.database.functionality;

import de.paul2708.worm.data.Person;
import de.paul2708.worm.data.PersonRepository;
import de.paul2708.worm.database.Database;
import de.paul2708.worm.repository.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public abstract class CustomMethodsTest extends DatabaseTestBase {

    private PersonRepository repository;

    @BeforeEach
    void resetDatabase() {
        Database emptyDatabase = provider().createEmptyDatabase();
        emptyDatabase.connect();

        this.repository = Repository.create(PersonRepository.class, Person.class, emptyDatabase);
        assumeTrue(repository.findAll().isEmpty());
    }

    @Test
    void testFindByNameAndAge() {
        Person youngAlice = repository.save(new Person("Alice", 24));
        repository.save(new Person("Bob", 24));
        repository.save(new Person("Alice", 42));

        assumeTrue(repository.findAll().size() == 3);

        Optional<Person> aliceOpt = repository.findByNameAndAge("Alice", 24);

        assertTrue(aliceOpt.isPresent());
        assertEquals(youngAlice, aliceOpt.get());
    }

    @Test
    void testEmptyFindByNameAndAge() {
        repository.save(new Person("Alice", 24));
        repository.save(new Person("Bob", 24));
        repository.save(new Person("Alice", 42));

        assumeTrue(repository.findAll().size() == 3);

        Optional<Person> aliceOpt = repository.findByNameAndAge("Bob", 42);
        assertTrue(aliceOpt.isEmpty());
    }

    @Test
    void testMultipleFindByNameAndAge() {
        repository.save(new Person("Alice", 24));
        repository.save(new Person("Bob", 24));
        repository.save(new Person("Alice", 24));

        assumeTrue(repository.findAll().size() == 3);

        assertThrows(RuntimeException.class, () -> {
            repository.findByNameAndAge("Alice", 24);
        });
    }

    @Test
    void testFindByName() {
        Person aliceA = repository.save(new Person("Alice", 24));
        repository.save(new Person("Bob", 24));
        Person aliceB = repository.save(new Person("Alice", 42));

        assumeTrue(repository.findAll().size() == 3);

        List<Person> alices = repository.findByName("Alice");
        assertIgnoringOrder(List.of(aliceA, aliceB), alices);
    }

    @Test
    void testEmptyFindByName() {
        repository.save(new Person("Alice", 24));
        repository.save(new Person("Bob", 24));
        repository.save(new Person("Sam", 42));

        assumeTrue(repository.findAll().size() == 3);

        List<Person> alices = repository.findByName("Paul");
        assertTrue(alices.isEmpty());
    }

    @Test
    void testFindByUnknownColumn() {
        repository.save(new Person("Alice", 24));
        repository.save(new Person("Bob", 24));
        repository.save(new Person("Sam", 42));

        assumeTrue(repository.findAll().size() == 3);

        assertThrows(IllegalArgumentException.class, () -> {
            repository.findByInvalid("unknown");
        });
    }

    @Test
    void testDefaultMethod() {
        repository.save(new Person("Alice", 24));
        Person bob = repository.save(new Person("Bob", 24));
        repository.save(new Person("Sam", 42));

        assumeTrue(repository.findAll().size() == 3);

        List<Person> persons = repository.findByNameBob();
        assertIgnoringOrder(List.of(bob), persons);
    }

    @Test
    void testArbitraryDefaultMethod() {
        assertEquals("bar", repository.foo());
    }

    private <T> void assertIgnoringOrder(List<T> expected, List<T> actual) {
        Set<T> expectedSet = new HashSet<>(expected);
        Set<T> actualSet = new HashSet<>(actual);

        assertEquals(expectedSet, actualSet);
    }
}
