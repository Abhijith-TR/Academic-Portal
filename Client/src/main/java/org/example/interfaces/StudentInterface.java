package org.example.interfaces;

import org.example.dal.StudentDAO;
import org.example.users.Student;

import java.util.Scanner;

public class StudentInterface {
    final String[] studentChoices = {
            "Update Profile",
            "Enroll",
            "Drop",
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
        if (!isUserValid) {
            System.out.println("Invalid username or password");
            return;
        }

        Student student = new Student(id);
        while (true) {
            System.out.println();
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
                boolean status = student.updateProfile(newPhoneNumber, databaseConnection);

                if (!status) System.out.println("Profile Update Failed");
                else System.out.println("Profile Updated Successfully");
            }
            else if (studentChoice == 2) {
                System.out.print("Enter the course code: ");

                // Read the course code
                String courseCode = keyboardInput.nextLine();
                // Print the response from the enroll function
                System.out.println(student.enroll(courseCode, databaseConnection));
            }
            else if (studentChoice == 3) {
                System.out.print("Enter the course code: ");

                // Read the course code
                String courseCode = keyboardInput.nextLine();
                // Print the response from the drop function
                System.out.println(student.drop(courseCode, databaseConnection));
            }
            else {
                break;
            }
            System.out.println();
        }
    }
}
