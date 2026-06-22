package com.gamerentals.service;

import com.gamerentals.entity.*;
import com.gamerentals.repository.*;
import java.time.LocalTime;
import java.util.List;

public class CrudDemoService {

    private final ClientRepository clientRepo = new ClientRepository();
    private final GameRepository gameRepo = new GameRepository();
    private final BoxRepository boxRepo = new BoxRepository();
    private final BoxRentRepository boxRentRepo = new BoxRentRepository();
    private final GameAttractionRepository gameAttractionRepo = new GameAttractionRepository();
    private final GameSessionRepository gameSessionRepo = new GameSessionRepository();

    public void demoCreate() {
        printHeader("CREATE — Создание записей");

        Client client = clientRepo.save(new Client("8120 148867", "89991488670", "Иван", "Иванов", "Иванович"));
        System.out.printf("Создан клиент: passNumber=%s, %s %s%n",
                client.getPassNumber(), client.getLastName(), client.getName());

        Game game = gameRepo.save(new Game("Монополия", "Джеффри Эйнштейн", GameDifficulty.MEDIUM, LocalTime.parse("01:30")));
        System.out.printf("Создана игра: id=%d, '%s', сложность=%s, время=%d мин%n",
                game.getId(), game.getName(), game.getDifficulty(), game.getAvgGameTime().getMinute());

        Box box = boxRepo.save(new Box(game, 1, 100));
        System.out.printf("Создана коробка: id=%d, игра='%s', номер=%d, состояние=%s%n",
                box.getId(), game.getName(), box.getBoxNumber(), box.getStatus());

        int rentId = boxRentRepo.rentBox(client.getPassNumber(), box.getId());
        System.out.printf("Коробка арендована: rentId=%d, клиент=%s, коробка=%d%n",
                rentId, client.getPassNumber(), box.getId());

        BoxRent rent = boxRentRepo.findById(rentId).orElseThrow();
        System.out.printf("Детали аренды: дата выдачи=%s, дата возврата=%s%n",
                rent.getDateOfRent(), rent.getDateOfReturn());

        int attractionId = gameAttractionRepo.startAttraction(client.getPassNumber(), game.getId());
        System.out.printf("Начата аттракция: id=%d, игра='%s'%n", attractionId, game.getName());

        int sessionId = gameSessionRepo.startSession(client.getPassNumber(), game.getId());
        System.out.printf("Начата сессия: id=%d, игра='%s'%n", sessionId, game.getName());

        printDivider();
    }

    public void demoRead() {
        printHeader("READ — Чтение данных");

        // 1. Все клиенты
        System.out.println("Все клиенты:");
        List<Client> clients = clientRepo.findAll();
        System.out.printf("     %-12s %-20s %-20s %-20s%n", "Паспорт", "Фамилия", "Имя", "Отчество");
        System.out.println("     " + "─".repeat(74));
        for (Client c : clients) {
            System.out.printf("     %-12s %-20s %-20s %-20s%n",
                    c.getPassNumber(), c.getLastName(), c.getName(),
                    c.getPatronymic() != null ? c.getPatronymic() : "—");
        }

        // 2. Все игры с коробками
        System.out.println("\nВсе игры:");
        List<Game> games = gameRepo.findAll();
        System.out.printf("     %-5s %-25s %-15s %-10s %-12s%n",
                "ID", "Название", "Сложность", "Время", "Коробок");
        System.out.println("     " + "─".repeat(69));
        for (Game g : games) {
            List<Box> boxes = boxRepo.findByGameId(g.getId());
            int boxCount = boxes.size();
            System.out.printf("     %-5d %-25s %-15s %-10s %-12d%n",
                    g.getId(), g.getName(), g.getDifficulty().getDisplayName(),
                    g.getAvgGameTime().getMinute() + " мин", boxCount);
        }

        // 3. Все коробки
        System.out.println("\nВсе коробки:");
        List<Box> boxes = boxRepo.findAllWithGames();
        System.out.printf("     %-5s %-25s %-10s %-12s%n",
                "ID", "Игра", "№ коробки", "Состояние");
        System.out.println("     " + "─".repeat(54));
        for (Box b : boxes) {
            String gameName = b.getGame() != null ? b.getGame().getName() : "—";
            System.out.printf("     %-5d %-25s %-10d %-12s%n",
                    b.getId(), gameName, b.getBoxNumber(), b.getStatus());
        }

        // 4. Все аренды
        System.out.println("\nВсе аренды:");
        List<BoxRent> rents = boxRentRepo.findAllWithDetails();
        System.out.printf("     %-5s %-12s %-25s %-20s %-20s%n",
                "ID", "Клиент", "Игра", "Дата выдачи", "Дата возврата");
        System.out.println("     " + "─".repeat(84));
        for (BoxRent r : rents) {
            String clientPass = r.getClient() != null ? r.getClient().getPassNumber() : "—";
            String gameName = (r.getBox() != null && r.getBox().getGame() != null)
                    ? r.getBox().getGame().getName() : "—";
            System.out.printf("     %-5d %-12s %-25s %-20s %-20s%n",
                    r.getId(), clientPass, gameName,
                    r.getDateOfRent().toLocalDate(), r.getDateOfReturn().toLocalDate());
        }

        // 5. Поиск клиента по passNumber
        System.out.println("\nПоиск клиента с паспортом '8120 148867':");
        clientRepo.findById("8120 148867").ifPresentOrElse(
                c -> System.out.println("     " + c),
                () -> System.out.println("     Не найден")
        );

        // 6. Поиск игры по ID
        System.out.println("\nПоиск игры с id=1:");
        gameRepo.findById(1).ifPresentOrElse(
                g -> System.out.println("     " + g),
                () -> System.out.println("     Не найдена")
        );

        // 7. Аренды конкретного клиента
        System.out.println("\nАренды клиента '8120 148867':");
        List<BoxRent> clientRents = boxRentRepo.findByClientPassNumberWithDetails("8120 148867");
        if (clientRents.isEmpty()) {
            System.out.println("     У клиента нет аренд");
        } else {
            for (BoxRent r : clientRents) {
                String gameName = (r.getBox() != null && r.getBox().getGame() != null)
                        ? r.getBox().getGame().getName() : "—";
                System.out.printf("     Аренда #%d: игра='%s', с %s по %s%n",
                        r.getId(), gameName,
                        r.getDateOfRent().toLocalDate(), r.getDateOfReturn().toLocalDate());
            }
        }

        // 8. Активные аттракции клиента
        System.out.println("\nАктивные аттракции клиента '8120 148867':");
        List<GameAttraction> activeAttractions = gameAttractionRepo.findActiveByClientIdWithGame("8120 148867");
        if (activeAttractions.isEmpty()) {
            System.out.println("     Нет активных аттракций");
        } else {
            for (GameAttraction ga : activeAttractions) {
                System.out.printf("     Аттракция #%d: игра='%s', до %s%n",
                        ga.getId(), ga.getGame().getName(), ga.getEndTime());
            }
        }

        // 9. Активные сессии клиента
        System.out.println("\nАктивные сессии клиента '8120 148867':");
        List<GameSession> activeSessions = gameSessionRepo.findActiveByClientIdWithGame("8120 148867");
        if (activeSessions.isEmpty()) {
            System.out.println("     Нет активных сессий");
        } else {
            for (GameSession gs : activeSessions) {
                System.out.printf("     Сессия #%d: игра='%s', до %s%n",
                        gs.getId(), gs.getGame().getName(), gs.getEndTime());
            }
        }

        printDivider();
    }

    public void demoUpdate() {
        printHeader("UPDATE — Обновление данных");

        gameRepo.findById(1).ifPresent(g -> {
            GameDifficulty oldDifficulty = g.getDifficulty();
            g.setDifficulty(GameDifficulty.EXPERT);
            Game updated = gameRepo.update(g);
            System.out.printf("  Обновлена сложность игры id=1: '%s' → '%s'%n", oldDifficulty, updated.getDifficulty());
        });

        printDivider();
    }

    public void demoDelete() {
        printHeader("DELETE — Удаление данных");

        Client temp = clientRepo.save(new Client("8000 228337","85421320001", "Удали", "Удалёв", "Удалёвович"));
        System.out.printf("Создан временный: id=%s%n", temp.getPassNumber());

        boolean deleted = clientRepo.deleteById(temp.getPassNumber());
        System.out.printf(" Удалён passNumber=%s (успех=%b)%n", temp.getPassNumber(), deleted);

        boolean notFound = clientRepo.deleteById("7777 777777");
        System.out.printf(" Удаление несуществующего passNumber=7777 7777 (успех=%b)%n", notFound);

        printDivider();
    }

    public void demoTransaction() {
        printHeader("TRANSACTION — Аренда коробки");

        System.out.println("Аренда: посетитель=1, коробка=1, начало=now, конец=через 14 дней");
        try {
            int boxRentId = boxRentRepo.rentBox("8120 148867", 1);
            System.out.printf("Коробка арендована! id=%d%n", boxRentId);

            System.out.println("\nПовторная аренда той же коробки...");
            try {
                boxRentRepo.rentBox("8120 7", 1);
            } catch (IllegalStateException e) {
                System.out.printf("Ожидаемая ошибка: %s%n", e.getMessage());
            }

            boxRentRepo.deleteById(boxRentId);
        } catch (Exception e) {
            System.out.printf("%s%n", e.getMessage());
        }

        printDivider();
    }

    public void runAll() {
        demoCreate();
        demoRead();
        demoUpdate();
        demoDelete();
        demoTransaction();
    }


    public static void printHeader(String title) {
        System.out.println();
        System.out.println("╔" + "═".repeat(title.length() + 4) + "╗");
        System.out.println("║  " + title + "  ║");
        System.out.println("╚" + "═".repeat(title.length() + 4) + "╝");
    }

    public static void printDivider() {
        System.out.println("─".repeat(80));
    }
}
