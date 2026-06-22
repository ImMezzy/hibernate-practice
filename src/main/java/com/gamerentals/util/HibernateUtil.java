package com.gamerentals.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateUtil {

    private static final Logger log = LoggerFactory.getLogger(HibernateUtil.class);
    private static volatile EntityManagerFactory emf;

    private HibernateUtil() {}

    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            synchronized (HibernateUtil.class) {
                if (emf == null) {
                    emf = Persistence.createEntityManagerFactory("gamerentals-pu");
                    log.info("EntityManagerFactory инициализирован (persistence-unit: gamerentals-pu)");
                }
            }
        }
        return emf;
    }

    public static EntityManager createEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            log.info("EntityManagerFactory закрыт");
        }
    }
}
