package com.gamerentals.service;

import com.gamerentals.util.HibernateUtil;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.util.*;

public class BusinessQueryService {

    public void rentalCountByGame() {
        printHeader("Количество аренд по играм");
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<Object[]> results = em.createQuery("""
                SELECT br.box.game.id,
                       br.box.game.name,
                       COUNT(br)
                FROM BoxRent br
                GROUP BY br.box.game.id, br.box.game.name
                ORDER BY COUNT(br) DESC
                """, Object[].class).getResultList();

            System.out.printf("     %-5s %-25s %-15s%n", "ID", "Игра", "Аренд");
            System.out.println("     " + "─".repeat(47));
            for (Object[] row : results) {
                System.out.printf("     %-5d %-25s %-15d%n", row[0], row[1], (long) row[2]);
            }
        }
        printDivider();
    }

    public void getActiveRentals() {
        printHeader("Активные аренды у клиентов");
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<Object[]> results = em.createQuery("""
                SELECT c.passNumber,
                       br.box.game.name,
                       br.box.boxNumber,
                       br.dateOfRent,
                       br.dateOfReturn
                FROM BoxRent br
                JOIN br.client c
                WHERE br.dateOfReturn > CURRENT_TIMESTAMP
                ORDER BY br.dateOfReturn ASC
                """, Object[].class).getResultList();

            System.out.printf("     %-12s %-20s %-10s %-12s %-12s%n",
                    "Паспорт", "Игра", "№ коробки", "Выдана", "Вернуть до");
            System.out.println("     " + "─".repeat(68));
            for (Object[] row : results) {
                System.out.printf("     %-12s %-20s %-10d %-12s %-12s%n",
                        row[0], row[1], row[2],
                        ((java.time.LocalDateTime) row[3]).toLocalDate(),
                        ((java.time.LocalDateTime) row[4]).toLocalDate());
            }
        }
        printDivider();
    }

    public void getTop3Winners() {
        printHeader("Топ-3 клиентов по победам");
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<Object[]> results = em.createQuery("""
                SELECT c.lastName, c.name, COUNT(gs)
                FROM GameSession gs
                JOIN gs.client c
                WHERE gs.gameResult = true
                GROUP BY gs.client.passNumber, c.lastName, c.name
                ORDER BY COUNT(gs) DESC
                """, Object[].class)
                    .setMaxResults(3)
                    .getResultList();

            System.out.printf("     %-20s %-15s %-10s%n", "Фамилия", "Имя", "Побед");
            System.out.println("     " + "─".repeat(47));
            for (Object[] row : results) {
                System.out.printf("     %-20s %-15s %-10d%n", row[0], row[1], (long) row[2]);
            }
        }
        printDivider();
    }

    public void getGamesWithBoxCount() {
        printHeader("Игры с количеством коробок");
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<Object[]> results = em.createQuery("""
                SELECT g.name, g.difficulty, g.avgGameTime, COUNT(b)
                FROM Box b
                JOIN b.game g
                GROUP BY g.name, g.difficulty, g.avgGameTime
                ORDER BY COUNT(b) DESC
                """, Object[].class).getResultList();

            System.out.printf("     %-20s %-12s %-10s %-10s%n", "Игра", "Сложность", "Время", "Коробок");
            System.out.println("     " + "─".repeat(54));
            for (Object[] row : results) {
                java.time.LocalTime time = (java.time.LocalTime) row[2];
                int minutes = time.getHour() * 60 + time.getMinute();
                System.out.printf("     %-20s %-12s %-10s %-10d%n",
                        row[0], row[1], minutes + " мин", (long) row[3]);
            }
        }
        printDivider();
    }

    public void getUnrentedBoxes() {
        printHeader("Коробки без аренд");
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<Object[]> results = em.createQuery("""
                SELECT b.id, b.game.name, b.boxNumber, b.status
                FROM Box b
                WHERE NOT EXISTS (SELECT br FROM BoxRent br WHERE br.box = b)
                ORDER BY b.id ASC
                """, Object[].class).getResultList();

            System.out.printf("     %-5s %-20s %-10s %-10s%n", "ID", "Игра", "№ коробки", "Состояние");
            System.out.println("     " + "─".repeat(47));
            if (results.isEmpty()) {
                System.out.println("     (все коробки хотя бы раз арендовались)");
            }
            for (Object[] row : results) {
                System.out.printf("     %-5d %-20s %-10d %-10d%n", row[0], row[1], row[2], row[3]);
            }
        }
        printDivider();
    }

    public void getAvgRentDurationByDifficulty() {
        printHeader("Среднее время аренды по сложности");
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<Object[]> results = em.createQuery("""
                SELECT g.difficulty,
                       AVG(TIMESTAMPDIFF(DAY, br.dateOfRent, br.dateOfReturn))
                FROM BoxRent br
                JOIN br.box b
                JOIN b.game g
                GROUP BY g.difficulty
                ORDER BY AVG(TIMESTAMPDIFF(DAY, br.dateOfRent, br.dateOfReturn)) DESC
                """, Object[].class).getResultList();

            System.out.printf("     %-12s %-20s%n", "Сложность", "Среднее (дней)");
            System.out.println("     " + "─".repeat(34));
            for (Object[] row : results) {
                System.out.printf("     %-12s %-20.1f%n", row[0], ((Number) row[1]).doubleValue());
            }
        }
        printDivider();
    }

    public void getAttractionsStatsByGame() {
        printHeader("Статистика игротек по играм");
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<Object[]> results = em.createQuery("""
                SELECT g.name,
                       COUNT(ga),
                       COALESCE(SUM(TIMESTAMPDIFF(HOUR, ga.startTime, ga.endTime)), 0)
                FROM Game g
                LEFT JOIN GameAttraction ga ON ga.game = g
                GROUP BY g.name
                ORDER BY COUNT(ga) DESC
                """, Object[].class).getResultList();

            System.out.printf("     %-20s %-12s %-15s%n", "Игра", "Игротек", "Общее время (ч)");
            System.out.println("     " + "─".repeat(49));
            for (Object[] row : results) {
                System.out.printf("     %-20s %-12d %-15d%n", row[0], (long) row[1], (long) row[2]);
            }
        }
        printDivider();
    }

    public void getProblematicClients() {
        printHeader("Проблемные клиенты");
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<Object[]> results = em.createQuery("""
                SELECT c.lastName,
                       c.name,
                       COUNT(br),
                       COUNT(CASE WHEN br.status = com.gamerentals.entity.RentStatus.OVERDUE THEN 1 END),
                       SUM(br.fine),
                       ROUND(COUNT(CASE WHEN br.status = com.gamerentals.entity.RentStatus.OVERDUE THEN 1 END) * 100.0 / COUNT(br), 1)
                FROM BoxRent br
                JOIN br.client c
                GROUP BY c.passNumber, c.lastName, c.name
                HAVING SUM(br.fine) > 0
                ORDER BY ROUND(COUNT(CASE WHEN br.status = com.gamerentals.entity.RentStatus.OVERDUE THEN 1 END) * 100.0 / COUNT(br), 1) DESC
                """, Object[].class).getResultList();

            System.out.printf("     %-15s %-12s %-10s %-10s %-10s %-10s%n",
                    "Фамилия", "Имя", "Всего", "Просроч.", "Штраф", "% просроков");
            System.out.println("     " + "─".repeat(69));
            for (Object[] row : results) {
                System.out.printf("     %-15s %-12s %-10d %-10d %-10d %-10.1f%n",
                        row[0], row[1], (long) row[2], (long) row[3],
                        (long) row[4], ((Number) row[5]).doubleValue());
            }
        }
        printDivider();
    }

    public void getOverdueRentals() {
        printHeader("Просроченные аренды");
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<Object[]> results = em.createQuery("""
                SELECT c.passNumber,
                       c.lastName,
                       c.name,
                       br.box.game.name,
                       br.box.boxNumber,
                       br.dateOfReturn,
                       TIMESTAMPDIFF(DAY, br.dateOfReturn, CURRENT_TIMESTAMP),
                       br.fine
                FROM BoxRent br
                JOIN br.client c
                WHERE br.status = com.gamerentals.entity.RentStatus.OVERDUE
                ORDER BY TIMESTAMPDIFF(DAY, br.dateOfReturn, CURRENT_TIMESTAMP) DESC
                """, Object[].class).getResultList();

            System.out.printf("     %-12s %-12s %-10s %-15s %-8s %-12s %-10s %-10s%n",
                    "Паспорт", "Фамилия", "Имя", "Игра", "№ кор.", "План. возврат", "Просроч.", "Штраф");
            System.out.println("     " + "─".repeat(91));
            if (results.isEmpty()) {
                System.out.println("     (нет просроченных аренд)");
            }
            for (Object[] row : results) {
                System.out.printf("     %-12s %-12s %-10s %-15s %-8d %-12s %-10d %-10d%n",
                        row[0], row[1], row[2], row[3], row[4],
                        ((java.time.LocalDateTime) row[5]).toLocalDate(),
                        (long) row[6], row[7]);
            }
        }
        printDivider();
    }

    public void getGamesFullStatistics() {
        printHeader("Полная статистика по играм");
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<Object[]> results = em.createQuery("""
                SELECT g.name,
                       (SELECT COUNT(br) FROM BoxRent br WHERE br.box.game = g),
                       (SELECT COUNT(ga) FROM GameAttraction ga WHERE ga.game = g),
                       (SELECT COUNT(gs) FROM GameSession gs WHERE gs.game = g)
                FROM Game g
                ORDER BY g.name
                """, Object[].class).getResultList();

            System.out.printf("     %-20s %-12s %-12s %-12s%n", "Игра", "Аренд", "Игротек", "Сессий");
            System.out.println("     " + "─".repeat(58));
            for (Object[] row : results) {
                System.out.printf("     %-20s %-12d %-12d %-12d%n",
                        row[0], (long) row[1], (long) row[2], (long) row[3]);
            }
        }
        printDivider();
    }

    public void runAll() {
        rentalCountByGame();
        getActiveRentals();
        getTop3Winners();
        getGamesWithBoxCount();
        getUnrentedBoxes();
        getAvgRentDurationByDifficulty();
        getAttractionsStatsByGame();
        getProblematicClients();
        getOverdueRentals();
        getGamesFullStatistics();
    }

    private void printHeader(String title) {
        System.out.println();
        System.out.println("╔" + "═".repeat(title.length() + 4) + "╗");
        System.out.println("║  " + title + "  ║");
        System.out.println("╚" + "═".repeat(title.length() + 4) + "╝");
    }

    private void printDivider() {
        System.out.println("─".repeat(80));
    }
}
