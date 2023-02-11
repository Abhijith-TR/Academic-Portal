package org.example;

import org.example.ui.HomeInterface;

public class Main {
    public static void main(String[] args) {
        String connectionURL = "jdbc:postgresql://localhost:5432/mini_project";
        String username = "postgres";
        String password = "admin";
        HomeInterface homeInterface = new HomeInterface(connectionURL, username, password);
        homeInterface.mainInterface();
    }
}