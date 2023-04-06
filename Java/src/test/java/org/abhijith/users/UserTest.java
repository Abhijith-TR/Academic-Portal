package org.abhijith.users;

import org.abhijith.dal.PostgresCommonDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UserTest {
    String            userID      = "2020CSB1062";
    String            phoneNumber = "99999";
    String            email       = "random@gmail.com";
    User              user        = new User( userID );
    PostgresCommonDAO commonDAO;

    @BeforeEach
    void setUp() {
        commonDAO = Mockito.mock( PostgresCommonDAO.class );
        user.setCommonDAO( commonDAO );
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getID() {
        assertEquals( userID, user.getID() );
    }

    @Test
    void setPhoneNumber() {
        // Returns false when the DAO returns false
        when( commonDAO.setPhoneNumber( userID, phoneNumber ) ).thenReturn( false );
        assertFalse( user.setPhoneNumber( phoneNumber ) );

        // True when the DAO returns true
        when( commonDAO.setPhoneNumber( userID, phoneNumber ) ).thenReturn( true );
        assertTrue( user.setPhoneNumber( phoneNumber ) );
    }

    @Test
    void setEmail() {
        // Returns false when the DAO returns false
        when( commonDAO.setEmail( userID, email ) ).thenReturn( false );
        assertFalse( user.setEmail( email ) );

        // True when the DAO returns true
        when( commonDAO.setEmail( userID, email ) ).thenReturn( true );
        assertTrue( user.setEmail( email ) );
    }

    @Test
    void getContactDetails() {
        assertArrayEquals( new String[]{}, user.getContactDetails( null ) );

        // Returns whatever the DAO returns
        when( commonDAO.getContactDetails( userID ) ).thenReturn( new String[]{} );
        assertArrayEquals( new String[]{}, user.getContactDetails( userID ) );

        when( commonDAO.getContactDetails( userID ) ).thenReturn( new String[]{ phoneNumber, "" } );
        assertArrayEquals( new String[]{ phoneNumber, "" }, user.getContactDetails( userID ) );
    }

    @Test
    void setPassword() {
        // Returns false when the DAO returns false
        when( commonDAO.setPassword( userID, "" ) ).thenReturn( false );
        assertFalse( user.setPassword( "" ) );

        // True when the DAO returns true
        when( commonDAO.setPassword( userID, "" ) ).thenReturn( true );
        assertTrue( user.setPassword( "" ) );
    }

    @Test
    void getCourseCatalog() {
        // Returns whatever the DAO returns
        when( commonDAO.getCourseCatalog() ).thenReturn( new String[][]{} );
        assertArrayEquals( new String[][]{}, user.getCourseCatalog() );

        when( commonDAO.getCourseCatalog() ).thenReturn( new String[][]{ { "CS101", "DISCRETE MATHEMATICS" } } );
        assertArrayEquals( new String[][]{ { "CS101", "DISCRETE MATHEMATICS" } }, user.getCourseCatalog() );
    }
}