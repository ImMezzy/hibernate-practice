package com.gamerentals.entity;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "clients")
public class Client {
    @Id
    private String pass_number;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "last_name",nullable = false, length = 100)
    private String lastName;

    @Column(name = "patronymic", nullable = false, length = 100)
    private String patronymic;

    protected Client() {}

    public Client(String pass_number, String name, String last_name, String patronymic) {
        this.pass_number = pass_number;
        this.name = name;
        this.lastName = last_name;
        this.patronymic = patronymic;
    }

    public String getPassNumber() { return pass_number; }
    public void setPass_number(String pass_number) { this.pass_number = pass_number; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLastName() { return lastName; }
    public void setLastName(String last_name) { this.lastName = last_name; }
    public String getPatronymic() { return patronymic; }
    public void setPatronymic(String patronymic) { this.patronymic = patronymic; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client c)) return false;
        return Objects.equals(pass_number, c.pass_number);
    }

    @Override
    public int hashCode() { return Objects.hashCode(pass_number); }

    @Override
    public String toString() { return String.format("Client{pass_number=%s, %s, %s, %s}", pass_number, name, lastName, patronymic); }
}
