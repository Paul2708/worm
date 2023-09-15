# worm
worm (Worthless Object Relation Mapper) is an ORM for Java that is inspired by JPA and Spring Data.

To get started, check out the [wiki](https://github.com/Paul2708/worm/wiki) or jump right into the quick start below.

## Quick Start

1. Add worm as a dependency.

**Maven**

```xml
<repository>
    <id>eldonexus</id>
    <url>https://eldonexus.de/repository/maven-public/</url>
</repository>

<dependency>
    <groupId>de.paul2708</groupId>
    <artifactId>worm</artifactId>
    <version>0.2.0</version>
</dependency>
```

**Gradle**
```
repositories {
    maven("https://eldonexus.de/repository/maven-public/")
}

dependencies {
    implementation("de.paul2708:worm:0.2.0")
}
```

2. Define an entity.

```java
@Entity("persons")
public class Person {

  @Identifier
  @MaxLength(128)
  @Attribute("email_address")
  private String email;

  @Attribute("age")
  private int age;

  @Reference
  @Attribute("partner")
  private Person partner;

  @Attribte("passwords")
  private List<String> passwords;

  public Person() {

  }
}
```

3. Create a repository.

```java
public interface PersonRepository extends CrudRepository<Person, String> {

  List<Person> findByAge(int age);
}
```

4. Create a MySQL database and store the first entity.

```java
Database database = new MySQLDatabase("localhost", 3306, "database", "user", "password");
PersonRepository repository = Repository.create(PersonRepository.class, Person.class, database);

Person person = new Person();
// ...

repository.save(person);
```

## Contribution
If you want to contribute, just open an issue and check out the *work-in-progress* [contribution wiki](https://github.com/Paul2708/worm/wiki/Contribution).
