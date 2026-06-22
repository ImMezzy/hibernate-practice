package com.gamerentals.repository;

import com.gamerentals.entity.*;
import com.gamerentals.util.HibernateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.LockModeType;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class GameAttractionRepository extends GenericRepository<GameAttraction, Integer> {

    public GameAttractionRepository() {
        super(GameAttraction.class);
    }

    public List<GameAttraction> findByClientId(String clientPassNumber) {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            return em.createQuery(
                            "FROM GameAttraction ga WHERE ga.client.passNumber = :passNumber ORDER BY ga.startTime DESC",
                            GameAttraction.class)
                    .setParameter("passNumber", clientPassNumber)
                    .getResultList();
        }
    }

    public List<GameAttraction> findByGameId(Integer gameId) {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            return em.createQuery(
                            "FROM GameAttraction ga WHERE ga.game.id = :gameId ORDER BY ga.startTime DESC",
                            GameAttraction.class)
                    .setParameter("gameId", gameId)
                    .getResultList();
        }
    }

    public List<GameAttraction> findActiveByClientIdWithGame(String passNumber) {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            return em.createQuery(
                            "SELECT ga FROM GameAttraction ga " +
                                    "JOIN FETCH ga.game " +
                                    "WHERE ga.client.passNumber = :passNumber " +
                                    "AND ga.endTime > CURRENT_TIMESTAMP " +
                                    "ORDER BY ga.endTime", GameAttraction.class
                    ).setParameter("passNumber", passNumber)
                    .getResultList();
        }
    }

    public boolean existsActiveByClientAndGame(String clientPassNumber, Integer gameId) {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            long count = em.createQuery(
                            "SELECT COUNT(ga) FROM GameAttraction ga " +
                                    "WHERE ga.client.passNumber = :passNumber " +
                                    "AND ga.game.id = :gameId " +
                                    "AND ga.endTime > CURRENT_TIMESTAMP",
                            Long.class)
                    .setParameter("passNumber", clientPassNumber)
                    .setParameter("gameId", gameId)
                    .getSingleResult();
            return count > 0;
        }
    }

    public int startAttraction(String clientPassNumber, int gameId) {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();

                Client client = em.find(Client.class, clientPassNumber);
                Game game = em.find(Game.class, gameId);

                if (client == null || game == null) {
                    throw new IllegalArgumentException("Клиент или игра не найдены");
                }

                em.lock(game, LockModeType.PESSIMISTIC_WRITE);

                long activeAttractions = em.createQuery(
                                "SELECT COUNT(ga) FROM GameAttraction ga " +
                                        "WHERE ga.client.passNumber = :passNumber " +
                                        "AND ga.game.id = :gameId " +
                                        "AND ga.endTime > CURRENT_TIMESTAMP",
                                Long.class)
                        .setParameter("passNumber", clientPassNumber)
                        .setParameter("gameId", gameId)
                        .getSingleResult();

                if (activeAttractions > 0) {
                    tx.rollback();
                    throw new IllegalStateException(
                            String.format("Клиент %s уже играет в '%s'", clientPassNumber, game.getName()));
                }

                LocalDateTime now = LocalDateTime.now();
                LocalDateTime plannedEnd = now.plusHours(4);

                GameAttraction ga = new GameAttraction(game, client, now, plannedEnd);
                em.persist(ga);
                tx.commit();

                return ga.getId();

            } catch (Exception ex) {
                if (tx.isActive()) tx.rollback();
                throw ex;
            }
        }
    }

    public void endAttraction(String clientPassNumber, int attractionId) {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();

                GameAttraction attraction = em.find(GameAttraction.class, attractionId);

                if (attraction == null) {
                    throw new IllegalArgumentException("Аттракция не найдена");
                }

                if (!attraction.getClient().getPassNumber().equals(clientPassNumber)) {
                    throw new IllegalStateException("Аттракция принадлежит другому клиенту");
                }

                em.lock(attraction, LockModeType.PESSIMISTIC_WRITE);

                if (attraction.getEndTime().isBefore(LocalDateTime.now())) {
                    tx.rollback();
                    throw new IllegalStateException("Аттракция уже завершена");
                }

                attraction.setEndTime(LocalDateTime.now());

                tx.commit();

            } catch (Exception ex) {
                if (tx.isActive()) tx.rollback();
                throw ex;
            }
        }
    }
}