package com.gamerentals.repository;

import com.cinema.repository.GenericRepository;
import com.cinema.util.HibernateUtil;
import com.gamerentals.entity.*;
import jakarta.persistence.*;

import java.util.*;

public class GameRepository extends GenericRepository<Game, Integer> {

    public GameRepository() { super(Game.class); }

    public List<Game> findAllGamesWithBoxes() {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            return em.createQuery(
                    "SELECT DISTINCT "
            )
        }
    }

}
