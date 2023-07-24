package de.paul2708.worm.example;

import de.paul2708.worm.database.InMemoryDatabase;
import de.paul2708.worm.repository.Repository;


public class Main {

    public static void main(String[] args) {
        InMemoryDatabase database = new InMemoryDatabase();
        PersonRepository repository = Repository.create(PersonRepository.class, database);

        Person person = new Person("Paul", 23);
        person = repository.save(person);

        System.out.println(person);

        System.out.println(repository.findAll());

        System.out.println(repository.findById(0));
        System.out.println(repository.findById(person.getId()));

        repository.delete(person);

        System.out.println(repository.findAll());
    }
}