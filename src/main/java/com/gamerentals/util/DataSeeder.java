package com.gamerentals.util;

import com.gamerentals.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public final class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private DataSeeder() {}

    public static void seed() {
        EntityManager em = HibernateUtil.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Long clientsCount = em.createQuery("SELECT COUNT(c) FROM Client c", Long.class)
                    .getSingleResult();

            System.out.println("=== DataSeeder: найдено клиентов в БД: " + clientsCount + " ===");

            if (clientsCount > 0) {
                tx.commit();
                System.out.println("=== DataSeeder: данные уже есть, заполнение пропущено ===");
                return;
            }

            System.out.println("=== DataSeeder: начинаем вставку данных ===");

            Client client1 = new Client("8120 909827", "89001112233", "Иван", "Иванов", "Иванович");
            Client client2 = new Client("7451 234567", "89012223344", "Анна", "Смирнова", "Сергеевна");
            Client client3 = new Client("3892 456789", "89023334455", "Олег", "Петров", null);
            Client client4 = new Client("5634 789012", "89034445566", "Мария", "Кузнецова", "Александровна");
            Client client5 = new Client("9271 345678", "89045556677", "Дмитрий", "Соколов", "Владимирович");
            Client client6 = new Client("1048 567890", "89056667788", "Елена", "Волкова", null);
            Client client7 = new Client("6723 890123", "89067778899", "Алексей", "Морозов", "Николаевич");
            Client client8 = new Client("4589 123456", "89078889900", "Ольга", "Новикова", "Дмитриевна");
            Client client9 = new Client("2367 678901", "89089990011", "Сергей", "Федоров", null);
            Client client10 = new Client("8901 234567", "89091001122", "Татьяна", "Михайлова", "Петровна");
            Client client11 = new Client("3456 789012", "89102112233", "Андрей", "Соколов", "Игоревич");
            Client client12 = new Client("7890 123456", "89113223344", "Наталья", "Павлова", null);
            Client client13 = new Client("1234 567890", "89124334455", "Виктор", "Семёнов", "Андреевич");
            Client client14 = new Client("5678 901234", "89135445566", "Юлия", "Голубева", "Сергеевна");
            Client client15 = new Client("9012 345678", "89146556677", "Максим", "Воробьёв", null);

            List.of(client1, client2, client3, client4, client5, client6, client7, client8,
                    client9, client10, client11, client12, client13, client14, client15).forEach(em::persist);

            Game game1 = new Game("Колонизаторы", "Клаус Тойбер", GameDifficulty.MEDIUM, LocalTime.parse("02:00"));
            Game game2 = new Game("Каркассон", "Клаус-Юрген Реде", GameDifficulty.EASY, LocalTime.parse("00:45"));
            Game game3 = new Game("7 Чудес", "Антуан Боза", GameDifficulty.HARD, LocalTime.parse("03:00"));
            Game game4 = new Game("Имаджинариум", "Сергей Носов", GameDifficulty.EASY, LocalTime.parse("01:00"));
            Game game5 = new Game("Диксит", "Жан-Луи Рубира", GameDifficulty.EASY, LocalTime.parse("00:30"));
            Game game6 = new Game("Манчкин", "Стив Джексон", GameDifficulty.MEDIUM, LocalTime.parse("01:30"));
            Game game7 = new Game("Шахматы", "Традиционная", GameDifficulty.EXPERT, LocalTime.parse("02:00"));
            Game game8 = new Game("Монополия", "Чарльз Дэрроу", GameDifficulty.MEDIUM, LocalTime.parse("02:30"));
            Game game9 = new Game("Кодовые имена", "Владо Хватал", GameDifficulty.EASY, LocalTime.parse("00:45"));
            Game game10 = new Game("Пандемия", "Мэтт Ликок", GameDifficulty.HARD, LocalTime.parse("02:00"));
            Game game11 = new Game("Доминион", "Дональд Ваккарино", GameDifficulty.HARD, LocalTime.parse("01:30"));
            Game game12 = new Game("Ticket to Ride", "Алан Р. Мун", GameDifficulty.MEDIUM, LocalTime.parse("01:30"));

            List.of(game1, game2, game3, game4, game5, game6, game7, game8, game9, game10, game11, game12)
                    .forEach(em::persist);
            em.flush();

            Box box1 = new Box(game1, 1, 95);
            Box box2 = new Box(game1, 2, 80);
            Box box3 = new Box(game1, 3, 100);
            Box box4 = new Box(game2, 1, 90);
            Box box5 = new Box(game2, 2, 75);
            Box box6 = new Box(game3, 1, 100);
            Box box7 = new Box(game4, 1, 85);
            Box box8 = new Box(game4, 2, 60);
            Box box9 = new Box(game5, 1, 100);
            Box box10 = new Box(game6, 1, 70);
            Box box11 = new Box(game7, 1, 100);
            Box box12 = new Box(game7, 2, 95);
            Box box13 = new Box(game8, 1, 88);
            Box box14 = new Box(game9, 1, 100);
            Box box15 = new Box(game10, 1, 25);

            List.of(box1, box2, box3, box4, box5, box6, box7, box8, box9, box10, box11, box12, box13, box14, box15)
                    .forEach(em::persist);
            em.flush();

            LocalDateTime now = LocalDateTime.now();

            BoxRent rent1 = new BoxRent(box1, client1, now.minusDays(5), now.plusDays(9));
            rent1.setFine(0);
            rent1.setStatus(RentStatus.ACTIVE);

            BoxRent rent2 = new BoxRent(box6, client2, now.minusDays(1), now.plusDays(13));
            rent2.setFine(0);
            rent2.setStatus(RentStatus.ACTIVE);

            BoxRent rent3 = new BoxRent(box11, client3, now.minusHours(6), now.plusDays(14));
            rent3.setFine(0);
            rent3.setStatus(RentStatus.ACTIVE);

            BoxRent rent4 = new BoxRent(box2, client6, now.minusDays(50), now.minusDays(36));
            rent4.setFine(0);
            rent4.setStatus(RentStatus.RETURNED);

            BoxRent rent5 = new BoxRent(box2, client7, now.minusDays(30), now.minusDays(16));
            rent5.setFine(0);
            rent5.setStatus(RentStatus.RETURNED);

            BoxRent rent6 = new BoxRent(box3, client9, now.minusDays(40), now.minusDays(26));
            rent6.setFine(0);
            rent6.setStatus(RentStatus.RETURNED);

            BoxRent rent7 = new BoxRent(box12, client15, now.minusDays(35), now.minusDays(21));
            rent7.setFine(0);
            rent7.setStatus(RentStatus.RETURNED);

            BoxRent rent8 = new BoxRent(box13, client15, now.minusDays(25), now.minusDays(11));
            rent8.setFine(0);
            rent8.setStatus(RentStatus.RETURNED);

            BoxRent rent9 = new BoxRent(box14, client11, now.minusDays(20), now.minusDays(6));
            rent9.setFine(0);
            rent9.setStatus(RentStatus.RETURNED);

            BoxRent rent10 = new BoxRent(box7, client14, now.minusDays(15), now.minusDays(1));
            rent10.setFine(0);
            rent10.setStatus(RentStatus.RETURNED);

            BoxRent rent11 = new BoxRent(box5, client11, now.minusDays(60), now.minusDays(40));
            rent11.setFine(1500);
            rent11.setStatus(RentStatus.OVERDUE);

            BoxRent rent12 = new BoxRent(box8, client13, now.minusDays(50), now.minusDays(30));
            rent12.setFine(500);
            rent12.setStatus(RentStatus.OVERDUE);

            BoxRent rent13 = new BoxRent(box9, client13, now.minusDays(25), now.minusDays(5));
            rent13.setFine(1000);
            rent13.setStatus(RentStatus.OVERDUE);

            BoxRent rent14 = new BoxRent(box10, client14, now.minusDays(45), now.minusDays(31));
            rent14.setFine(1500);
            rent14.setStatus(RentStatus.OVERDUE);

            BoxRent rent15 = new BoxRent(box4, client12, now.minusDays(30), now.minusDays(10));
            rent15.setFine(2000);
            rent15.setStatus(RentStatus.OVERDUE);

            List.of(rent1, rent2, rent3, rent4, rent5, rent6, rent7, rent8, rent9, rent10,
                    rent11, rent12, rent13, rent14, rent15).forEach(em::persist);

            GameAttraction attraction1 = new GameAttraction(game2, client1, now.minusHours(1), now.plusHours(3));
            GameAttraction attraction2 = new GameAttraction(game4, client1, now.minusMinutes(30), now.plusHours(3).plusMinutes(30));
            GameAttraction attraction3 = new GameAttraction(game5, client2, now.minusMinutes(15), now.plusHours(3).plusMinutes(45));
            GameAttraction attraction4 = new GameAttraction(game9, client6, now.minusHours(2), now.plusHours(2));

            GameAttraction attraction5 = new GameAttraction(game1, client3, now.minusDays(10).minusHours(4), now.minusDays(10));
            GameAttraction attraction6 = new GameAttraction(game3, client3, now.minusDays(7).minusHours(3), now.minusDays(7).plusHours(1));
            GameAttraction attraction7 = new GameAttraction(game6, client3, now.minusDays(3).minusHours(2), now.minusDays(3).plusHours(2));
            GameAttraction attraction8 = new GameAttraction(game2, client5, now.minusDays(15).minusHours(3), now.minusDays(15).plusHours(1));
            GameAttraction attraction9 = new GameAttraction(game7, client5, now.minusDays(5).minusHours(5), now.minusDays(5).minusHours(1));
            GameAttraction attraction10 = new GameAttraction(game4, client7, now.minusDays(20).minusHours(4), now.minusDays(20));
            GameAttraction attraction11 = new GameAttraction(game4, client7, now.minusDays(8).minusHours(2), now.minusDays(8).plusHours(2));
            GameAttraction attraction12 = new GameAttraction(game5, client8, now.minusDays(12).minusHours(3), now.minusDays(12).plusHours(1));
            GameAttraction attraction13 = new GameAttraction(game8, client8, now.minusDays(4).minusHours(3), now.minusDays(4).plusHours(1));
            GameAttraction attraction14 = new GameAttraction(game1, client9, now.minusDays(6).minusHours(4), now.minusDays(6));
            GameAttraction attraction15 = new GameAttraction(game9, client10, now.minusDays(9).minusHours(4), now.minusDays(9));

            List.of(attraction1, attraction2, attraction3, attraction4, attraction5, attraction6, attraction7,
                            attraction8, attraction9, attraction10, attraction11, attraction12, attraction13, attraction14, attraction15)
                    .forEach(em::persist);

            GameSession session1 = new GameSession(game1, client1, now.minusHours(1), now.plusHours(1), false);
            GameSession session2 = new GameSession(game2, client1, now.minusMinutes(45), now.plusHours(1).plusMinutes(15), false);
            GameSession session3 = new GameSession(game4, client2, now.minusMinutes(30), now.plusHours(1).plusMinutes(30), false);
            GameSession session4 = new GameSession(game5, client6, now.minusMinutes(20), now.plusMinutes(40), false);

            GameSession session5 = new GameSession(game3, client3, now.minusDays(10).minusHours(3), now.minusDays(10), true);
            GameSession session6 = new GameSession(game6, client3, now.minusDays(7).minusHours(2), now.minusDays(7).plusHours(1), true);
            GameSession session7 = new GameSession(game7, client3, now.minusDays(4).minusHours(4), now.minusDays(4).minusHours(1), true);
            GameSession session8 = new GameSession(game8, client5, now.minusDays(5).minusHours(3), now.minusDays(5), true);

            GameSession session9 = new GameSession(game2, client7, now.minusDays(12).minusHours(2), now.minusDays(12), false);
            GameSession session10 = new GameSession(game4, client7, now.minusDays(8).minusHours(1), now.minusDays(8).plusMinutes(30), false);
            GameSession session11 = new GameSession(game5, client7, now.minusDays(3).minusHours(2), now.minusDays(3).plusHours(1), false);
            GameSession session12 = new GameSession(game9, client8, now.minusDays(6).minusHours(3), now.minusDays(6), false);
            GameSession session13 = new GameSession(game1, client8, now.minusDays(2).minusHours(4), now.minusDays(2).minusHours(1), false);
            GameSession session14 = new GameSession(game3, client9, now.minusDays(1).minusHours(5), now.minusDays(1).minusHours(2), false);
            GameSession session15 = new GameSession(game6, client10, now.minusDays(9).minusHours(3), now.minusDays(9), true);

            List.of(session1, session2, session3, session4, session5, session6, session7, session8,
                    session9, session10, session11, session12, session13, session14, session15).forEach(em::persist);

            tx.commit();
            log.info("Начальные данные для GameRentals demo добавлены: {} клиентов, {} игр, {} коробок, {} аренд, {} аттракций, {} сессий",
                    15, 12, 15, 15, 15, 15);

        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            log.error("Ошибка при заполнении начальных данных", e);
            throw e;
        } finally {
            em.close();
        }
    }

    // Очистка всех таблиц с сбросом ID
    public static void clearAll() {
        EntityManager em = HibernateUtil.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            em.createNativeQuery("TRUNCATE TABLE game_session RESTART IDENTITY CASCADE").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE game_attraction RESTART IDENTITY CASCADE").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE box_rent RESTART IDENTITY CASCADE").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE boxes RESTART IDENTITY CASCADE").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE games RESTART IDENTITY CASCADE").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE clients RESTART IDENTITY CASCADE").executeUpdate();

            tx.commit();
            log.info("Все таблицы очищены");

        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            log.error("Ошибка при очистке таблиц", e);
            throw e;
        } finally {
            em.close();
        }
    }

}