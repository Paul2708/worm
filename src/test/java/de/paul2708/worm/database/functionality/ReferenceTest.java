package de.paul2708.worm.database.functionality;

import de.paul2708.worm.data.Fleet;
import de.paul2708.worm.data.FleetRepository;
import de.paul2708.worm.data.Person;
import de.paul2708.worm.data.PersonRepository;
import de.paul2708.worm.database.Database;
import de.paul2708.worm.repository.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public abstract class ReferenceTest extends DatabaseTestBase {

    private PersonRepository personRepository;
    private FleetRepository fleetRepository;

    @BeforeEach
    void resetDatabase() {
        Database emptyDatabase = provider().createEmptyDatabase();
        emptyDatabase.connect();

        this.personRepository = Repository.create(PersonRepository.class, Person.class, emptyDatabase);
        this.fleetRepository = Repository.create(FleetRepository.class, Fleet.class, emptyDatabase);

        assumeTrue(personRepository.findAll().isEmpty());
        assumeTrue(fleetRepository.findAll().isEmpty());
    }

    @Test
    void testReference() {
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
    void testAlreadyExistingReference() {
        Person person = personRepository.save(new Person("Max", 42));
        Fleet fleet = fleetRepository.save(new Fleet("My Fleet", person));

        // Test stored fleet
        assertFalse(fleetRepository.findAll().isEmpty());
        Optional<Fleet> optionalFleet = fleetRepository.findById(fleet.getId());
        assertTrue(optionalFleet.isPresent());

        Fleet restoredFleet = optionalFleet.get();

        assertNotNull(restoredFleet.getPerson());
        assertEquals(person, restoredFleet.getPerson());

        // Test duplicated reference
        assertEquals(1, personRepository.findAll().size());
    }
}
