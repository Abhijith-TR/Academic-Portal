package org.example.interfaces;

import org.example.dal.StudentDAO;
import org.example.users.Student;

import java.util.Scanner;

public class StudentInterface {
    final String[] studentChoices = {
            "Update Profile",
            "Enroll",
            "View Grades",
            "Get CGPA"
    };

    public StudentInterface() {
        StudentDAO databaseConnection = new StudentDAO(
                "jdbc:postgresql://localhost:5432/mini_project",
                "postgres",
                "admin"
        );
        StudentInterfaceHomeScreen(databaseConnection);
    }

    public void StudentInterfaceHomeScreen(StudentDAO databaseConnection) {
        Scanner keyboardInput = new Scanner(System.in);
        // Enter the id and password
        System.out.print("Enter your id: ");
        String id = keyboardInput.nextLine();

        System.out.print("Enter your password: ");
        String password = keyboardInput.nextLine();

        boolean isUserValid = databaseConnection.authenticateUser(id, password, "student");
        if (isUserValid == false) {
            System.out.println("Invalid username or password");
            return;
        }

        Student student = new Student(id);
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
}
