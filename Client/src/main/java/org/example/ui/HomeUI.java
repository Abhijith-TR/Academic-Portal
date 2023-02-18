package org.example.ui;

import org.example.dal.PasswordDatabase;
import org.example.daoInterfaces.PasswordDAO;
import org.example.utils.CustomScanner;

public class HomeUI {
    PasswordDAO passwordAuthConnection;
    String      centralConnectionURl = "jdbc:postgresql://localhost:5432/mini_project";
    String      studentUsername      = "postgres";
    String      studentPassword      = "admin";
    String      facultyUsername      = "postgres";
    String      facultyPassword      = "admin";
    String      adminUsername        = "postgres";
    String      adminPassword        = "admin";

    // Roles in the database system - to allow easy extension
    String[] Role = new String[]{
            "student",
            "faculty",
            "admin"
    };

    public HomeUI( String connectionURL, String username, String password ) {
        passwordAuthConnection = new PasswordDatabase( connectionURL, username, password );
    }

    public void mainInterface() {
        CustomScanner keyboardInput = new CustomScanner();
        while ( true ) {
            // Before displaying the prompt to enter the username and password, we have to ensure that no previous user is logged into the database
            passwordAuthConnection.logoutPreviousUser();

            System.out.println();
            System.out.println( "Welcome to the Academic System" );
            System.out.println( "Select your role" );

            // Short-lived iterator variable
            int i = 1;
            // Printing all the roles available in the database
            for ( String role : Role ) {
                System.out.printf( "%d. %s\n", i, role );
                i++;
            }

            // Index of the role in the enum roles
            int roleIndex = keyboardInput.integerInput( "Enter the number corresponding to the role" );

            // If the user has entered an invalid role index
            if ( roleIndex > Role.length || roleIndex <= 0 ) {
                System.out.println( "Invalid Choice" );
                continue;
            }

            // Enter the id and password
            String id       = keyboardInput.stringInput( "Enter your id" );
            String password = keyboardInput.stringInput( "Enter your password" );

            // We verify the username, password and role using the details entered
            boolean isValidUser = passwordAuthConnection.authenticateUser( id, password, Role[roleIndex - 1] );
            // If the verification reveals that the username or password or role is invalid
            if ( !isValidUser ) {
                System.out.println( "Invalid Username or Password" );
                continue;
            }

            // Once the username and password have been verified, pass control over to the corresponding user interface
            if ( roleIndex == 1 ) {
                new StudentUI( centralConnectionURl, studentUsername, studentPassword, id );
            }
            else if ( roleIndex == 2 ) {
                new FacultyUI( centralConnectionURl, facultyUsername, facultyPassword, id );
            }
            else if ( roleIndex == 3 ) {
                new AdminUI( centralConnectionURl, adminUsername, adminPassword, id );
            }

            // Trying to log the logout of the user into the database. Logout is not successful until the log recognizes it
            while ( true ) {
                if ( passwordAuthConnection.logLogoutEntry( id, Role[roleIndex - 1] ) )
                    break;
            }
        }
    }

}
