package de.paul2708.worm.data;

import de.paul2708.worm.attributes.Attribute;
import de.paul2708.worm.attributes.MaxLength;
import de.paul2708.worm.attributes.Identifier;
import de.paul2708.worm.attributes.Entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity("collectors")
public class Collector {

    @Identifier
    @MaxLength(255)
    @Attribute("name")
    private String name;

    @Attribute("age")
    private int age;

    @Attribute("badges")
    private Set<String> badges;

    @Attribute("primes")
    private List<Integer> primeNumbers;

    public Collector() {

    }

    public Collector(String name, Set<String> badges, List<Integer> primeNumbers) {
        this.name = name;
        this.age = 42;
        this.badges = new HashSet<>(badges);
        this.primeNumbers = new ArrayList<>(primeNumbers);
    }

    public void addBadge(String badge) {
        this.badges.add(badge);
    }

    public void removePrime(int prime) {
        this.primeNumbers.removeIf(value -> value == prime);
    }

    public Set<String> getBadges() {
        return badges;
    }

    public List<Integer> getPrimeNumbers() {
        return primeNumbers;
    }
}
