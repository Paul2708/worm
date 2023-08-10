package de.paul2708.worm.example;

import de.paul2708.worm.repository.CrudRepository;

import java.util.UUID;

public interface CarRepository extends CrudRepository<Car, UUID> {

}
