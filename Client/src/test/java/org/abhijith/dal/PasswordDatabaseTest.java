package org.abhijith.dal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.*;

class PasswordDatabaseTest {
    PasswordDatabase passwordDatabase;

    @BeforeEach
    void setUp() {
        passwordDatabase = new PasswordDatabase();
    }

    @Test
    void authenticateUser() {
        String username = "2020CSB1062";
        String password = "iitropar";
        String role     = "STUDENT";

        // False due to invalid input parameters
        assertFalse( passwordDatabase.authenticateUser( null, password, role ) );
        assertFalse( passwordDatabase.authenticateUser( username, null, role ) );
        assertFalse( passwordDatabase.authenticateUser( username, password, null ) );

        // False because the user does not exist
        assertFalse( passwordDatabase.authenticateUser( "random", password, role ) );

        // False because of wrong password
        assertFalse( passwordDatabase.authenticateUser( username, "random", role ) );

        // Successful login
        assertTrue( passwordDatabase.authenticateUser( username, password, role ) );

        try {
            Connection        connection  = passwordDatabase.getDatabaseConnection();
            PreparedStatement deleteQuery = connection.prepareStatement( "DELETE FROM log WHERE log_time IN ( SELECT log_time FROM log ORDER BY log_time DESC LIMIT 1)" );
            deleteQuery.executeUpdate();

            // False because the connection has been closed
            connection.close();
            assertFalse( passwordDatabase.authenticateUser( username, password, role ) );
        } catch ( Exception error ) {
            fail( "Could not delete / close database connection" );
        }
    }

    @Test
    void logLogoutEntry() {
        String id   = "2020CSB1062";
        String role = "STUDENT";

        // False due to invalid input parameters
        assertFalse( passwordDatabase.logLogoutEntry( null, role ) );
        assertFalse( passwordDatabase.logLogoutEntry( id, null ) );

        // Successful
        assertTrue( passwordDatabase.logLogoutEntry( id, role ) );

        try {
            Connection        connection  = passwordDatabase.getDatabaseConnection();
            PreparedStatement deleteQuery = connection.prepareStatement( "DELETE FROM log WHERE log_time IN ( SELECT log_time FROM log ORDER BY log_time DESC LIMIT 1)" );
            deleteQuery.executeUpdate();

            // False because the connection has been closed
            connection.close();
            assertFalse( passwordDatabase.logLogoutEntry( id, role ) );
        } catch ( Exception error ) {
            fail( "Could not delete / close database connection" );
        }
    }

    @Test
    void logoutPreviousUser() {
        String username = "2020CSB1062";
        String password = "iitropar";
        String role     = "STUDENT";
        try {
            // The log table is currently empty and so the previous user is logged out
            assertTrue( passwordDatabase.logoutPreviousUser() );

            // Insert an IN entry into the database
            passwordDatabase.authenticateUser( username, password, role );
            // Now this will insert another entry into the database
            assertTrue( passwordDatabase.logoutPreviousUser() );

            // This will simply verify that the last user who was logged in was already logged out
            assertTrue( passwordDatabase.logoutPreviousUser() );

            // Clears the contents of the log table
            Connection connection = passwordDatabase.getDatabaseConnection();
            PreparedStatement deleteQuery = connection.prepareStatement( "DELETE FROM log" );
            deleteQuery.executeUpdate();

            // Tests the exception
            connection.close();
            assertFalse( passwordDatabase.logoutPreviousUser() );
        } catch ( Exception error ) {
            fail( "Could not insert / delete / close database connection" );
        }
    }
}