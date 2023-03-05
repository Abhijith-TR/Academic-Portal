package org.abhijith.dal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class PostgresCommonDAOTest {
    PostgresCommonDAO commonDAO;

    @BeforeEach
    void setUp() {
        commonDAO = new PostgresCommonDAO();
    }


    @Test
    void getCurrentAcademicSession() {
        // Returns the actual current year and semester
        assertArrayEquals( new int[]{ 2023, 2 }, commonDAO.getCurrentAcademicSession() );

        // Returns the default when the connection is not available
        try {
            Connection connection = commonDAO.getDatabaseConnection();
            connection.close();
            assertArrayEquals( new int[]{ 2020, 1 }, commonDAO.getCurrentAcademicSession() );
        } catch ( Exception error ) {
            fail( "Could not close database connection" );
        }
    }

    @Test
    void getStudentGradesForSemester() {
        String entryNumber = "2020CSB1062";
        int    year        = 2023;
        int    semester    = 2;
        String[][] records = new String[][]{
                { "CP303", "CAPSTONE II", "A", "3.0" },
                { "CS539", "INTERNET OF THINGS", "A", "3.0" },
                { "CS550", "RESEARCH METHODOLOGIES IN COMPUTER SCIENCE", "A", "1.0" },
                { "CS999", "TEST COURSE", "F", "25.0" },
                { "MA628", "FINANCIAL DERIVATIVES PRICING", "A", "4.0" },
                { "NS104", "NSS IV", "A", "1.0" }
        };

        // [][] due to invalid input parameters
        assertArrayEquals( new String[][]{}, commonDAO.getStudentGradesForSemester( null, year, semester ) );
        assertArrayEquals( new String[][]{}, commonDAO.getStudentGradesForSemester( entryNumber, -1, semester ) );
        assertArrayEquals( new String[][]{}, commonDAO.getStudentGradesForSemester( entryNumber, year, -1 ) );

        // Expected result
        assertArrayEquals( records, commonDAO.getStudentGradesForSemester( entryNumber, year, semester ) );

        try {
            Connection connection = commonDAO.getDatabaseConnection();
            connection.close();
            assertArrayEquals( new String[][]{}, commonDAO.getStudentGradesForSemester( entryNumber, year, semester ) );
        } catch ( Exception error ) {
            fail( "Could not close database connection" );
        }
    }

    @Test
    void getBatch() {
        String entryNumber = "2020CSB1062";

        // -1 due to invalid input parameters
        assertEquals( -1, commonDAO.getBatch( null ) );

        // -1 due to missing student
        assertEquals( -1, commonDAO.getBatch( "random" ) );

        // Expected batch of the student
        assertEquals( 2020, commonDAO.getBatch( entryNumber ) );

        try {
            Connection connection = commonDAO.getDatabaseConnection();
            connection.close();
            assertEquals( -1, commonDAO.getBatch( entryNumber ) );
        } catch ( Exception error ) {
            fail( "Could not close database connection" );
        }
    }

    @Test
    void checkCourseCatalog() {
        // False due to invalid input parameters
        assertFalse( commonDAO.checkCourseCatalog( null ) );

        // Course is not present in the catalog
        assertFalse( commonDAO.checkCourseCatalog( "CS888" ) );

        // Course is present in the catalog
        assertTrue( commonDAO.checkCourseCatalog( "CS101" ) );

        try {
            Connection connection = commonDAO.getDatabaseConnection();
            connection.close();
            assertFalse( commonDAO.checkCourseCatalog( "CS101" ) );
        } catch ( Exception error ) {
            fail( "Could not close database connection" );
        }
    }

    @Test
    void getUGCurriculum() {
        int                     batch      = 2020;
        HashMap<String, Double> curriculum = new HashMap<>();
        curriculum.put( "SC", 24.00 );
        curriculum.put( "SE", 6.00 );
        curriculum.put( "GR", 23.50 );
        curriculum.put( "PC", 36.00 );
        curriculum.put( "PE", 12.00 );
        curriculum.put( "HC", 15.00 );
        curriculum.put( "HE", 6.00 );
        curriculum.put( "CP", 9.00 );
        curriculum.put( "II", 3.50 );
        curriculum.put( "NN", 4.00 );
        curriculum.put( "OE", 6.00 );

        // Empty hashmap due to invalid input parameters
        assertEquals( null, commonDAO.getUGCurriculum( -1 ) );

        // Expected value
        assertTrue( curriculum.equals( commonDAO.getUGCurriculum( batch ) ) );
        try {
            Connection connection = commonDAO.getDatabaseConnection();
            connection.close();
            assertEquals( null, commonDAO.getUGCurriculum( batch ) );
        } catch ( Exception error ) {
            fail( "Could not close database connection" );
        }
    }

    @Test
    void getCreditsInAllCategories() {
        String                  entryNumber = "2020CSB1062";
        HashMap<String, Double> curriculum  = new HashMap<>();
        curriculum.put( "SC", 24.0 );
        curriculum.put( "SE", 8.0 );
        curriculum.put( "GR", 23.5 );
        curriculum.put( "PC", 41.0 );
        curriculum.put( "PE", 13.0 );
        curriculum.put( "HC", 15.0 );
        curriculum.put( "HE", 3.0 );
        curriculum.put( "CP", 9.0 );
        curriculum.put( "II", 3.5 );
        curriculum.put( "NN", 4.0 );

        // Null due to invalid input parameters
        assertNull( commonDAO.getCreditsInAllCategories( null ) );

        // Expected value
        assertTrue( curriculum.equals( commonDAO.getCreditsInAllCategories( entryNumber ) ) );

        try {
            Connection connection = commonDAO.getDatabaseConnection();
            connection.close();
            assertNull( commonDAO.getCreditsInAllCategories( entryNumber ) );
        } catch ( Exception error ) {
            fail( "Could not close database connection" );
        }
    }

    @Test
    void setPhoneNumber() {
        String id    = "2020CSB1062";
        String phone = "99999";

        // False due to invalid input parameters
        assertFalse( commonDAO.setPhoneNumber( id, null ) );
        assertFalse( commonDAO.setPhoneNumber( null, phone ) );

        // True because the number was set
        assertTrue( commonDAO.setPhoneNumber( id, phone ) );

        try {
            Connection connection = commonDAO.getDatabaseConnection();
            connection.close();
            assertFalse( commonDAO.setPhoneNumber( id, phone ) );
        } catch ( Exception error ) {
            fail( "Could not close database connection" );
        }
    }

    @Test
    void setEmail() {
        String id    = "2020CSB1062";
        String email = "random@gmail.com";

        // False due to invalid input parameters
        assertFalse( commonDAO.setEmail( null, email ) );
        assertFalse( commonDAO.setEmail( id, null ) );

        // True as the email is set
        assertTrue( commonDAO.setEmail( id, email ) );

        try {
            Connection connection = commonDAO.getDatabaseConnection();
            connection.close();
            assertFalse( commonDAO.setEmail( id, email ) );
        } catch ( Exception error ) {
            fail( "Could not close database connection" );
        }
    }

    @Test
    void getContactDetails() {
        String id    = "2020CSB1062";
        String email = "random@gmail.com";
        String phone = "99999";

        // [][] due to invalid input parameters
        assertArrayEquals( new String[]{}, commonDAO.getContactDetails( null ) );

        // [][] due to missing id
        assertArrayEquals( new String[]{}, commonDAO.getContactDetails( "random" ) );

        // Expected value
        assertArrayEquals( new String[]{ email, phone }, commonDAO.getContactDetails( id ) );

        try {
            Connection connection = commonDAO.getDatabaseConnection();
            connection.close();
            assertArrayEquals( new String[]{}, commonDAO.getContactDetails( id ) );
        } catch ( Exception error ) {
            fail( "Could not close database connection" );
        }
    }

    @Test
    void setPassword() {
        String id       = "2020CSB1062";
        String password = "iitropar";

        // False due to invalid input parameters
        assertFalse( commonDAO.setPassword( null, password ) );
        assertFalse( commonDAO.setPassword( id, null ) );

        // True as the password is set
        assertTrue( commonDAO.setPassword( id, password ) );

        try {
            Connection connection = commonDAO.getDatabaseConnection();
            connection.close();
            assertFalse( commonDAO.setPassword( id, password ) );
        } catch ( Exception error ) {
            fail( "Could not close database connection" );
        }
    }

    @Test
    void getCourseGrade() {
        String entryNumber = "2020CSB1062";
        String courseCode  = "CS101";

        // "" due to invalid input parameters
        assertEquals( "", commonDAO.getCourseGrade( null, courseCode ) );
        assertEquals( "", commonDAO.getCourseGrade( entryNumber, null ) );

        // Returns the actual grade that was expected
        assertEquals( "A", commonDAO.getCourseGrade( entryNumber, courseCode ) );

        // No such record exists in the database
        assertEquals( "-", commonDAO.getCourseGrade( entryNumber, "CS888" ) );

        try {
            Connection connection = commonDAO.getDatabaseConnection();
            connection.close();
            assertEquals( "", commonDAO.getCourseGrade( entryNumber, courseCode ) );
        } catch ( Exception error ) {
            fail( "Could not close database connection" );
        }
    }

    @Test
    void getStudentDepartment() {
        String entryNumber = "2020CSB1062";

        // "" because of invalid input arguments
        assertEquals( "", commonDAO.getStudentDepartment( null ) );

        // Gets the correct student department
        assertEquals( "CS", commonDAO.getStudentDepartment( entryNumber ) );

        // Student does not exist. Returns ""
        assertEquals( "", commonDAO.getStudentDepartment( "random" ) );

        try {
            Connection connection = commonDAO.getDatabaseConnection();
            connection.close();
            assertEquals( "", commonDAO.getStudentDepartment( entryNumber ) );
        } catch ( Exception error ) {
            fail( "Could not close database connection" );
        }
    }
}