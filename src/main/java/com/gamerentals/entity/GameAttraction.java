package com.gamerentals.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "game_attraction")
public class GameAttraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_pass_number", nullable = false)
    private Client client;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    protected GameAttraction() {}

    public GameAttraction(Game game, Client client, LocalDateTime startTime, LocalDateTime endTime) {
        this.game = game;
        this.client = client;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Game getGame() { return game; }
    public void setGame(Game game) { this.game = game; }
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameAttraction ga)) return false;
        return Objects.equals(id, ga.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return String.format("GameAttraction{id=%d, game='%s', client='%s', start=%s, end=%s}",
                id, game.getName(), client.getPassNumber(), startTime, endTime);
    }
}