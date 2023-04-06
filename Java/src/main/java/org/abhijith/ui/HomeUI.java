package org.abhijith.ui;

import org.abhijith.dal.PasswordDAO;
import org.abhijith.utils.CustomScanner;

public class HomeUI {
    private org.abhijith.daoInterfaces.PasswordDAO passwordAuthConnection;

    // Roles in the database system - to allow easy extension
    String[] roles = new String[]{
            "STUDENT",
            "FACULTY",
            "ADMIN",
            "SHUT DOWN"
    };

    public HomeUI() {
        passwordAuthConnection = new PasswordDAO();
    }

    public void setPasswordAuthConnection( org.abhijith.daoInterfaces.PasswordDAO passwordAuthConnection ) {
        this.passwordAuthConnection = passwordAuthConnection;
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
            for ( String role : roles ) {
                System.out.printf( "%d. %s\n", i, role );
                i++;
            }

            // Index of the role in the enum roles
            int roleIndex = keyboardInput.integerInput( "Enter the number corresponding to the role" );

            // If the user has entered an invalid role index
            if ( roleIndex > roles.length || roleIndex <= 0 ) {
                System.out.println( "Invalid Choice" );
                continue;
            }

            // Enter the id and password
            String id       = keyboardInput.stringInput( "Enter your id" );
            String password = keyboardInput.stringInput( "Enter your password" );

            // We verify the username, password and role using the details entered
            String  role        = ( roleIndex == roles.length ) ? roles[2] : roles[roleIndex - 1];
            boolean isValidUser = passwordAuthConnection.authenticateUser( id, password, role );
            // If the verification reveals that the username or password or role is invalid
            if ( !isValidUser ) {
                System.out.println( "Invalid Username or Password" );
                continue;
            }

            // Once the username and password have been verified, pass control over to the corresponding user interface
            if ( roleIndex == 1 ) {
                StudentUI studentUI = new StudentUI( id, keyboardInput );
                studentUI.studentInterfaceHomeScreen();
            }
            else if ( roleIndex == 2 ) {
                FacultyUI facultyUI = new FacultyUI( id, keyboardInput );
                facultyUI.facultyInterfaceHomeScreen();
            }
            else if ( roleIndex == 3 ) {
                AdminUI adminUI = new AdminUI( id, keyboardInput );
                adminUI.adminInterfaceHomeScreen();
            }
            else {
                break;
            }

            // Trying to log the logout of the user into the database. Logout is not successful until the log recognizes it
            while ( true ) {
                if ( passwordAuthConnection.logLogoutEntry( id, role ) )
                    break;
            }
        }
    }

}
