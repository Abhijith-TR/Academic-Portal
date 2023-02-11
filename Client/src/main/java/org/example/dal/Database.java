package org.example.dal;

import java.sql.*;

public class Database {
    protected Connection databaseConnection;
    public Database(String connectionURL, String username, String password) {
        try {
            databaseConnection = DriverManager.getConnection(
                    connectionURL,
                    username,
                    password
            );
        } catch (Exception error) {
            System.err.println("Could not connect to database. Shutting down.");
            System.exit(0);
        }
    }

    public boolean authenticateUser(String id, String password, String role) {
        try {
            PreparedStatement authenticationQuery = databaseConnection.prepareStatement("SELECT password FROM user_login_details WHERE id = ? AND role = ?");
            authenticationQuery.setString(1, id);
            authenticationQuery.setString(2, role);
            ResultSet userDetails = authenticationQuery.executeQuery();
            if ( userDetails.next() ) {
                String passwordInDatabase = userDetails.getString(1);
                return password.equals(passwordInDatabase);
            }
        } catch (SQLException error) {
            System.err.println("No response from database. Service shutting down");
            System.exit(0);
        }
        return false;
    }

    public boolean updatePhoneNumber(String role, int newPhoneNumber) {
        try {
            PreparedStatement updateQuery = databaseConnection.prepareStatement("UPDATE phone");
        } catch (SQLException error) {
            System.out.println(error.getMessage());
        }
        return true;
    }
}
