package org.example.database;

import java.sql.*;

public class Database {
    private Connection databaseConnection;
    public Database() {
        try {
            databaseConnection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/mini_project",
                    "postgres",
                    "admin"
            );
            System.out.println("Database connection established");
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
//                System.out.println( password + " " + passwordInDatabase );
                return password.equals(passwordInDatabase);
            }
        } catch (SQLException e) {
            System.err.println("No response from database. Service shutting down");
            System.exit(0);
        }
        return false;
    }

    public boolean updatePhoneNumber(String role, int newPhoneNumber) {
        try {
            PreparedStatement updateQuery = databaseConnection.prepareStatement("UPDATE phone");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return true;
    }
}
