package com.gamerentals.entity;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "boxes")
public class Box {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(name = "box_number", nullable = false)
    private Integer boxNumber;

    @Column(name = "status", nullable = false)
    private Integer status = 100;

    protected Box() {}

    public Box(Game game_id, Integer box_number, Integer status) {
        this.game = game_id;
        this.boxNumber = box_number;
        this.status = status;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Game getGame() { return game; }
    public void setGame(Game game) { this.game = game; }
    public Integer getBoxNumber() { return boxNumber; }
    public void setBox_number(Integer boxNumber) { this.boxNumber = boxNumber; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Box b)) return false;
        return Objects.equals(boxNumber, b.boxNumber);
    }

    @Override
    public int hashCode() { return Objects.hashCode(boxNumber); }

    @Override
    public String toString() { return String.format("Box{id=%d, game=%s, box_number=%d, status=%d}", id, game.getName(), boxNumber, status); }
}
