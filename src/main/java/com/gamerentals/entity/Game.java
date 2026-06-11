package com.gamerentals.entity;

import jakarta.persistence.*;
import java.time.LocalTime;
import java.util.*;

@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "authors", nullable = false, length = 100)
    private String authors;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false, length = 20)
    private GameDifficulty difficulty;

    @Column(name = "avg_game_time", nullable = false)
    private LocalTime avgGameTime;

    protected Game() {}

    public Game(String name, String authors, GameDifficulty difficulty, LocalTime avg_game_time) {
        this.name = name;
        this.authors = authors;
        this.difficulty = difficulty;
        this.avgGameTime = avg_game_time;
    }

    public GameDifficulty getDifficulty() { return difficulty; }
    public void setDifficulty(GameDifficulty difficulty) { this.difficulty = difficulty; }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAuthors() { return authors; }
    public void setAuthors(String authors) { this.authors = authors; }
    public LocalTime getAvgGameTime() { return avgGameTime; }
    public void setAvgGameTime(LocalTime avgGameTime) { this.avgGameTime = avgGameTime; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Game g)) return false;
        return Objects.equals(name, g.name);
    }

    @Override
    public int hashCode() { return Objects.hashCode(name); }

    @Override
    public String toString() { return String.format("Game{id=%d, '%s', %s, %s, time=%s}", id, name, authors, difficulty, avgGameTime); }
}
