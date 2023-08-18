package de.paul2708.worm;

import de.paul2708.worm.columns.*;
import de.paul2708.worm.columns.generator.IntegerGenerator;

import java.time.LocalDateTime;

@Table("rounds")
public class Round {

    @PrimaryKey
    @AutoGenerated(IntegerGenerator.class)
    @Column("id")
    private int id;

    @Column("start_time")
    private LocalDateTime startTime;

    @Column("end_time")
    private LocalDateTime endTime;

    @CreatedAt
    @Column("created_at")
    private LocalDateTime createdAt;

    @UpdatedAt
    @Column("updated_at")
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
