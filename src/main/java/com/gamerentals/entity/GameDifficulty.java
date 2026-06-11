package com.gamerentals.entity;

public enum GameDifficulty {
    EASY("Легко"),
    MEDIUM("Средне"),
    HARD("Сложно"),
    EXPERT("Эксперт");

    private final String displayName;

    GameDifficulty(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
