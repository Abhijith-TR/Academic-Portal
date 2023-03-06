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
        assertEquals( "F", commonDAO.getCourseGrade( entryNumber, "CS888" ) );

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

    @Test
    void getCourseCatalog() {
        String[][] courseCatalog = new String[][]{ { "BM101", "BIOLOGY FOR ENGINEERS", "3.00", "1.00", "0.00", "5.00", "3.0", "{}" },
                { "CP301", "DEVELOPMENT ENGINEERING PRODUCT", "0.00", "0.00", "6.00", "3.00", "3.0", "{}" },
                { "CP302", "CAPSTONE I", "0.00", "0.00", "6.00", "3.00", "3.0", "{}" },
                { "CP303", "CAPSTONE II", "0.00", "0.00", "6.00", "3.00", "3.0", "{CP302}" },
                { "CS101", "DISCRETE MATHEMATICAL STRUCTURES", "3.00", "1.00", "0.00", "5.00", "3.0", "{}" },
                { "CS201", "DATA STRUCTURES", "3.00", "1.00", "2.00", "6.00", "4.0", "{GE103}" },
                { "CS202", "PROGRAMMING PARADIGMS AND PRAGMATICS", "3.00", "1.00", "2.00", "6.00", "4.0", "{CS201}" },
                { "CS203", "DIGITAL LOGIC DESIGN", "3.00", "1.00", "3.00", "6.00", "4.0", "{}" },
                { "CS204", "COMPUTER ARCHITECTURE", "3.00", "1.00", "2.00", "6.00", "4.0", "{CS203,GE103}" },
                { "CS301", "DATABASES", "3.00", "1.00", "2.00", "6.00", "4.0", "{CS201}" },
                { "CS302", "ANALYSIS AND DESIGN OF ALGORITHMS", "3.00", "1.00", "0.00", "5.00", "3.0", "{CS101,CS201}" },
                { "CS303", "OPERATING SYSTEMS", "3.00", "1.00", "2.00", "6.00", "4.0", "{CS201}" },
                { "CS304", "COMPUTER NETWORKS", "3.00", "1.00", "2.00", "6.00", "4.0", "{CS201}" },
                { "CS305", "SOFTWARE ENGINEERING", "3.00", "1.00", "2.00", "6.00", "4.0", "{CS301,CS303}" },
                { "CS306", "THEORY OF COMPUTATION", "3.00", "1.00", "0.00", "5.00", "3.0", "{CS101}" },
                { "CS517", "DIGITAL IMAGE PROCESSING AND ANALYSIS", "2.00", "1.00", "2.00", "4.00", "3.0", "{}" },
                { "CS522", "SOCIAL COMPUTING AND NETWORKS", "2.00", "0.00", "2.00", "5.00", "3.0", "{}" },
                { "CS535", "INTRODUCTION TO GAME THEORY AND MECHANISM DESIGN", "3.00", "0.00", "0.00", "6.00", "3.0", "{}" },
                { "CS539", "INTERNET OF THINGS", "3.00", "0.00", "0.00", "6.00", "3.0", "{}" },
                { "CS550", "RESEARCH METHODOLOGIES IN COMPUTER SCIENCE", "1.00", "0.00", "0.00", "2.00", "1.0", "{}" },
                { "CS999", "TEST COURSE", "3.00", "0.00", "0.00", "6.00", "25.0", "{}" },
                { "CY101", "CHEMISTRY FOR ENGINEERS", "3.00", "1.00", "2.00", "6.00", "4.0", "{}" },
                { "EE201", "SIGNALS AND SYSTEMS", "3.00", "1.00", "0.00", "5.00", "3.0", "{}" },
                { "GE101", "TECHNOLOGY MUSEUM LAB", "0.00", "0.00", "2.00", "1.00", "1.0", "{}" },
                { "GE102", "WORKSHOP PRACTICE", "0.00", "0.00", "4.00", "2.00", "2.0", "{}" },
                { "GE103", "INTRODUCTION TO COMPUTING AND DATA STRUCTURES", "3.00", "0.00", "3.00", "7.00", "4.5", "{}" },
                { "GE104", "INTRODUCTION TO ELECTRICAL ENGINEERING", "2.00", "0.00", "2.00", "4.00", "3.0", "{}" },
                { "GE105", "ENGINEERING DRAWING", "0.00", "0.00", "3.00", "1.50", "1.5", "{}" },
                { "GE107", "TINKERING LAB", "0.00", "0.00", "3.00", "1.00", "1.5", "{}" },
                { "GE108", "BASIC ELECTRONICS", "2.00", "0.00", "2.00", "4.00", "3.0", "{}" },
                { "GE109", "INTRODUCTION TO ENGINEERING PRODUCTS", "0.00", "0.00", "2.00", "1.00", "1.0", "{}" },
                { "GE111", "INTRODUCTION TO ENVIRONMENTAL SCIENCE AND ENGINEERING", "3.00", "1.00", "0.00", "5.00", "3.0", "{}" },
                { "HS101", "HISTORY OF TECHNOLOGY", "1.50", "0.50", "0.00", "2.50", "1.5", "{}" },
                { "HS103", "PROFESSIONAL ENGLISH COMMUNICATION", "2.00", "0.00", "2.00", "5.00", "3.0", "{}" },
                { "HS104", "PROFESSIONAL ETHICS", "1.00", "0.00", "1.00", "2.00", "1.5", "{}" },
                { "HS201", "ECONOMICS", "3.00", "1.00", "0.00", "5.00", "3.0", "{}" },
                { "HS202", "HUMAN GEOGRAPHY AND SOCIAL NEEDS", "1.00", "0.00", "4.00", "3.00", "3.0", "{}" },
                { "HS301", "INDUSTRIAL MANAGEMENT", "3.00", "1.00", "0.00", "5.00", "3.0", "{}" },
                { "HS475", "AN INTRODUCTION TO FANTASY AND SCIENCE FICTION", "3.00", "0.00", "0.00", "6.00", "3.0", "{}" },
                { "HS507", "POSITIVE PSYCHOLOGY AND WELL-BEING", "3.00", "0.00", "0.00", "6.00", "3.0", "{}" },
                { "II301", "INDUSTRIAL INTERNSHIP AND COMPREHENSIVE VIVA", "0.00", "0.00", "7.00", "3.50", "3.5", "{}" },
                { "MA101", "CALCULUS", "3.00", "1.00", "0.00", "5.00", "3.0", "{}" },
                { "MA102", "LINEAR ALGEBRA, INTEGRAL TRANSFORMS AND SPECIAL FUNCTIONS", "3.00", "1.00", "0.00", "5.00", "3.0", "{}" },
                { "MA201", "DIFFERENTIAL EQUATIONS", "3.00", "1.00", "0.00", "5.00", "3.0", "{}" },
                { "MA202", "PROBABILITY AND STATISTICS", "3.00", "0.00", "0.00", "6.00", "3.0", "{}" },
                { "MA515", "FOUNDATIONS OF DATA SCIENCE", "3.00", "0.00", "2.00", "7.00", "4.0", "{}" },
                { "MA628", "FINANCIAL DERIVATIVES PRICING", "3.00", "0.00", "2.00", "7.00", "4.0", "{}" },
                { "NS101", "NSS I", "0.00", "0.00", "2.00", "1.00", "1.0", "{}" },
                { "NS102", "NSS II", "0.00", "0.00", "2.00", "1.00", "1.0", "{}" },
                { "NS103", "NSS III", "0.00", "0.00", "2.00", "1.00", "1.0", "{}" },
                { "NS104", "NSS IV", "0.00", "0.00", "2.00", "1.00", "1.0", "{}" },
                { "PH101", "PHYSICS FOR ENGINEERS", "3.00", "1.00", "0.00", "5.00", "3.0", "{}" },
                { "PH102", "PHYSICS FOR ENGINEERS LAB", "0.00", "0.00", "4.00", "2.00", "2.0", "{}" } };

        // Expected result
        assertArrayEquals( courseCatalog, commonDAO.getCourseCatalog() );

        try {
            Connection connection = commonDAO.getDatabaseConnection();
            connection.close();
            assertArrayEquals( new String[][]{}, commonDAO.getCourseCatalog() );
        } catch ( Exception error ) {
            fail( "Could not close database connection" );
        }
    }
}