package de.paul2708.worm.data;

import de.paul2708.worm.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends CrudRepository<Person, Integer> {

    Optional<Person> findByNameAndAge(String name, int age);

    List<Person> findByName(String name);

    Optional<Person> findByInvalid(String invalid);

    default List<Person> findByNameBob() {
        return findByName("Bob");
    }

    default String foo() {
        return "bar";
    }
}