package de.paul2708.worm.data;

import de.paul2708.worm.attributes.*;
import de.paul2708.worm.attributes.generator.IntegerGenerator;

import java.util.Objects;

@Entity("persons")
public class Person {

    @Identifier(generator = IntegerGenerator.class)
    @Attribute("id")
    private int id;

    @MaxLength(255)
    @Attribute("name")
    private String name;

    @Attribute("age")
    private int age;

    @Attribute("blocked")
    private boolean blocked;

    public Person() {

    }

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Person person = (Person) object;
        return id == person.id && age == person.age && Objects.equals(name, person.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, age);
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
