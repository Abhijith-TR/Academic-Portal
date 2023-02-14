package org.example.ui;

import org.example.dal.Database;
import org.example.utils.CustomScanner;

public class HomeUI {
    Database passwordAuthConnection;
    String   centralConnectionURl = "jdbc:postgresql://localhost:5432/mini_project";
    String   studentUsername      = "postgres";
    String   studentPassword      = "admin";
    String   facultyUsername      = "postgres";
    String   facultyPassword      = "admin";
    String   adminUsername        = "postgres";
    String   adminPassword        = "admin";

    // Roles in the database system - to allow easy extension
    enum Role {
        STUDENT,
        FACULTY,
        ADMIN
    }

    public HomeUI(String connectionURL, String username, String password) {
        passwordAuthConnection = new Database(connectionURL, username, password);
    }

    public void mainInterface() {
        CustomScanner keyboardInput = new CustomScanner();
        while (true) {
            System.out.println();
            System.out.println("Welcome to the Academic System");
            System.out.println("Select your role");

            // Short-lived iterator variable
            int i = 1;
            for (Role role : Role.values()) {
                System.out.printf("%d. %s\n", i, role);
                i++;
            }

            // Index of the role in the enum roles
            int roleIndex = keyboardInput.integerInput("Enter the number corresponding to the role");

            if (roleIndex > Role.values().length || roleIndex <= 0) {
                System.out.println("Invalid Choice");
                continue;
            }

            // Enter the id and password
            String id       = keyboardInput.stringInput("Enter your id");
            String password = keyboardInput.stringInput("Enter your password");

            boolean isValidUser = passwordAuthConnection.authenticateUser(id, password, Role.values()[roleIndex - 1].name().toLowerCase());
            if (!isValidUser) {
                System.out.println("Invalid Username or Password");
                continue;
            }

            // If the id and password entered do not match OR the id does not exist
            if (roleIndex == 1) {
                new StudentUI(centralConnectionURl, studentUsername, studentPassword, id);
            } else if (roleIndex == 2) {
                new FacultyUI();
            } else if (roleIndex == 3) {
                new AdminUI(centralConnectionURl, adminUsername, adminPassword, id);
            }
            while (true) {
                if (passwordAuthConnection.logLogoutEntry(id, Role.values()[roleIndex - 1].name().toLowerCase())) break;
            }
        }
    }

}
