package de.paul2708.worm.database.functionality;

import de.paul2708.worm.data.Car;
import de.paul2708.worm.data.CarRepository;
import de.paul2708.worm.database.Database;
import de.paul2708.worm.repository.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public abstract class IdentifierTest extends DatabaseTestBase {

    private CarRepository carRepository;

    @BeforeEach
    void resetDatabase() {
        Database emptyDatabase = provider().createEmptyDatabase();
        emptyDatabase.connect();

        this.carRepository = Repository.create(CarRepository.class, Car.class, emptyDatabase);
        assumeTrue(carRepository.findAll().isEmpty());
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
}
