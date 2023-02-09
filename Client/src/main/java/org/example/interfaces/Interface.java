package org.example.interfaces;

import org.example.users.Admin;
import org.example.database.Database;
import org.example.users.Faculty;
import org.example.users.Student;

import java.util.Scanner;

public class Interface {
    // Roles in the database system - to allow easy extension
    enum Role {
        STUDENT,
        FACULTY,
        ADMIN
    }
    String[] studentChoices = {
            "Update Profile",
            "Enroll",
            "View Grades",
            "Get CGPA"
    };

    String[] facultyChoices = {
            "Register Course",
            "Deregister Course",
            "View Grades of Specific Student",
            "View Grades of Specific Branch",
            "Update Course Grades"
    };

    String[] adminChoices = {
            "Add Course",
            "Remove Course",
            "View Grade of Specific Student",
            "View Grade of Specific Branch",
            "Generate Transcript"
    };
    public Interface() {}

    public void mainInterface(Database databaseConnection) {
        Scanner keyboardInput = new Scanner(System.in);
        while (true) {
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

            // Check whether the id and password combination exists in the database
            // The role from the enum is extracted and converted to lowercase before passing it to the function
            boolean isValidUser = databaseConnection.authenticateUser(id, password, Role.values()[roleIndex-1].name().toLowerCase());

            // If the id and password entered do not match OR the id does not exist
            if (isValidUser == false) {
                System.out.println("Incorrect id or password");
            }
            else {
                if (roleIndex == 1) {
                    studentInterface(databaseConnection, id);
                } else if (roleIndex == 2) {
                    facultyInterface(databaseConnection, id);
                } else if (roleIndex == 3) {
                    adminInterface(databaseConnection, id);
                }
            }
        }
    }

    public void studentInterface(Database databaseConnection, String id) {
        Student student = new Student(id);
        Scanner keyboardInput = new Scanner(System.in);
        while (true) {
            System.out.println("Select an option");
            for (int i=1; i<=studentChoices.length; i++) {
                System.out.println(i + ". " + studentChoices[i-1]);
            }
            System.out.print("Enter your choice: ");

            // Read the user choice and remove the newline that comes after the integer
            int studentChoice = keyboardInput.nextInt();
            keyboardInput.nextLine();

            if (studentChoice == 1) {
                System.out.print("Enter the new phone number: ");

                // Read the phone number and the newline that follows it
                int newPhoneNumber = keyboardInput.nextInt();
                keyboardInput.nextLine();

                student.updateProfile(newPhoneNumber, databaseConnection);
            }
        }
    }

    public void facultyInterface(Database databaseConnection, String id) {
        Faculty faculty = new Faculty(id);
        while (true) {
            System.out.println("Select an option");
            for (int i=1; i<=facultyChoices.length; i++) {
                System.out.println(i + ". " + facultyChoices[i-1]);
            }
        }
    }

    public void adminInterface(Database databaseConnection, String id) {
        Admin admin = new Admin(id);
        while (true) {
            System.out.println("Select an option");
            for (int i=1; i<=adminChoices.length; i++) {
                System.out.println(i + ". " + adminChoices[i-1]);
            }
        }
    }

}
