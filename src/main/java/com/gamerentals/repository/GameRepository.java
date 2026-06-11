package com.gamerentals.repository;

import com.cinema.util.HibernateUtil;
import com.gamerentals.entity.*;
import jakarta.persistence.*;

import java.util.*;

public class GameRepository extends GenericRepository<Game, Integer> {

    public GameRepository() {
        super(Game.class);
    }

    public List<Game> findAllGamesThatHaveBoxes() {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            return em.createQuery(
                    "SELECT DISTINCT b.game FROM Box b ORDER BY b.game.id",
                    Game.class
            ).getResultList();
        }
    }

    public Optional<Game> findGameWithBoxesCheck(int id) {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<Game> result = em.createQuery(
                    "SELECT b.game FROM Box b WHERE b.game.id = :id",
                    Game.class).setParameter("id", id).getResultList();
            return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
        }
    }

    public void assignBoxToGame(int gameId, int boxId) {
        EntityManager em = HibernateUtil.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Game game = em.find(Game.class, gameId);
            Box box = em.find(Box.class, boxId);
            if (game != null && box != null) {
                box.setGame(game);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void removeBoxFromGame(int gameId, int boxId) {
        EntityManager em = HibernateUtil.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Box box = em.find(Box.class, boxId);
            if (box != null && box.getGame() != null && box.getGame().getId().equals(gameId)) { // проверка, что игра в коробке
                em.remove(box);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
