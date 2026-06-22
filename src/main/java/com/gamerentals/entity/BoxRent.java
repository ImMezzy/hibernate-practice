package com.gamerentals.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "box_rent")
public class BoxRent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "box_id", nullable = false)
    private Box box;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_pass_number", nullable = false)
    private Client client;

    @Column(name = "date_of_rent", nullable = false)
    private LocalDateTime dateOfRent;

    @Column(name = "date_of_return", nullable = false)
    private LocalDateTime dateOfReturn;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RentStatus status = RentStatus.ACTIVE;

    @Column(nullable = false)
    private Integer fine = 0;

    protected BoxRent() {}

    public BoxRent(Box box, Client client, LocalDateTime dateOfRent, LocalDateTime dateOfReturn) {
        this.box = box;
        this.client = client;
        this.dateOfRent = dateOfRent;
        this.dateOfReturn = dateOfReturn;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Box getBox() { return box; }
    public void setBox(Box box) { this.box = box; }
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    public LocalDateTime getDateOfRent() { return dateOfRent; }
    public void setDateOfRent(LocalDateTime dateOfRent) { this.dateOfRent = dateOfRent; }
    public LocalDateTime getDateOfReturn() { return dateOfReturn; }
    public void setDateOfReturn(LocalDateTime dateOfReturn) { this.dateOfReturn = dateOfReturn; }
    public RentStatus getStatus() { return status; }
    public void setStatus(RentStatus status) { this.status = status; }
    public Integer getFine() { return fine; }
    public void setFine(Integer fine) { this.fine = fine; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoxRent br)) return false;
        return Objects.equals(id, br.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() { return String.format("Rent{id=%d, box=%d, client=%s, date_rent:%s, date_return:%s, fine=%d}",
            id, box.getBoxNumber(), client.getPassNumber(), dateOfRent, dateOfReturn, fine); }
}
