package com.gamerentals.repository;

import com.gamerentals.entity.Client;
import com.gamerentals.util.HibernateUtil;
import jakarta.persistence.*;

import java.util.*;

public class ClientRepository extends GenericRepository<Client, String>{
    public ClientRepository() { super(Client.class); }

    public Optional<Client> findByPhoneNumber(String phoneNumber) {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<Client> result = em.createQuery(
                    "SELECT c FROM Client c WHERE c.phoneNumber = :phone", Client.class
            ).setParameter("phone", phoneNumber).getResultList();
            return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
        }
    }

    public List<Client> findByLastName(String lastName) {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            return em.createQuery(
                    "SELECT c FROM Client c WHERE LOWER(c.lastName) LIKE LOWER(:pattern) ORDER BY c.lastName",
                    Client.class
            ).setParameter("pattern", "%" + lastName + "%").getResultList();
        }
    }

    public boolean deleteWithDependencies(String passNumber) {
        EntityManager em = HibernateUtil.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Client client = em.find(Client.class, passNumber);
            if (client == null) {
                return false;
            }

            em.createQuery("DELETE FROM GameSession gs WHERE gs.client.passNumber = :pass")
                    .setParameter("pass", passNumber).executeUpdate();
            em.createQuery("DELETE FROM GameAttraction ga WHERE ga.client.passNumber = :pass")
                    .setParameter("pass", passNumber).executeUpdate();
            em.createQuery("DELETE FROM BoxRent br WHERE br.client.passNumber = :pass")
                    .setParameter("pass", passNumber).executeUpdate();

            em.remove(client);

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