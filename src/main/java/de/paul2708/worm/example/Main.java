package de.paul2708.worm.example;

import de.paul2708.worm.database.Database;
import de.paul2708.worm.database.InMemoryDatabase;
import de.paul2708.worm.database.sql.MySQLDatabase;
import de.paul2708.worm.repository.Repository;


public class Main {

    public static void main(String[] args) {
        Database database = new InMemoryDatabase();
        database = new MySQLDatabase("host.docker.internal", 3306, "worm", "worm", "worm_password");
        database.connect();

        PersonRepository repository = Repository.create(PersonRepository.class, Person.class, database);

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