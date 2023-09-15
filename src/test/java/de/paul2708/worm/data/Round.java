package de.paul2708.worm.data;

import de.paul2708.worm.attributes.*;
import de.paul2708.worm.attributes.generator.IntegerGenerator;

import java.time.LocalDateTime;

@Entity("rounds")
public class Round {

    @Identifier(generator = IntegerGenerator.class)
    @Attribute("id")
    private int id;

    @TimeZone("Europe/Berlin")
    @Attribute("start_time")
    private LocalDateTime startTime;

    @TimeZone("Europe/Berlin")
    @Attribute("end_time")
    private LocalDateTime endTime;

    @CreatedAt
    @TimeZone("Europe/Berlin")
    @Attribute("created_at")
    private LocalDateTime createdAt;

    @UpdatedAt
    @TimeZone("Europe/Berlin")
    @Attribute("updated_at")
    private LocalDateTime updatedAt;

    public Round() {

    }

    public Round(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
