package org.example;

import org.example.dal.Database;
import org.example.interfaces.HomeInterface;

public class Main {
    public static void main(String[] args) {
        Database databaseConnection = new Database(
                "jdbc:postgresql://localhost:5432/mini_project",
                "postgres",
                "admin"
                );
        HomeInterface homeInterface = new HomeInterface();
        homeInterface.mainInterface();
    }
}