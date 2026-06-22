package com.gamerentals.entity;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "clients")
public class Client {

    @Id
    @Column(name = "pass_number")
    private String passNumber;

    @Column(name = "phone_number", nullable = false, length = 11, unique = true)
    private String phoneNumber;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "last_name",nullable = false, length = 100)
    private String lastName;

    @Column(name = "patronymic", length = 100)
    private String patronymic;

    protected Client() {}

    public Client(String passNumber, String phoneNumber, String name, String last_name, String patronymic) {
        this.passNumber = passNumber;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.lastName = last_name;
        this.patronymic = patronymic;
    }

    public Client(String passNumber, String phoneNumber, String name, String lastName) {
        this(passNumber, phoneNumber, name, lastName, null);
    }

    public String getPassNumber() { return passNumber; }
    public void setPass_number(String passNumber) { this.passNumber = passNumber; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
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
        return Objects.equals(passNumber, c.passNumber);
    }

    @Override
    public int hashCode() { return Objects.hashCode(passNumber); }

    @Override
    public String toString() {
        return String.format("%s %s %s (паспорт: %s, тел.: %s)",
                lastName, name,
                patronymic != null ? patronymic : "",
                passNumber, phoneNumber);
    }
}
