package org.example.database;

import java.sql.Connection;
import java.sql.DriverManager;

public class Database {
    Connection databaseConnection;
    public Database() {
        try {
            databaseConnection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/mini_project",
                    "postgres",
                    "admin"
            );
            System.out.println("Database connection established");
        } catch (Exception error) {
            System.err.println("Could not connect to database. Shutting down.");
            System.exit(0);
        }

    }
}
