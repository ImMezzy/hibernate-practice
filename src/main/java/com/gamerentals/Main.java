package com.gamerentals;

import com.gamerentals.service.BusinessQueryService;
import com.gamerentals.service.CrudDemoService;
import com.gamerentals.util.DataSeeder;
import com.gamerentals.util.HibernateUtil;

public class Main {

    public static void main(String[] args) {
        try {
            // Инициализация EntityManagerFactory (аналог ConnectionManager.init())
            HibernateUtil.getEntityManagerFactory();
            DataSeeder.seed();
            System.out.println("Hibernate инициализирован, схема создана и заполнена\n");

            // CRUD-демонстрация
            System.out.println("CRUD-операции через Hibernate");

            CrudDemoService crudDemo = new CrudDemoService();
            crudDemo.runAll();

            // Бизнес-запросы
            System.out.println("Примеры бизнес-запросов (JPQL)");

            BusinessQueryService queryService = new BusinessQueryService();
            queryService.runAll();

        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        } finally {
            HibernateUtil.close();
            System.out.println("\nHibernate закрыт. Готово.");
        }
    }
}