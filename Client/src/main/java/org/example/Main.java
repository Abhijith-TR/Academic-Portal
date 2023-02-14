package org.example;

import org.example.ui.HomeUI;

public class Main {
    public static void main(String[] args) {
        String connectionURL = "jdbc:postgresql://localhost:5432/mini_project";
        String username      = "postgres";
        String password      = "admin";
        HomeUI homeUI        = new HomeUI(connectionURL, username, password);
        homeUI.mainInterface();
    }
}