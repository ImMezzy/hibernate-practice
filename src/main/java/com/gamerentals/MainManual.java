package com.gamerentals;

import com.gamerentals.entity.*;
import com.gamerentals.repository.*;
import com.gamerentals.service.BusinessQueryService;
import com.gamerentals.service.CrudDemoService;
import com.gamerentals.util.DataSeeder;
import com.gamerentals.util.HibernateUtil;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class MainManual {

    private static Scanner scanner = new Scanner(System.in);
    private static ClientRepository clientRepo = new ClientRepository();
    private static GameRepository gameRepo = new GameRepository();
    private static BoxRepository boxRepo = new BoxRepository();
    private static BoxRentRepository boxRentRepo = new BoxRentRepository();
    private static GameAttractionRepository gameAttractionRepo = new GameAttractionRepository();
    private static GameSessionRepository gameSessionRepo = new GameSessionRepository();

    public static void main(String[] args) {
        try {
            HibernateUtil.getEntityManagerFactory();
            System.out.println("=== Система управления прокатом настольных игр ===\n");

            mainMenu();

        } catch (Exception e) {
            System.err.println("Критическая ошибка: " + e.getMessage());
            e.printStackTrace();
        } finally {
            HibernateUtil.close();
            System.out.println("\nПриложение завершено.");
        }
    }

    // Главное меню
    private static void mainMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║         ГЛАВНОЕ МЕНЮ               ║");
            System.out.println("╠════════════════════════════════════╣");
            System.out.println("║ 1. Заполнить таблицы данными       ║");
            System.out.println("║ 2. Очистить все таблицы            ║");
            System.out.println("║ 3. CRUD операции (демо)            ║");
            System.out.println("║ 4. CRUD операции (ручной ввод)     ║");
            System.out.println("║ 5. Бизнес-запросы                  ║");
            System.out.println("║ 6. Запустить всё сразу             ║");
            System.out.println("║ 0. Выход                           ║");
            System.out.println("╚════════════════════════════════════╝");
            System.out.print("Выберите действие: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> fillTables();
                case "2" -> clearTables();
                case "3" -> crudDemo();
                case "4" -> manualCrudMenu();
                case "5" -> businessQueriesMenu();
                case "6" -> runAll();
                case "0" -> {
                    running = false;
                    System.out.println("Завершение работы...");
                }
                default -> System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    // 1. Заполнение таблиц
    private static void fillTables() {
        System.out.println("\nЗаполнение таблиц тестовыми данными...");
        DataSeeder.seed();
        System.out.println("Готово!");
        pressEnterToContinue();
    }

    // 2. Очистка таблиц
    private static void clearTables() {
        DataSeeder.clearAll();
        System.out.println("Все таблицы очищены!");
        pressEnterToContinue();
    }

    // 3. CRUD демо
    private static void crudDemo() {
        CrudDemoService crudDemo = new CrudDemoService();
        crudDemo.runAll();
        pressEnterToContinue();
    }

    // 4. Меню ручного CRUD
    private static void manualCrudMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║      РУЧНЫЕ CRUD ОПЕРАЦИИ          ║");
            System.out.println("╠════════════════════════════════════╣");
            System.out.println("║ 1. Работа с клиентами              ║");
            System.out.println("║ 2. Работа с играми                 ║");
            System.out.println("║ 3. Работа с коробками              ║");
            System.out.println("║ 4. Работа с арендами               ║");
            System.out.println("║ 5. Работа с аттракциями            ║");
            System.out.println("║ 6. Работа с игровыми сессиями      ║");
            System.out.println("║ 0. Назад                           ║");
            System.out.println("╚════════════════════════════════════╝");
            System.out.print("Выберите таблицу: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> clientMenu();
                case "2" -> gameMenu();
                case "3" -> boxMenu();
                case "4" -> boxRentMenu();
                case "5" -> gameAttractionMenu();
                case "6" -> gameSessionMenu();
                case "0" -> running = false;
                default -> System.out.println("Неверный выбор.");
            }
        }
    }

    // Меню работы с клиентами
    private static void clientMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║        РАБОТА С КЛИЕНТАМИ          ║");
            System.out.println("╠════════════════════════════════════╣");
            System.out.println("║ 1. Показать всех клиентов          ║");
            System.out.println("║ 2. Найти клиента по паспорту       ║");
            System.out.println("║ 3. Добавить клиента                ║");
            System.out.println("║ 4. Обновить клиента                ║");
            System.out.println("║ 5. Удалить клиента                 ║");
            System.out.println("║ 0. Назад                           ║");
            System.out.println("╚════════════════════════════════════╝");
            System.out.print("Выберите действие: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> showAllClients();
                case "2" -> findClientByPassport();
                case "3" -> createClient();
                case "4" -> updateClient();
                case "5" -> deleteClient();
                case "0" -> running = false;
                default -> System.out.println("Неверный выбор.");
            }
        }
    }

    private static void showAllClients() {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<Client> clients = em.createQuery("FROM Client c ORDER BY c.lastName", Client.class)
                    .getResultList();
            System.out.println("\nВсе клиенты:");
            System.out.printf("%-12s %-13s %-15s %-15s %-20s%n",
                    "Паспорт", "Телефон", "Фамилия", "Имя", "Отчество");
            System.out.println("─".repeat(77));
            for (Client c : clients) {
                System.out.printf("%-12s %-13s %-15s %-15s %-20s%n",
                        c.getPassNumber(), c.getPhoneNumber(), c.getLastName(), c.getName(),
                        c.getPatronymic() != null ? c.getPatronymic() : "—");
            }
        }
        pressEnterToContinue();
    }

    private static void findClientByPassport() {
        System.out.println("Поиск клиента:");
        System.out.println("1. По номеру паспорта");
        System.out.println("2. По номеру телефона");
        System.out.print("Выберите способ поиска: ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1" -> {
                System.out.print("Введите номер паспорта: ");
                String passport = scanner.nextLine().trim();
                clientRepo.findById(passport).ifPresentOrElse(
                        c -> System.out.println("Найден: " + c),
                        () -> System.out.println("Клиент не найден")
                );
            }
            case "2" -> {
                System.out.print("Введите номер телефона: ");
                String phone = scanner.nextLine().trim();
                clientRepo.findByPhoneNumber(phone).ifPresentOrElse(
                        c -> System.out.println("Найден: " + c),
                        () -> System.out.println("Клиент не найден")
                );
            }
            default -> System.out.println("Неверный выбор.");
        }
        pressEnterToContinue();
    }

    private static void createClient() {
        System.out.println("\nДобавление нового клиента:");
        System.out.print("Паспорт (формат XXXX XXXXXX): ");
        String passport = scanner.nextLine().trim();
        System.out.print("Номер телефона (11 цифр, например 89001234567): ");
        String phone = scanner.nextLine().trim();

        // Валидация формата
        if (!phone.matches("^[87]\\d{10}$")) {
            System.out.println("Ошибка: номер должен содержать 11 цифр, начинаться с 8 или 7");
            pressEnterToContinue();
            return;
        }

        System.out.print("Фамилия: ");
        String lastName = scanner.nextLine().trim();
        System.out.print("Имя: ");
        String name = scanner.nextLine().trim();
        System.out.print("Отчество (или Enter для пропуска): ");
        String patronymic = scanner.nextLine().trim();
        if (patronymic.isEmpty()) patronymic = null;

        try {
            Client client = new Client(passport, phone, name, lastName, patronymic);
            clientRepo.save(client);
            System.out.println("Клиент успешно добавлен!");
        } catch (Exception e) {
            System.err.println("Ошибка при добавлении: " + e.getMessage());
        }
        pressEnterToContinue();
    }

    private static void updateClient() {
        System.out.print("Введите паспорт клиента для обновления: ");
        String passport = scanner.nextLine().trim();
        clientRepo.findById(passport).ifPresentOrElse(
                c -> {
                    System.out.println("Текущие данные: " + c);
                    System.out.print("Новый телефон (11 цифр или Enter для пропуска): ");
                    String newPhone = scanner.nextLine().trim();
                    if (!newPhone.isEmpty()) {
                        if (!newPhone.matches("^[87]\\d{10}$")) {
                            System.out.println("Ошибка: номер должен содержать 11 цифр, начинаться с 8 или 7");
                            pressEnterToContinue();
                            return;
                        }
                        c.setPhoneNumber(newPhone);
                    }
                    System.out.print("Новая фамилия (или Enter для пропуска): ");
                    String newLastName = scanner.nextLine().trim();
                    if (!newLastName.isEmpty()) c.setLastName(newLastName);
                    System.out.print("Новое имя (или Enter для пропуска): ");
                    String newName = scanner.nextLine().trim();
                    if (!newName.isEmpty()) c.setName(newName);
                    System.out.print("Новое отчество (или Enter для пропуска): ");
                    String newPatronymic = scanner.nextLine().trim();
                    if (!newPatronymic.isEmpty()) c.setPatronymic(newPatronymic);
                    clientRepo.update(c);
                    System.out.println("Клиент обновлён!");
                },
                () -> System.out.println("Клиент не найден")
        );
        pressEnterToContinue();
    }

    private static void deleteClient() {
        System.out.print("Введите паспорт клиента для удаления: ");
        String passport = scanner.nextLine().trim();
        clientRepo.findById(passport).ifPresentOrElse(
                c -> {
                    boolean success = clientRepo.deleteById(passport);
                    System.out.println(success ? "Клиент удалён!" : "Ошибка при удалении");
                },
                () -> System.out.println("Клиент не найден")
        );
        pressEnterToContinue();
    }

    // Меню работы с играми
    private static void gameMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║         РАБОТА С ИГРАМИ            ║");
            System.out.println("╠════════════════════════════════════╣");
            System.out.println("║ 1. Показать все игры               ║");
            System.out.println("║ 2. Найти игру по ID                ║");
            System.out.println("║ 3. Добавить игру                   ║");
            System.out.println("║ 4. Обновить игру                   ║");
            System.out.println("║ 5. Удалить игру                    ║");
            System.out.println("║ 0. Назад                           ║");
            System.out.println("╚════════════════════════════════════╝");
            System.out.print("Выберите действие: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> showAllGames();
                case "2" -> findGameById();
                case "3" -> createGame();
                case "4" -> updateGame();
                case "5" -> deleteGame();
                case "0" -> running = false;
                default -> System.out.println("Неверный выбор.");
            }
        }
    }

    private static void showAllGames() {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<Game> games = em.createQuery("FROM Game g ORDER BY g.id", Game.class).getResultList();
            System.out.println("\nВсе игры:");
            System.out.printf("%-5s %-25s %-20s %-12s %-10s%n", "ID", "Название", "Автор", "Сложность", "Время");
            System.out.println("─".repeat(74));
            for (Game g : games) {
                System.out.printf("%-5d %-25s %-20s %-12s %-10s%n",
                        g.getId(), g.getName(), g.getAuthors(),
                        g.getDifficulty().getDisplayName(),
                        g.getAvgGameTime().getHour() + "ч " + g.getAvgGameTime().getMinute() + "мин");
            }
        }
        pressEnterToContinue();
    }

    private static void findGameById() {
        System.out.print("Введите ID игры: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        gameRepo.findById(id).ifPresentOrElse(
                g -> System.out.println("Найдена: " + g),
                () -> System.out.println("Игра не найдена")
        );
        pressEnterToContinue();
    }

    private static void createGame() {
        System.out.println("\nДобавление новой игры:");
        System.out.print("Название: ");
        String name = scanner.nextLine().trim();
        System.out.print("Автор: ");
        String authors = scanner.nextLine().trim();
        System.out.print("Сложность (EASY/MEDIUM/HARD/EXPERT): ");
        String diffStr = scanner.nextLine().trim().toUpperCase();
        GameDifficulty difficulty = GameDifficulty.valueOf(diffStr);
        System.out.print("Среднее время (формат ЧЧ:ММ, например 02:30): ");
        LocalTime avgTime = LocalTime.parse(scanner.nextLine().trim());

        try {
            Game game = new Game(name, authors, difficulty, avgTime);
            gameRepo.save(game);
            System.out.println("Игра успешно добавлена!");
        } catch (Exception e) {
            System.err.println("Ошибка при добавлении: " + e.getMessage());
        }
        pressEnterToContinue();
    }

    private static void updateGame() {
        System.out.print("Введите ID игры для обновления: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        gameRepo.findById(id).ifPresentOrElse(
                g -> {
                    System.out.println("Текущие данные: " + g);
                    System.out.print("Новое название (или Enter): ");
                    String newName = scanner.nextLine().trim();
                    if (!newName.isEmpty()) g.setName(newName);
                    System.out.print("Новый автор (или Enter): ");
                    String newAuthors = scanner.nextLine().trim();
                    if (!newAuthors.isEmpty()) g.setAuthors(newAuthors);
                    System.out.print("Новая сложность (EASY/MEDIUM/HARD/EXPERT или Enter): ");
                    String newDiff = scanner.nextLine().trim().toUpperCase();
                    if (!newDiff.isEmpty()) g.setDifficulty(GameDifficulty.valueOf(newDiff));
                    gameRepo.update(g);
                    System.out.println("Игра обновлена!");
                },
                () -> System.out.println("Игра не найдена")
        );
        pressEnterToContinue();
    }

    private static void deleteGame() {
        System.out.print("Введите ID игры для удаления: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        gameRepo.findById(id).ifPresentOrElse(
                g -> {
                    boolean success = gameRepo.deleteById(id);
                    System.out.println(success ? "Игра удалена!" : "Ошибка при удалении");
                },
                () -> System.out.println("Игра не найдена")
        );
        pressEnterToContinue();
    }

    private static void boxMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║       РАБОТА С КОРОБКАМИ           ║");
            System.out.println("╠════════════════════════════════════╣");
            System.out.println("║ 1. Показать все коробки            ║");
            System.out.println("║ 2. Найти коробку по ID             ║");
            System.out.println("║ 3. Добавить коробку                ║");
            System.out.println("║ 4. Обновить состояние коробки      ║");
            System.out.println("║ 5. Удалить коробку                 ║");
            System.out.println("║ 0. Назад                           ║");
            System.out.println("╚════════════════════════════════════╝");
            System.out.print("Выберите действие: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> showAllBoxes();
                case "2" -> findBoxById();
                case "3" -> createBox();
                case "4" -> updateBox();
                case "5" -> deleteBox();
                case "0" -> running = false;
                default -> System.out.println("Неверный выбор.");
            }
        }
    }

    private static void showAllBoxes() {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<Box> boxes = em.createQuery(
                    "SELECT b FROM Box b JOIN FETCH b.game ORDER BY b.id", Box.class
            ).getResultList();

            System.out.println("\nВсе коробки:");
            System.out.printf("%-5s %-25s %-10s %-10s%n", "ID", "Игра", "№ коробки", "Состояние");
            System.out.println("─".repeat(52));
            for (Box b : boxes) {
                System.out.printf("%-5d %-25s %-10d %-10d%n",
                        b.getId(), b.getGame().getName(), b.getBoxNumber(), b.getStatus());
            }
        }
        pressEnterToContinue();
    }

    private static void findBoxById() {
        System.out.print("Введите ID коробки: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        boxRepo.findById(id).ifPresentOrElse(
                b -> {
                    try (EntityManager em = HibernateUtil.createEntityManager()) {
                        Box box = em.createQuery(
                                "SELECT b FROM Box b JOIN FETCH b.game WHERE b.id = :id", Box.class
                        ).setParameter("id", id).getSingleResult();
                        System.out.println("Найдена: коробка #" + box.getBoxNumber() +
                                ", игра='" + box.getGame().getName() +
                                "', состояние=" + box.getStatus());
                    }
                },
                () -> System.out.println("Коробка не найдена")
        );
        pressEnterToContinue();
    }

    private static void createBox() {
        System.out.println("\nДобавление новой коробки:");

        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<Game> games = em.createQuery("FROM Game g ORDER BY g.id", Game.class).getResultList();
            System.out.println("Доступные игры:");
            for (Game g : games) {
                System.out.printf("  ID=%d: %s (%s)%n", g.getId(), g.getName(), g.getDifficulty().getDisplayName());
            }
        }

        System.out.print("Введите ID игры: ");
        int gameId = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Номер коробки: ");
        int boxNumber = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Состояние (0-100): ");
        int status = Integer.parseInt(scanner.nextLine().trim());

        try (EntityManager em = HibernateUtil.createEntityManager()) {
            Game game = em.find(Game.class, gameId);
            if (game == null) {
                System.out.println("Игра с ID=" + gameId + " не найдена!");
                pressEnterToContinue();
                return;
            }
            Box box = new Box(game, boxNumber, status);
            boxRepo.save(box);
            System.out.println("Коробка успешно добавлена!");
        } catch (Exception e) {
            System.err.println("Ошибка при добавлении: " + e.getMessage());
        }
        pressEnterToContinue();
    }

    private static void updateBox() {
        System.out.print("Введите ID коробки для обновления: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        boxRepo.findById(id).ifPresentOrElse(
                b -> {
                    System.out.println("Текущее состояние: " + b.getStatus());
                    System.out.print("Новое состояние (0-100, или Enter для пропуска): ");
                    String newStatus = scanner.nextLine().trim();
                    if (!newStatus.isEmpty()) {
                        b.setStatus(Integer.parseInt(newStatus));
                        boxRepo.update(b);
                        System.out.println("Состояние обновлено!");
                    } else {
                        System.out.println("Обновление отменено.");
                    }
                },
                () -> System.out.println("Коробка не найдена")
        );
        pressEnterToContinue();
    }

    private static void deleteBox() {
        System.out.print("Введите ID коробки для удаления: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        boxRepo.findById(id).ifPresentOrElse(
                b -> {
                    boolean success = boxRepo.deleteById(id);
                    System.out.println(success ? "Коробка удалена!" : "Ошибка при удалении (возможно, есть активные аренды)");
                },
                () -> System.out.println("Коробка не найдена")
        );
        pressEnterToContinue();
    }

    private static void boxRentMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║       РАБОТА С АРЕНДАМИ            ║");
            System.out.println("╠════════════════════════════════════╣");
            System.out.println("║ 1. Показать все аренды             ║");
            System.out.println("║ 2. Найти аренду по ID              ║");
            System.out.println("║ 3. Создать аренду                  ║");
            System.out.println("║ 4. Завершить аренду (возврат)      ║");
            System.out.println("║ 5. Удалить аренду                  ║");
            System.out.println("║ 0. Назад                           ║");
            System.out.println("╚════════════════════════════════════╝");
            System.out.print("Выберите действие: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> showAllRents();
                case "2" -> findRentById();
                case "3" -> createRent();
                case "4" -> completeRent();
                case "5" -> deleteRent();
                case "0" -> running = false;
                default -> System.out.println("Неверный выбор.");
            }
        }
    }

    private static void showAllRents() {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<BoxRent> rents = em.createQuery(
                    "SELECT br FROM BoxRent br " +
                            "JOIN FETCH br.client " +
                            "JOIN FETCH br.box b " +
                            "JOIN FETCH b.game " +
                            "ORDER BY br.id", BoxRent.class
            ).getResultList();

            System.out.println("\nВсе аренды:");
            System.out.printf("%-5s %-12s %-20s %-12s %-12s %-10s%n",
                    "ID", "Клиент", "Игра", "Выдана", "Вернуть", "Статус");
            System.out.println("─".repeat(73));
            for (BoxRent r : rents) {
                System.out.printf("%-5d %-12s %-20s %-12s %-12s %-10s%n",
                        r.getId(),
                        r.getClient().getPassNumber(),
                        r.getBox().getGame().getName(),
                        r.getDateOfRent().toLocalDate(),
                        r.getDateOfReturn().toLocalDate(),
                        r.getStatus());
            }
        }
        pressEnterToContinue();
    }

    private static void findRentById() {
        System.out.print("Введите ID аренды: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        boxRentRepo.findById(id).ifPresentOrElse(
                r -> {
                    try (EntityManager em = HibernateUtil.createEntityManager()) {
                        BoxRent rent = em.createQuery(
                                "SELECT br FROM BoxRent br JOIN FETCH br.client " +
                                        "JOIN FETCH br.box b JOIN FETCH b.game WHERE br.id = :id",
                                BoxRent.class
                        ).setParameter("id", id).getSingleResult();
                        System.out.println("Аренда #" + rent.getId() +
                                ": клиент=" + rent.getClient().getPassNumber() +
                                ", игра='" + rent.getBox().getGame().getName() +
                                "', с " + rent.getDateOfRent().toLocalDate() +
                                " по " + rent.getDateOfReturn().toLocalDate() +
                                ", статус=" + rent.getStatus() +
                                ", штраф=" + rent.getFine());
                    }
                },
                () -> System.out.println("Аренда не найдена")
        );
        pressEnterToContinue();
    }

    private static void createRent() {
        System.out.println("\nСоздание новой аренды:");

        // Показываем клиентов
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<Client> clients = em.createQuery("FROM Client c ORDER BY c.passNumber", Client.class).getResultList();
            System.out.println("Доступные клиенты:");
            for (Client c : clients) {
                System.out.printf("  %s: %s %s%n", c.getPassNumber(), c.getLastName(), c.getName());
            }
        }
        System.out.print("Введите паспорт клиента: ");
        String clientPass = scanner.nextLine().trim();

        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<Box> boxes = em.createQuery(
                    "SELECT b FROM Box b JOIN FETCH b.game " +
                            "WHERE NOT EXISTS (SELECT br FROM BoxRent br WHERE br.box = b AND br.dateOfReturn > CURRENT_TIMESTAMP)",
                    Box.class
            ).getResultList();
            System.out.println("Свободные коробки:");
            for (Box b : boxes) {
                System.out.printf("  ID=%d: %s (коробка #%d, состояние=%d)%n",
                        b.getId(), b.getGame().getName(), b.getBoxNumber(), b.getStatus());
            }
        }
        System.out.print("Введите ID коробки: ");
        int boxId = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Дата возврата (формат YYYY-MM-DD, например 2026-07-10): ");
        LocalDate returnDate = LocalDate.parse(scanner.nextLine().trim());
        LocalDateTime dateOfReturn = returnDate.atTime(23, 59, 59);

        try (EntityManager em = HibernateUtil.createEntityManager()) {
            Client client = em.find(Client.class, clientPass);
            Box box = em.find(Box.class, boxId);

            if (client == null) {
                System.out.println("Клиент не найден!");
                pressEnterToContinue();
                return;
            }
            if (box == null) {
                System.out.println("Коробка не найдена!");
                pressEnterToContinue();
                return;
            }

            Long activeRents = em.createQuery(
                    "SELECT COUNT(br) FROM BoxRent br WHERE br.box.id = :boxId AND br.dateOfReturn > CURRENT_TIMESTAMP",
                    Long.class
            ).setParameter("boxId", boxId).getSingleResult();

            if (activeRents > 0) {
                System.out.println("Коробка уже арендована!");
                pressEnterToContinue();
                return;
            }

            BoxRent rent = new BoxRent(box, client, LocalDateTime.now(), dateOfReturn);
            rent.setStatus(RentStatus.ACTIVE);
            rent.setFine(0);
            boxRentRepo.save(rent);
            System.out.println("Аренда успешно создана!");
        } catch (Exception e) {
            System.err.println("Ошибка при создании: " + e.getMessage());
        }
        pressEnterToContinue();
    }

    private static void completeRent() {
        System.out.print("Введите ID аренды для завершения: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        boxRentRepo.findById(id).ifPresentOrElse(
                r -> {
                    System.out.print("Дата фактического возврата (YYYY-MM-DD, или Enter для сегодня): ");
                    String dateStr = scanner.nextLine().trim();
                    LocalDate returnDate = dateStr.isEmpty() ? LocalDate.now() : LocalDate.parse(dateStr);

                    System.out.print("Штраф (0 если нет): ");
                    int fine = Integer.parseInt(scanner.nextLine().trim());

                    try (EntityManager em = HibernateUtil.createEntityManager()) {
                        BoxRent rent = em.find(BoxRent.class, id);
                        rent.setDateOfReturn(returnDate.atTime(23, 59, 59));
                        rent.setFine(fine);
                        rent.setStatus(fine > 0 ? RentStatus.OVERDUE : RentStatus.RETURNED);
                        boxRentRepo.update(rent);
                        System.out.println("Аренда завершена!");
                    }
                },
                () -> System.out.println("Аренда не найдена")
        );
        pressEnterToContinue();
    }

    private static void deleteRent() {
        System.out.print("Введите ID аренды для удаления: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        boxRentRepo.findById(id).ifPresentOrElse(
                r -> {
                    boolean success = boxRentRepo.deleteById(id);
                    System.out.println(success ? "Аренда удалена!" : "Ошибка при удалении");
                },
                () -> System.out.println("Аренда не найдена")
        );
        pressEnterToContinue();
    }

    private static void gameAttractionMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║     РАБОТА С ИГРОТЕКАМИ            ║");
            System.out.println("╠════════════════════════════════════╣");
            System.out.println("║ 1. Показать все игротеки           ║");
            System.out.println("║ 2. Найти игротеку  по ID           ║");
            System.out.println("║ 3. Начать игротеку                 ║");
            System.out.println("║ 4. Завершить игротеку              ║");
            System.out.println("║ 5. Удалить игротеку                ║");
            System.out.println("║ 0. Назад                           ║");
            System.out.println("╚════════════════════════════════════╝");
            System.out.print("Выберите действие: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> showAllAttractions();
                case "2" -> findAttractionById();
                case "3" -> createAttraction();
                case "4" -> completeAttraction();
                case "5" -> deleteAttraction();
                case "0" -> running = false;
                default -> System.out.println("Неверный выбор.");
            }
        }
    }

    private static void showAllAttractions() {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<GameAttraction> attractions = em.createQuery(
                    "SELECT ga FROM GameAttraction ga " +
                            "JOIN FETCH ga.game " +
                            "JOIN FETCH ga.client " +
                            "ORDER BY ga.id", GameAttraction.class
            ).getResultList();

            System.out.println("\nВсе игротеки:");
            System.out.printf("%-5s %-12s %-20s %-12s %-12s%n",
                    "ID", "Клиент", "Игра", "Начало", "Конец");
            System.out.println("─".repeat(63));
            for (GameAttraction ga : attractions) {
                System.out.printf("%-5d %-12s %-20s %-12s %-12s%n",
                        ga.getId(),
                        ga.getClient().getPassNumber(),
                        ga.getGame().getName(),
                        ga.getStartTime().toLocalDate(),
                        ga.getEndTime().toLocalDate());
            }
        }
        pressEnterToContinue();
    }

    private static void findAttractionById() {
        System.out.print("Введите ID игротеки: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        gameAttractionRepo.findById(id).ifPresentOrElse(
                ga -> {
                    try (EntityManager em = HibernateUtil.createEntityManager()) {
                        GameAttraction attraction = em.createQuery(
                                "SELECT ga FROM GameAttraction ga JOIN FETCH ga.game " +
                                        "JOIN FETCH ga.client WHERE ga.id = :id", GameAttraction.class
                        ).setParameter("id", id).getSingleResult();
                        System.out.println("Игротека #" + attraction.getId() +
                                ": клиент=" + attraction.getClient().getPassNumber() +
                                ", игра='" + attraction.getGame().getName() +
                                "', с " + attraction.getStartTime() +
                                " по " + attraction.getEndTime());
                    }
                },
                () -> System.out.println("Игротека не найдена")
        );
        pressEnterToContinue();
    }

    private static void createAttraction() {
        System.out.println("\nНачало новой игротеки:");

        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<Client> clients = em.createQuery("FROM Client c ORDER BY c.passNumber", Client.class).getResultList();
            System.out.println("Доступные клиенты:");
            for (Client c : clients) {
                System.out.printf("  %s: %s %s%n", c.getPassNumber(), c.getLastName(), c.getName());
            }
        }
        System.out.print("Введите паспорт клиента: ");
        String clientPass = scanner.nextLine().trim();

        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<Game> games = em.createQuery("FROM Game g ORDER BY g.id", Game.class).getResultList();
            System.out.println("Доступные игры:");
            for (Game g : games) {
                System.out.printf("  ID=%d: %s%n", g.getId(), g.getName());
            }
        }
        System.out.print("Введите ID игры: ");
        int gameId = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Продолжительность в часах (например, 2): ");
        int hours = Integer.parseInt(scanner.nextLine().trim());
        LocalDateTime endTime = LocalDateTime.now().plusHours(hours);

        try (EntityManager em = HibernateUtil.createEntityManager()) {
            Client client = em.find(Client.class, clientPass);
            Game game = em.find(Game.class, gameId);

            if (client == null) {
                System.out.println("Клиент не найден!");
                pressEnterToContinue();
                return;
            }
            if (game == null) {
                System.out.println("Игра не найдена!");
                pressEnterToContinue();
                return;
            }

            Long activeAttractions = em.createQuery(
                    "SELECT COUNT(ga) FROM GameAttraction ga WHERE ga.client.passNumber = :pass AND ga.game.id = :gameId AND ga.endTime > CURRENT_TIMESTAMP",
                    Long.class
            ).setParameter("pass", clientPass).setParameter("gameId", gameId).getSingleResult();

            if (activeAttractions > 0) {
                System.out.println("Клиент уже играет в эту игру!");
                pressEnterToContinue();
                return;
            }

            GameAttraction attraction = new GameAttraction(game, client, LocalDateTime.now(), endTime);
            gameAttractionRepo.save(attraction);
            System.out.println("Игротека начата! ID=" + attraction.getId());
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
        pressEnterToContinue();
    }

    private static void completeAttraction() {
        System.out.print("Введите ID игротеки для завершения: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        gameAttractionRepo.findById(id).ifPresentOrElse(
                ga -> {
                    try (EntityManager em = HibernateUtil.createEntityManager()) {
                        GameAttraction attraction = em.find(GameAttraction.class, id);
                        attraction.setEndTime(LocalDateTime.now());
                        gameAttractionRepo.update(attraction);
                        System.out.println("Игротека завершена!");
                    }
                },
                () -> System.out.println("Игротека не найдена")
        );
        pressEnterToContinue();
    }

    private static void deleteAttraction() {
        System.out.print("Введите ID игротеки для удаления: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        gameAttractionRepo.findById(id).ifPresentOrElse(
                ga -> {
                    boolean success = gameAttractionRepo.deleteById(id);
                    System.out.println(success ? "Игротека удалена!" : "Ошибка при удалении");
                },
                () -> System.out.println("Игротека не найдена")
        );
        pressEnterToContinue();
    }

    private static void gameSessionMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║     РАБОТА С ИГРОВЫМИ СЕССИЯМИ     ║");
            System.out.println("╠════════════════════════════════════╣");
            System.out.println("║ 1. Показать все сессии             ║");
            System.out.println("║ 2. Найти сессию по ID              ║");
            System.out.println("║ 3. Начать сессию                   ║");
            System.out.println("║ 4. Завершить сессию (результат)    ║");
            System.out.println("║ 5. Удалить сессию                  ║");
            System.out.println("║ 0. Назад                           ║");
            System.out.println("╚════════════════════════════════════╝");
            System.out.print("Выберите действие: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> showAllSessions();
                case "2" -> findSessionById();
                case "3" -> createSession();
                case "4" -> completeSession();
                case "5" -> deleteSession();
                case "0" -> running = false;
                default -> System.out.println("Неверный выбор.");
            }
        }
    }

    private static void showAllSessions() {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<GameSession> sessions = em.createQuery(
                    "SELECT gs FROM GameSession gs " +
                            "JOIN FETCH gs.game " +
                            "JOIN FETCH gs.client " +
                            "ORDER BY gs.id", GameSession.class
            ).getResultList();

            System.out.println("\nВсе сессии:");
            System.out.printf("%-5s %-12s %-20s %-12s %-12s %-8s%n",
                    "ID", "Клиент", "Игра", "Начало", "Конец", "Результат");
            System.out.println("─".repeat(71));
            for (GameSession gs : sessions) {
                String result = gs.isGameResult() ? "Победа" : "Поражение";
                System.out.printf("%-5d %-12s %-20s %-12s %-12s %-8s%n",
                        gs.getId(),
                        gs.getClient().getPassNumber(),
                        gs.getGame().getName(),
                        gs.getStartTime().toLocalDate(),
                        gs.getEndTime().toLocalDate(),
                        result);
            }
        }
        pressEnterToContinue();
    }

    private static void findSessionById() {
        System.out.print("Введите ID сессии: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        gameSessionRepo.findById(id).ifPresentOrElse(
                gs -> {
                    try (EntityManager em = HibernateUtil.createEntityManager()) {
                        GameSession session = em.createQuery(
                                "SELECT gs FROM GameSession gs JOIN FETCH gs.game " +
                                        "JOIN FETCH gs.client WHERE gs.id = :id", GameSession.class
                        ).setParameter("id", id).getSingleResult();
                        String result = session.isGameResult() ? "Победа" : "Поражение";
                        System.out.println("Сессия #" + session.getId() +
                                ": клиент=" + session.getClient().getPassNumber() +
                                ", игра='" + session.getGame().getName() +
                                "', результат=" + result);
                    }
                },
                () -> System.out.println("Сессия не найдена")
        );
        pressEnterToContinue();
    }

    private static void createSession() {
        System.out.println("\nНачало новой игровой сессии:");

        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<Client> clients = em.createQuery("FROM Client c ORDER BY c.passNumber", Client.class).getResultList();
            System.out.println("Доступные клиенты:");
            for (Client c : clients) {
                System.out.printf("  %s: %s %s%n", c.getPassNumber(), c.getLastName(), c.getName());
            }
        }
        System.out.print("Введите паспорт клиента: ");
        String clientPass = scanner.nextLine().trim();

        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<Game> games = em.createQuery("FROM Game g ORDER BY g.id", Game.class).getResultList();
            System.out.println("Доступные игры:");
            for (Game g : games) {
                System.out.printf("  ID=%d: %s%n", g.getId(), g.getName());
            }
        }
        System.out.print("Введите ID игры: ");
        int gameId = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Продолжительность в часах (например, 2): ");
        int hours = Integer.parseInt(scanner.nextLine().trim());
        LocalDateTime endTime = LocalDateTime.now().plusHours(hours);

        try (EntityManager em = HibernateUtil.createEntityManager()) {
            Client client = em.find(Client.class, clientPass);
            Game game = em.find(Game.class, gameId);

            if (client == null) {
                System.out.println("Клиент не найден!");
                pressEnterToContinue();
                return;
            }
            if (game == null) {
                System.out.println("Игра не найдена!");
                pressEnterToContinue();
                return;
            }

            Long activeSessions = em.createQuery(
                    "SELECT COUNT(gs) FROM GameSession gs WHERE gs.client.passNumber = :pass AND gs.game.id = :gameId AND gs.endTime > CURRENT_TIMESTAMP",
                    Long.class
            ).setParameter("pass", clientPass).setParameter("gameId", gameId).getSingleResult();

            if (activeSessions > 0) {
                System.out.println("У клиента уже есть активная сессия в этой игре!");
                pressEnterToContinue();
                return;
            }

            GameSession session = new GameSession(game, client, LocalDateTime.now(), endTime, false);
            gameSessionRepo.save(session);
            System.out.println("Сессия начата! ID=" + session.getId());
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
        pressEnterToContinue();
    }

    private static void completeSession() {
        System.out.print("Введите ID сессии для завершения: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        gameSessionRepo.findById(id).ifPresentOrElse(
                gs -> {
                    System.out.print("Результат (true=победа, false=поражение): ");
                    boolean result = Boolean.parseBoolean(scanner.nextLine().trim());

                    try (EntityManager em = HibernateUtil.createEntityManager()) {
                        GameSession session = em.find(GameSession.class, id);
                        session.setEndTime(LocalDateTime.now());
                        session.setGameResult(result);
                        gameSessionRepo.update(session);
                        System.out.println("Сессия завершена! Результат: " + (result ? "Победа" : "Поражение"));
                    }
                },
                () -> System.out.println("Сессия не найдена")
        );
        pressEnterToContinue();
    }

    private static void deleteSession() {
        System.out.print("Введите ID сессии для удаления: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        gameSessionRepo.findById(id).ifPresentOrElse(
                gs -> {
                    boolean success = gameSessionRepo.deleteById(id);
                    System.out.println(success ? "Сессия удалена!" : "Ошибка при удалении");
                },
                () -> System.out.println("Сессия не найдена")
        );
        pressEnterToContinue();
    }

    // 5. Меню бизнес-запросов
    private static void businessQueriesMenu() {
        BusinessQueryService queryService = new BusinessQueryService();
        boolean running = true;
        while (running) {
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║        БИЗНЕС-ЗАПРОСЫ              ║");
            System.out.println("╠════════════════════════════════════╣");
            System.out.println("║ 1. Количество аренд по играм       ║");
            System.out.println("║ 2. Активные аренды у клиентов      ║");
            System.out.println("║ 3. Топ-3 клиентов по победам       ║");
            System.out.println("║ 4. Игры с количеством коробок      ║");
            System.out.println("║ 5. Коробки без аренд               ║");
            System.out.println("║ 6. Среднее время аренды            ║");
            System.out.println("║ 7. Статистика аттракций            ║");
            System.out.println("║ 8. Проблемные клиенты              ║");
            System.out.println("║ 9. Просроченные аренды             ║");
            System.out.println("║ 10. Полная статистика по играм     ║");
            System.out.println("║ 11. Запустить все запросы          ║");
            System.out.println("║ 0. Назад                           ║");
            System.out.println("╚════════════════════════════════════╝");
            System.out.print("Выберите запрос: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> queryService.rentalCountByGame();
                case "2" -> queryService.getActiveRentals();
                case "3" -> queryService.getTop3Winners();
                case "4" -> queryService.getGamesWithBoxCount();
                case "5" -> queryService.getUnrentedBoxes();
                case "6" -> queryService.getAvgRentDurationByDifficulty();
                case "7" -> queryService.getAttractionsStatsByGame();
                case "8" -> queryService.getProblematicClients();
                case "9" -> queryService.getOverdueRentals();
                case "10" -> queryService.getGamesFullStatistics();
                case "11" -> queryService.runAll();
                case "0" -> running = false;
                default -> System.out.println("Неверный выбор.");
            }
        }
    }

    // 6. Запустить всё сразу
    private static void runAll() {
        System.out.println("\nЗапуск всех операций...\n");
        DataSeeder.seed();
        CrudDemoService crudDemo = new CrudDemoService();
        crudDemo.runAll();
        BusinessQueryService queryService = new BusinessQueryService();
        queryService.runAll();
        pressEnterToContinue();
    }

    // Вспомогательный метод
    private static void pressEnterToContinue() {
        System.out.print("\nНажмите Enter для продолжения...");
        scanner.nextLine();
    }
}