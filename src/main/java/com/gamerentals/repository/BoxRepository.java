package com.gamerentals.repository;

import com.gamerentals.entity.Box;
import com.gamerentals.util.HibernateUtil;
import jakarta.persistence.*;
import java.util.*;

public class BoxRepository extends GenericRepository<Box, Integer> {

    public BoxRepository() {
        super(Box.class);
    }

    public List<Box> findAllWithGames() {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            return em.createQuery(
                    "SELECT b FROM Box b JOIN FETCH b.game ORDER BY b.id", Box.class
            ).getResultList();
        }
    }

    public List<Box> findByGameId(Integer gameId) {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            return em.createQuery(
                    "FROM Box b WHERE b.game.id = :gameId ORDER BY b.id",
                    Box.class).setParameter("gameId", gameId).getResultList();
        }
    }

    public int saveAll(List<Box> boxes) {
        EntityManager em = HibernateUtil.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            for (int i = 0; i < boxes.size(); i++) {
                em.persist(boxes.get(i));
                if (i > 0 && i % 25 == 0) { em.flush(); em.clear(); }
            }
            tx.commit();
            return boxes.size();
        } catch (Exception ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    public boolean deleteWithDependencies(int id) {
        EntityManager em = HibernateUtil.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Box box = em.find(Box.class, id);
            if (box == null) {
                return false;
            }

            em.createQuery("DELETE FROM BoxRent br WHERE br.box.id = :boxId")
                    .setParameter("boxId", id).executeUpdate();

            em.remove(box);

            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
