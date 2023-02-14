package org.example.ui;

import org.example.users.Faculty;

import java.util.Scanner;

public class FacultyUI {
    String[] facultyChoices = {
            "Register Course",
            "Deregister Course",
            "View Grades of Specific Student",
            "View Grades of Specific Branch",
            "Update Course Grades"
    };

    public FacultyUI() {
        Scanner keyboardInput = new Scanner(System.in);
        // Enter the id and password
        System.out.print("Enter your id: ");
        String id = keyboardInput.nextLine();

        System.out.print("Enter your password: ");
        String password = keyboardInput.nextLine();

        Faculty faculty = new Faculty(id);
        while (true) {
            System.out.println("Select an option");
            for (int i=1; i<=facultyChoices.length; i++) {
                System.out.println(i + ". " + facultyChoices[i-1]);
            }
        }
    }
}
