package de.paul2708.worm;

import de.paul2708.worm.columns.Column;
import de.paul2708.worm.columns.MaxLength;
import de.paul2708.worm.columns.Identifier;
import de.paul2708.worm.columns.Table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Table("collectors")
public class Collector {

    @Identifier
    @MaxLength(255)
    @Column("name")
    private String name;

    @Column("age")
    private int age;

    @Column("badges")
    private Set<String> badges;

    @Column("primes")
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
