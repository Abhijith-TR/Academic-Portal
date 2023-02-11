package org.example.interfaces;

import org.example.dal.Database;

import java.util.Scanner;

public class HomeInterface {
    // Roles in the database system - to allow easy extension
    enum Role {
        STUDENT,
        FACULTY,
        ADMIN
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

            // If the id and password entered do not match OR the id does not exist
            if (roleIndex == 1) {
                new StudentInterface();
            } else if (roleIndex == 2) {
                new FacultyInterface();
            } else if (roleIndex == 3) {
                new AdminInterface();
            }
        }
    }

}
