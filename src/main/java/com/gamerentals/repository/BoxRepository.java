package com.gamerentals.repository;

import com.cinema.util.HibernateUtil;
import com.gamerentals.entity.Box;
import jakarta.persistence.*;
import java.util.*;

public class BoxRepository extends GenericRepository<Box, Integer> {

    public BoxRepository() {
        super(Box.class);
    }

    public List<Box> findByGameId(Integer gameId) {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            return em.createQuery(
                    "FROM Box b WHERE b.game.id = :gameId ORDER BY b.id",
                    Box.class).setParameter("gameId", gameId).getResultList();
        }
    }


}
