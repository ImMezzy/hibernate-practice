package com.gamerentals.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "game_session")
public class GameSession {

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

    @Column(name = "game_result", nullable = false)
    private boolean gameResult;

    protected GameSession() {}

    public GameSession(Game game, Client client, LocalDateTime startTime, LocalDateTime endTime, boolean gameResult) {
        this.game = game;
        this.client = client;
        this.startTime = startTime;
        this.endTime = endTime;
        this.gameResult = gameResult;
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
    public boolean isGameResult() { return gameResult; }
    public void setGameResult(boolean gameResult) { this.gameResult = gameResult; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameSession gs)) return false;
        return Objects.equals(id, gs.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return String.format("GameSession{id=%d, game='%s', client='%s', start=%s, end=%s, result=%b}",
                id, game.getName(),client.getPassNumber(), startTime, endTime, gameResult);
    }
}