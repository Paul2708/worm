package de.paul2708.worm.example;

import de.paul2708.worm.repository.CrudRepository;

import java.util.Optional;

public interface PersonRepository extends CrudRepository<Person, Integer> {

    Optional<Person> findByName(String name);
}