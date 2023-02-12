package org.example.dal;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

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
                boolean passwordIsCorrect = password.equals(passwordInDatabase);
                if (passwordIsCorrect) {
                    PreparedStatement insertLogQuery = databaseConnection.prepareStatement("INSERT INTO log VALUES(?, ?, ?, ?)");
                    insertLogQuery.setString(1, id);
                    insertLogQuery.setString(2, role);
                    insertLogQuery.setObject(3, LocalDateTime.now());
                    insertLogQuery.setString(4, "in");
                    int insertLogQueryResult = insertLogQuery.executeUpdate();
                    return insertLogQueryResult != 0;
                }
            }
        } catch (SQLException error) {
            System.err.println("Database Error. Login failed");
            return false;
        }
        return false;
    }

    public boolean logLogoutEntry(String id, String role) {
        try {
            PreparedStatement logoutQuery = databaseConnection.prepareStatement("INSERT INTO log VALUES (?, ?, ?, ?)");
            logoutQuery.setString(1, id);
            logoutQuery.setString(2, role);
            logoutQuery.setObject(3, LocalDateTime.now());
            logoutQuery.setString(4, "out");
            int logoutQueryResult = logoutQuery.executeUpdate();
            return logoutQueryResult != 0;
        } catch (Exception error) {
            System.out.println("Database Error. Please wait");
            return false;
        }
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
