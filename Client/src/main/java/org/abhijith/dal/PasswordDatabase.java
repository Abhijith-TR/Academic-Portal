package org.abhijith.dal;

import org.abhijith.daoInterfaces.PasswordDAO;

import java.io.FileInputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Properties;

public class PasswordDatabase implements PasswordDAO {
    private Connection databaseConnection;

    public PasswordDatabase() {
        try {
            Properties databaseConfig = new Properties();
            databaseConfig.load( new FileInputStream( "./src/main/resources/config.properties" ) );

            String connectionURL = databaseConfig.getProperty( "connectionURL" );
            String username      = databaseConfig.getProperty( "username" );
            String password      = databaseConfig.getProperty( "password" );
            databaseConnection = DriverManager.getConnection(
                    connectionURL,
                    username,
                    password
            );
        } catch ( Exception error ) {
            System.err.println( "Configuration Error. Could not connect to database. Shutting down." );
            System.exit( 0 );
        }
    }

    public boolean authenticateUser( String id, String password, String role ) {
        try {
            // Create a query to check whether there exists a user with this role and this id in the database
            PreparedStatement authenticationQuery = databaseConnection.prepareStatement( "SELECT password FROM common_user_details WHERE id = ? AND role = ?" );
            authenticationQuery.setString( 1, id );
            authenticationQuery.setString( 2, role );
            ResultSet userDetails = authenticationQuery.executeQuery();

            // If a row is returned, it means that there is such a user
            if ( userDetails.next() ) {
                // We get the password from the database and check whether this password matches the one that was given
                String  passwordInDatabase = userDetails.getString( 1 );
                boolean passwordIsCorrect  = password.equals( passwordInDatabase );

                // If the password matches the one that was given
                if ( passwordIsCorrect ) {
                    // Create a query to insert a log into the database
                    PreparedStatement insertLogQuery = databaseConnection.prepareStatement( "INSERT INTO log VALUES(?, ?, ?, ?)" );
                    insertLogQuery.setString( 1, id );
                    insertLogQuery.setString( 2, role );
                    insertLogQuery.setObject( 3, LocalDateTime.now() );
                    insertLogQuery.setString( 4, "IN" );
                    int insertLogQueryResult = insertLogQuery.executeUpdate();

                    // Returns to indicate that the log insertion was successful
                    // If the log insertion is unsuccessful, the user is not allowed to enter the application
                    return insertLogQueryResult != 0;
                }
            }
        } catch ( SQLException error ) {
            System.err.println( "Database Error. Login failed" );
            return false;
        }
        return false;
    }

    public boolean logLogoutEntry( String id, String role ) {
        try {
            // Create a query to insert the out entry of the user into the log table
            PreparedStatement logoutQuery = databaseConnection.prepareStatement( "INSERT INTO log VALUES (?, ?, ?, ?)" );
            logoutQuery.setString( 1, id );
            logoutQuery.setString( 2, role );
            logoutQuery.setObject( 3, LocalDateTime.now() );
            logoutQuery.setString( 4, "OUT" );
            int logoutQueryResult = logoutQuery.executeUpdate();

            // If the log entry insertion is unsuccessful, the user is not allowed to log out of the application
            return logoutQueryResult != 0;
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please wait" );
            return false;
        }
    }

    @Override
    public boolean logoutPreviousUser() {
        try {
            // Get the last entry from the log
            PreparedStatement getLastLog       = databaseConnection.prepareStatement( "SELECT id, role, in_or_out FROM log ORDER BY log_time DESC LIMIT 1" );
            ResultSet         getLastLogResult = getLastLog.executeQuery();

            // Now check whether the in_or_out column is in or out
            // If the column is in, it means that the last user has not been logged out yet, and we have to log the user out
            // The only case in which we won't find an entry is if this user is the first entry into the database
            boolean isFirstEntry = getLastLogResult.next();
            if ( !isFirstEntry ) return true;

            String id        = getLastLogResult.getString( 1 );
            String role      = getLastLogResult.getString( 2 );
            String in_or_out = getLastLogResult.getString( 3 );

            if ( in_or_out.equals( "in" ) ) {
                // Now we have to insert a new log into the database which says that the user has logged out
                PreparedStatement insertOutLog = databaseConnection.prepareStatement( "INSERT INTO log VALUES ( ?, ?, ?, ?)" );
                insertOutLog.setString( 1, id );
                insertOutLog.setString( 2, role );
                insertOutLog.setObject( 3, LocalDateTime.now() );
                insertOutLog.setString( 4, "out" );

                // Returns true if the out entry was inserted successfully, returns false otherwise
                return insertOutLog.executeUpdate() == 1;
            }
            return true;
        } catch ( Exception error ) {
            System.out.println( "Database Error. Previous log not found" );
            return false;
        }
    }
}
