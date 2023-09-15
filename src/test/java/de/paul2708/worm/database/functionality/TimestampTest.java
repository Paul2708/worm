package de.paul2708.worm.database.functionality;

import de.paul2708.worm.data.Round;
import de.paul2708.worm.data.RoundRepository;
import de.paul2708.worm.database.Database;
import de.paul2708.worm.repository.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public abstract class TimestampTest extends DatabaseTestBase {

    private RoundRepository roundRepository;

    @BeforeEach
    void resetDatabase() {
        Database emptyDatabase = provider().createEmptyDatabase();
        emptyDatabase.connect();

        this.roundRepository = Repository.create(RoundRepository.class, Round.class, emptyDatabase);

        assumeTrue(roundRepository.findAll().isEmpty());
    }

    @Test
    void testDateTimeDataType() {
        LocalDateTime startTime = LocalDateTime.of(2023, 8, 27, 10, 0);
        LocalDateTime endTime = LocalDateTime.of(2023, 8, 27, 10, 45);

        int id = roundRepository.save(new Round(startTime, endTime)).getId();

        Optional<Round> optionalRound = roundRepository.findById(id);
        assertTrue(optionalRound.isPresent());

        Round round = optionalRound.get();
        assertEquals(startTime, round.getStartTime());
        assertEquals(endTime, round.getEndTime());
    }

    @Test
    void testCreatedAt() {
        LocalDateTime startOperationTime = LocalDateTime.now();

        Round round = roundRepository.save(new Round(LocalDateTime.now(), LocalDateTime.now().plusHours(1)));

        assertNotNull(round.getCreatedAt());

        assertInBetween(startOperationTime, LocalDateTime.now(), round.getCreatedAt());
    }

    @Test
    void testUnmodifiedCreatedAt() {
        Round round = roundRepository.save(new Round(LocalDateTime.now(), LocalDateTime.now().plusHours(1)));
        LocalDateTime createdAt = round.getCreatedAt();

        round.setEndTime(LocalDateTime.now().plusHours(2));
        Round updatedRound = roundRepository.save(round);

        assertNotNull(createdAt);
        assertEquals(createdAt, updatedRound.getCreatedAt());
    }

    @Test
    void testInitialUpdatedAt() {
        LocalDateTime startOperationTime = LocalDateTime.now();
        Round round = roundRepository.save(new Round(LocalDateTime.now(), LocalDateTime.now().plusHours(1)));

        assertInBetween(startOperationTime, LocalDateTime.now(), round.getUpdatedAt());
    }

    @Test
    void testUpdatedAt() {
        Round round = roundRepository.save(new Round(LocalDateTime.now(), LocalDateTime.now().plusHours(1)));
        LocalDateTime updatedAt = round.getUpdatedAt();

        // Ensure that time passed between the last updated
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        round.setEndTime(LocalDateTime.now().plusHours(2));
        Round updatedRound = roundRepository.save(round);

        assertNotEquals(updatedAt, updatedRound.getUpdatedAt());
        assertInBetween(updatedAt, LocalDateTime.now(), updatedRound.getUpdatedAt());
    }

    private void assertInBetween(LocalDateTime min, LocalDateTime max, LocalDateTime current) {
        if (min.isEqual(current) || max.isEqual(current) || (current.isAfter(min) && current.isBefore(max))) {
            return;
        }

        fail("Date %s should be between %s and %s".formatted(current, min, max));
    }
}
