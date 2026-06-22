package com.gamerentals.entity;

public enum RentStatus {
    ACTIVE("Активна"),
    OVERDUE("Просрочена"),
    RETURNED("Завершена");

    private final String displayName;

    RentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}