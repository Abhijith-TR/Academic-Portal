package org.abhijith.dal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.*;

class PasswordDAOTest {
    PasswordDAO passwordDAO;

    @BeforeEach
    void setUp() {
        passwordDAO = new PasswordDAO();
    }

    @Test
    void authenticateUser() {
        String username = "2020CSB1062";
        String password = "iitropar";
        String role     = "STUDENT";

        // False due to invalid input parameters
        assertFalse( passwordDAO.authenticateUser( null, password, role ) );
        assertFalse( passwordDAO.authenticateUser( username, null, role ) );
        assertFalse( passwordDAO.authenticateUser( username, password, null ) );

        // False because the user does not exist
        assertFalse( passwordDAO.authenticateUser( "random", password, role ) );

        // False because of wrong password
        assertFalse( passwordDAO.authenticateUser( username, "random", role ) );

        // Successful login
        assertTrue( passwordDAO.authenticateUser( username, password, role ) );

        try {
            Connection        connection  = passwordDAO.getDatabaseConnection();
            PreparedStatement deleteQuery = connection.prepareStatement( "DELETE FROM log WHERE log_time IN ( SELECT log_time FROM log ORDER BY log_time DESC LIMIT 1)" );
            deleteQuery.executeUpdate();

            // False because the connection has been closed
            connection.close();
            assertFalse( passwordDAO.authenticateUser( username, password, role ) );
        } catch ( Exception error ) {
            fail( "Could not delete / close database connection" );
        }
    }

    @Test
    void logLogoutEntry() {
        String id   = "2020CSB1062";
        String role = "STUDENT";

        // False due to invalid input parameters
        assertFalse( passwordDAO.logLogoutEntry( null, role ) );
        assertFalse( passwordDAO.logLogoutEntry( id, null ) );

        // Successful
        assertTrue( passwordDAO.logLogoutEntry( id, role ) );

        try {
            Connection        connection  = passwordDAO.getDatabaseConnection();
            PreparedStatement deleteQuery = connection.prepareStatement( "DELETE FROM log WHERE log_time IN ( SELECT log_time FROM log ORDER BY log_time DESC LIMIT 1)" );
            deleteQuery.executeUpdate();

            // False because the connection has been closed
            connection.close();
            assertFalse( passwordDAO.logLogoutEntry( id, role ) );
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
            assertTrue( passwordDAO.logoutPreviousUser() );

            // Insert an IN entry into the database
            passwordDAO.authenticateUser( username, password, role );
            // Now this will insert another entry into the database
            assertTrue( passwordDAO.logoutPreviousUser() );

            // This will simply verify that the last user who was logged in was already logged out
            assertTrue( passwordDAO.logoutPreviousUser() );

            // Clears the contents of the log table
            Connection connection = passwordDAO.getDatabaseConnection();
            PreparedStatement deleteQuery = connection.prepareStatement( "DELETE FROM log" );
            deleteQuery.executeUpdate();

            // Tests the exception
            connection.close();
            assertFalse( passwordDAO.logoutPreviousUser() );
        } catch ( Exception error ) {
            fail( "Could not insert / delete / close database connection" );
        }
    }
}