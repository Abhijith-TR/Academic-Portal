package org.example.ui;

import org.example.daoInterfaces.AdminDAO;
import org.example.dal.PostgresAdminDAO;
import org.example.users.Admin;
import org.example.utils.CustomInputStream;
import org.example.utils.Utils;

public class AdminInterface {
    String[] adminChoices = {
            "Add Student",
            "Add Faculty",
            "Add Course",
            "Remove Course",
            "View Grade of Specific Student",
            "View Grade of Specific Course Offering",
            "Generate Transcript"
    };

    public AdminInterface(String connectionURL, String username, String password, String id) {
        AdminDAO adminDAO = new PostgresAdminDAO(
                connectionURL,
                username,
                password
        );
        adminInterfaceHomeScreen(id, adminDAO);
    }

    public void adminInterfaceHomeScreen(String id, AdminDAO adminDAO) {
        CustomInputStream keyboardInput = new CustomInputStream();
        Admin             admin         = new Admin(id, adminDAO);
        while (true) {
            System.out.println();
            System.out.println("Select an option");
            for (int i = 1; i <= adminChoices.length; i++) {
                System.out.println(i + ". " + adminChoices[i - 1]);
            }
            int adminChoice = keyboardInput.integerInput("Enter your choice");
            if (adminChoice == 4) {
                String     courseCode = keyboardInput.stringInput("Enter the course code");
                int        year       = keyboardInput.integerInput("Enter the year");
                int        semester   = keyboardInput.integerInput("Enter the semester");
                String[][] records    = admin.getGradesOfOffering(courseCode, year, semester);
                if (records.length == 0) {
                    System.out.println("No course found with given specifications");
                    continue;
                }
                // You can add a sort function later to sort the entries by the entry number before printing to the screen
                Utils.prettyPrint(new String[]{"Entry Number", "Grade"}, records);
            }
            System.out.println();
        }
    }
}
