package de.paul2708.worm;

import de.paul2708.worm.columns.Column;
import de.paul2708.worm.columns.PrimaryKey;
import de.paul2708.worm.columns.Table;

import java.util.List;
import java.util.Set;

@Table("collectors")
public class Collector {

    @PrimaryKey
    @Column("name")
    private String name;

    @Column("badges")
    private Set<String> badges;

    @Column("primes")
    private List<Integer> primeNumbers;

    public Collector() {

    }

    public Collector(String name, Set<String> badges, List<Integer> primeNumbers) {
        this.name = name;
        this.badges = badges;
        this.primeNumbers = primeNumbers;
    }

    public void addBadge(String badge) {
        this.badges.add(badge);
    }

    public void removePrime(int prime) {
        this.primeNumbers.remove(prime);
    }

    public Set<String> getBadges() {
        return badges;
    }

    public List<Integer> getPrimeNumbers() {
        return primeNumbers;
    }
}
