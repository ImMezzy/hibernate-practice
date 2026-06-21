package com.gamerentals.repository;

import com.gamerentals.entity.*;
import com.gamerentals.util.HibernateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.LockModeType;

import java.time.LocalDateTime;
import java.util.List;

public class GameSessionRepository extends GenericRepository<GameSession, Integer> {

    public GameSessionRepository() {
        super(GameSession.class);
    }

    public List<GameSession> findByClientId(String clientPassNumber) {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            return em.createQuery(
                            "FROM GameSession gs WHERE gs.client.passNumber = :passNumber ORDER BY gs.startTime DESC",
                            GameSession.class)
                    .setParameter("passNumber", clientPassNumber)
                    .getResultList();
        }
    }

    public List<GameSession> findByGameId(Integer gameId) {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            return em.createQuery(
                            "FROM GameSession gs WHERE gs.game.id = :gameId ORDER BY gs.startTime DESC",
                            GameSession.class)
                    .setParameter("gameId", gameId)
                    .getResultList();
        }
    }

    public List<GameSession> findActiveByClientId(String clientPassNumber) {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            return em.createQuery(
                            "FROM GameSession gs " +
                                    "WHERE gs.client.passNumber = :passNumber " +
                                    "AND gs.endTime > CURRENT_TIMESTAMP",
                            GameSession.class)
                    .setParameter("passNumber", clientPassNumber)
                    .getResultList();
        }
    }

    public boolean existsActiveByClientAndGame(String clientPassNumber, Integer gameId) {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            long count = em.createQuery(
                            "SELECT COUNT(gs) FROM GameSession gs " +
                                    "WHERE gs.client.passNumber = :passNumber " +
                                    "AND gs.game.id = :gameId " +
                                    "AND gs.endTime > CURRENT_TIMESTAMP",
                            Long.class)
                    .setParameter("passNumber", clientPassNumber)
                    .setParameter("gameId", gameId)
                    .getSingleResult();
            return count > 0;
        }
    }

    public List<GameSession> findWinsByClientId(String clientPassNumber) {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            return em.createQuery(
                            "FROM GameSession gs " +
                                    "WHERE gs.client.passNumber = :passNumber " +
                                    "AND gs.gameResult = true " +
                                    "ORDER BY gs.startTime DESC",
                            GameSession.class)
                    .setParameter("passNumber", clientPassNumber)
                    .getResultList();
        }
    }

    public long countWinsByClientId(String clientPassNumber) {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            return em.createQuery(
                            "SELECT COUNT(gs) FROM GameSession gs " +
                                    "WHERE gs.client.passNumber = :passNumber " +
                                    "AND gs.gameResult = true",
                            Long.class)
                    .setParameter("passNumber", clientPassNumber)
                    .getSingleResult();
        }
    }

    public int startSession(String clientPassNumber, int gameId) {
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

                long activeSessions = em.createQuery(
                                "SELECT COUNT(gs) FROM GameSession gs " +
                                        "WHERE gs.client.passNumber = :passNumber " +
                                        "AND gs.game.id = :gameId " +
                                        "AND gs.endTime > CURRENT_TIMESTAMP",
                                Long.class)
                        .setParameter("passNumber", clientPassNumber)
                        .setParameter("gameId", gameId)
                        .getSingleResult();

                if (activeSessions > 0) {
                    tx.rollback();
                    throw new IllegalStateException(
                            String.format("Клиент %s уже играет в '%s'", clientPassNumber, game.getName()));
                }

                LocalDateTime now = LocalDateTime.now();
                LocalDateTime plannedEnd = now.plusHours(2);

                GameSession gs = new GameSession(game, client, now, plannedEnd, false);
                em.persist(gs);
                tx.commit();

                return gs.getId();

            } catch (Exception ex) {
                if (tx.isActive()) tx.rollback();
                throw ex;
            }
        }
    }

    public void endSession(String clientPassNumber, int sessionId, boolean gameResult) {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();

                GameSession session = em.find(GameSession.class, sessionId);

                if (session == null) {
                    throw new IllegalArgumentException("Сессия не найдена");
                }

                if (!session.getClient().getPassNumber().equals(clientPassNumber)) {
                    throw new IllegalStateException("Сессия принадлежит другому клиенту");
                }

                em.lock(session, LockModeType.PESSIMISTIC_WRITE);

                if (session.getEndTime().isBefore(LocalDateTime.now())) {
                    tx.rollback();
                    throw new IllegalStateException("Сессия уже завершена");
                }

                session.setEndTime(LocalDateTime.now());
                session.setGameResult(gameResult);

                tx.commit();

            } catch (Exception ex) {
                if (tx.isActive()) tx.rollback();
                throw ex;
            }
        }
    }
}