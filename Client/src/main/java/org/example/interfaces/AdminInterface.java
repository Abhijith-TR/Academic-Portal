package org.example.interfaces;

import org.example.dal.Database;
import org.example.users.Admin;

import java.util.Scanner;

public class AdminInterface {
    String[] adminChoices = {
            "Add Course",
            "Remove Course",
            "View Grade of Specific Student",
            "View Grade of Specific Branch",
            "Generate Transcript"
    };

    public AdminInterface() {
        Scanner keyboardInput = new Scanner(System.in);
        // Enter the id and password
        System.out.print("Enter your id: ");
        String id = keyboardInput.nextLine();

        System.out.print("Enter your password: ");
        String password = keyboardInput.nextLine();

        Admin admin = new Admin(id);
        while (true) {
            System.out.println("Select an option");
            for (int i=1; i<=adminChoices.length; i++) {
                System.out.println(i + ". " + adminChoices[i-1]);
            }
        }
    }
}
