package org.example.ui;

import org.example.dal.PostgresStudentDAO;
import org.example.users.Student;

import java.util.Scanner;

public class StudentInterface {
    final String[] studentChoices = {
            "Update Profile",
            "Enroll",
            "Drop",
            "View Grades for Entire Course",
            "View Grades for Particular Session",
            "Get CGPA",
            "View Available Courses"
    };

    public StudentInterface(String connectionURL, String username, String password, String id) {
        PostgresStudentDAO databaseConnection = new PostgresStudentDAO(
                connectionURL,
                username,
                password
        );
        StudentInterfaceHomeScreen(databaseConnection, id);
    }

    public void StudentInterfaceHomeScreen(PostgresStudentDAO databaseConnection, String id) {
        Scanner keyboardInput = new Scanner(System.in);
        Student student = new Student(id, databaseConnection);
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
                boolean status = student.updateProfile(newPhoneNumber);

                if (!status) System.out.println("Profile Update Failed");
                else System.out.println("Profile Updated Successfully");
            }
            else if (studentChoice == 2) {
                System.out.print("Enter the course code: ");

                // Read the course code
                String courseCode = keyboardInput.nextLine();
                // Print the response from the enroll function
                System.out.println(student.enroll(courseCode));
            }
            else if (studentChoice == 3) {
                System.out.print("Enter the course code: ");

                // Read the course code
                String courseCode = keyboardInput.nextLine();
                // Print the response from the drop function
                System.out.println(student.drop(courseCode));
            }
            else if (studentChoice == 4) {
                student.getGrades();
            }
            else if (studentChoice == 5) {
                System.out.print("Enter the year: ");
                int year = keyboardInput.nextInt();
                keyboardInput.nextLine();

                System.out.print("Enter the semester: ");
                int semester = keyboardInput.nextInt();
                keyboardInput.nextLine();

                student.getGrades(year, semester);
            }
            else if (studentChoice == 6) {
                System.out.printf("CGPA: %.2f", student.getCGPA());
            }
            else if (studentChoice == 7) {
                student.getAvailableCourses();
            }
            else {
                break;
            }
            System.out.println();
        }
    }
}
