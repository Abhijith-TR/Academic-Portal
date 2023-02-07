package org.example;

import org.example.database.Database;

import java.util.Scanner;

public class Main {
    // Roles in the database system - to allow easy extension
    enum Role {
        STUDENT,
        FACULTY,
        ADMIN
    };

    // Function to authenticate username and password from the database
    static void authenticate(String username, String password, String role, Database databaseConnection) {
        System.out.println(role);
    }

    static void loginInterface(Database databaseConnection) {
        Scanner keyboardInput = new Scanner(System.in);
        while (true) {
            System.out.println("Welcome to the Academic System");
            // Short-lived iterator variable
            int i = 1;
            for (Role role : Role.values()) {
                System.out.printf("%d. %s\n",i, role);
                i++;
            }

            // Index of the role in the enum roles
            int roleIndex = keyboardInput.nextInt();

            // Remove the space after the integer in keyboardInput
            String space = keyboardInput.nextLine();

            // Enter the username and password
            String username = keyboardInput.nextLine();
            String password = keyboardInput.nextLine();

            authenticate(username, password, Role.values()[roleIndex-1].name(), databaseConnection);
        }
    }

    public static void main(String[] args) {
        Database databaseConnection = new Database();
        loginInterface(databaseConnection);
    }
}