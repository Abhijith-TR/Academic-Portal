package org.example.ui;

import org.example.dal.Database;

import java.util.Scanner;

public class HomeInterface {
    Database passwordAuthConnection;
    String centralConnectionURl = "jdbc:postgresql://localhost:5432/mini_project";
    String studentUsername = "postgres";
    String studentPassword = "admin";
    String facultyUsername = "postgres";
    String facultyPassword = "admin";
    String adminUsername = "postgres";
    String adminPassword = "admin";
    // Roles in the database system - to allow easy extension
    enum Role {
        STUDENT,
        FACULTY,
        ADMIN
    }

    public HomeInterface(String connectionURL, String username, String password) {
        passwordAuthConnection = new Database(connectionURL, username, password);
    }

    public void mainInterface() {
        Scanner keyboardInput = new Scanner(System.in);
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
            System.out.print("Enter the number corresponding to the role: ");
            int roleIndex = keyboardInput.nextInt();

            if (roleIndex >= Role.values().length || roleIndex <= 0) {
                System.out.println("Invalid Choice");
                continue;
            }

            // Remove the space after the integer in keyboardInput
            keyboardInput.nextLine();

            // Enter the id and password
            System.out.print("Enter your id: ");
            String id = keyboardInput.nextLine();

            System.out.print("Enter your password: ");
            String password = keyboardInput.nextLine();

            boolean isValidUser = passwordAuthConnection.authenticateUser(id, password, Role.values()[roleIndex-1].name().toLowerCase());
            if (isValidUser == false) {
                System.out.println("Invalid Username or Password");
                continue;
            }

            // If the id and password entered do not match OR the id does not exist
            if (roleIndex == 1) {
                new StudentInterface(centralConnectionURl, studentUsername, studentPassword, id);
            } else if (roleIndex == 2) {
                new FacultyInterface();
            } else if (roleIndex == 3) {
                new AdminInterface();
            }
        }
    }

}
