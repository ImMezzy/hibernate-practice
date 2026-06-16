package com.gamerentals.repository;

import com.gamerentals.entity.*;
import com.gamerentals.util.HibernateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.LockModeType;

import java.time.LocalDateTime;
import java.util.*;

public class BoxRentRepository extends GenericRepository<BoxRent, Integer> {

    public BoxRentRepository() { super(BoxRent.class); }

    public List<BoxRent> findByClientId(Integer clientPassNumber) {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            return em.createQuery(
                    "FROM BoxRent br WHERE br.client.pass_number = :passNumber ORDER BY br.id",
                    BoxRent.class).setParameter("passNumber", clientPassNumber).getResultList();
        }
    }

    public List<BoxRent> findByGameId(Integer gameId) {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            return em.createQuery(
                    "FROM BoxRent br WHERE br.box.game.id = :gameId ORDER BY br.id ASC",
                    BoxRent.class).setParameter("gameId", gameId).getResultList();
        }
    }

    public List<BoxRent> findByBoxNumber(Integer boxNumber) {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            return em.createQuery(
                    "FROM BoxRent br WHERE br.box.boxNumber = :boxNumber ORDER BY br.id ASC",
                    BoxRent.class).setParameter("boxNumber", boxNumber).getResultList();
        }
    }

    public boolean existsByBoxNumberAndGameId(Integer boxNumber, Integer gameId) {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            long count = em.createQuery(
                "SELECT COUNT(br) FROM BoxRent br " +
                        "WHERE br.box.boxNumber = :boxNumber AND br.box.game.id = :gameId",
                    Long.class)
                    .setParameter("boxNumber", boxNumber)
                    .setParameter("gameId", gameId).getSingleResult();
            return count > 0;
        }
    }

    public int rentBox(String clientPassNumber, int boxId) {
        EntityManager em = HibernateUtil.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Client client = em.find(Client.class, clientPassNumber);
            Box box = em.find(Box.class, boxId);

            if (client == null || box == null) {
                throw new IllegalArgumentException("Client or box not found");
            }

            em.lock(box, LockModeType.PESSIMISTIC_WRITE);

            long occupied = em.createQuery(
                    "SELECT COUNT(br) FROM BoxRent br " +
                            "WHERE br.client.pass_number = :clientPassNumber AND br.box.id = :boxId",
                    Long.class)
                    .setParameter("clientPassNumber", clientPassNumber)
                    .setParameter("boxId", box.getId())
                    .getSingleResult();

            if (occupied > 0) {
                tx.rollback();
                throw new IllegalStateException(
                        String.format("Box already taken with id %d", boxId));
            }

            BoxRent br = new BoxRent(box, client, LocalDateTime.now(), LocalDateTime.now().plusDays(14));
        }
    }
}
