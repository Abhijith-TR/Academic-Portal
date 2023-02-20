package org.abhijith.dal;

import org.abhijith.daoInterfaces.AdminDAO;
import org.abhijith.daoInterfaces.FacultyDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class PostgresStudentDAOTest {
    PostgresStudentDAO postgresStudentDAO;
    AdminDAO           adminDAO   = new PostgresAdminDAO(
            "jdbc:postgresql://localhost:5432/mini_project",
            "postgres",
            "admin"
    );
    FacultyDAO         facultyDAO = new PostgresFacultyDAO(
            "jdbc:postgresql://localhost:5432/mini_project",
            "postgres",
            "admin"
    );

    @BeforeEach
    void setUp() {
        postgresStudentDAO = new PostgresStudentDAO(
                "jdbc:postgresql://localhost:5432/mini_project",
                "postgres",
                "admin"
        );
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void checkCourseOffering() {
        // Successful because course exists from this department in this particular session
        assertTrue( postgresStudentDAO.checkCourseOffering( "HS507", 2023, 2, "HS" ) );

        // Fails because this course does not exist in this particular year and semester
        assertFalse( postgresStudentDAO.checkCourseOffering( "CS101", 2022, 2, "CS" ) );

        // Fails because of invalid year and semester and strings being null
        assertFalse( postgresStudentDAO.checkCourseOffering( "HS507", 0, 2, "HS" ) );
        assertFalse( postgresStudentDAO.checkCourseOffering( "HS507", 2022, 0, "HS" ) );
        assertFalse( postgresStudentDAO.checkCourseOffering( null, 2023, 2, "HS" ) );
        assertFalse( postgresStudentDAO.checkCourseOffering( "HS507", 2023, 2, null ) );


        try {
            Connection conn = postgresStudentDAO.getDatabaseConnection();
            conn.close();
            assertFalse( postgresStudentDAO.checkCourseOffering( "HS507", 2023, 2, "HS" ) );
        } catch ( Exception error ) {
            System.out.println( error.getMessage() );
            fail( "Database connection could not be closed" );
        }
    }

    @Test
    void checkStudentPassStatus() {
        // Successful because this particular student has done and passed this course before
        assertTrue( postgresStudentDAO.checkStudentPassStatus( "CS101", 8, "2020CSB1062" ) );

        // False because the student has failed in the course
        assertFalse( postgresStudentDAO.checkStudentPassStatus( "CS999", 8, "2020CSB1062" ) );

        // False because no such course exists
        assertFalse( postgresStudentDAO.checkStudentPassStatus( "ZZ123", 8, "2020CSB1062" ) );

        // False because of invalid input parameters
        assertFalse( postgresStudentDAO.checkStudentPassStatus( null, 8, "2020CSB1062" ) );
        assertFalse( postgresStudentDAO.checkStudentPassStatus( "ZZ123", 11, "2020CSB1062" ) );
        assertFalse( postgresStudentDAO.checkStudentPassStatus( "ZZ123", -1, "2020CSB1062" ) );
        assertFalse( postgresStudentDAO.checkStudentPassStatus( "ZZ123", 8, null ) );

        try {
            Connection conn = postgresStudentDAO.getDatabaseConnection();
            conn.close();
            assertFalse( postgresStudentDAO.checkStudentPassStatus( "CS555", 8, "2020CSB1062" ) );
        } catch ( Exception error ) {
            fail( "Database connection could not be closed" );
        }
    }

    @Test
    void getCourseCatalogPrerequisites() {
        // Null because such a course does not exist
        assertNull( postgresStudentDAO.getCourseCatalogPrerequisites( "CS777" ) );

        // Prerequisites exist
        assertArrayEquals( new String[]{ "CS201" }, postgresStudentDAO.getCourseCatalogPrerequisites( "CS202" ) );

        // False because of invalid input parameters
        assertNull( postgresStudentDAO.getCourseCatalogPrerequisites( null ) );

        try {
            Connection conn = postgresStudentDAO.getDatabaseConnection();
            conn.close();
            assertNull( postgresStudentDAO.getCourseCatalogPrerequisites( "CS301" ) );
        } catch ( Exception error ) {
            fail( "Database connection could not be closed" );
        }

    }

    @Test
    void getInstructorPrerequisites() {
        // null because of invalid input parameters
        assertNull( postgresStudentDAO.getInstructorPrerequisites( null, 2023, 2, "CS" ) );
        assertNull( postgresStudentDAO.getInstructorPrerequisites( "CS539", 0, 2, "CS" ) );
        assertNull( postgresStudentDAO.getInstructorPrerequisites( "CS539", 2023, 0, "CS" ) );
        assertNull( postgresStudentDAO.getInstructorPrerequisites( "CS539", 2023, 2, null ) );

        // Instructor prerequisites exist
        assertArrayEquals( new String[][]{ { "CS303", "8" } }, postgresStudentDAO.getInstructorPrerequisites( "CS539", 2023, 2, "CS" ) );

        try {
            Connection conn = postgresStudentDAO.getDatabaseConnection();
            conn.close();
            assertArrayEquals( null, postgresStudentDAO.getInstructorPrerequisites( "CS301", 2022, 1, "CS" ) );
        } catch ( Exception error ) {
            fail( "Database connection could not be closed" );
        }
    }

    @Test
    void getCreditsOfCourse() {
        // 24 because of invalid input
        assertEquals( 24, postgresStudentDAO.getCreditsOfCourse( null ) );

        // Actual credits of course
        assertEquals( 3, postgresStudentDAO.getCreditsOfCourse( "CS302" ) );

        try {
            Connection conn = postgresStudentDAO.getDatabaseConnection();
            conn.close();
            assertEquals( 24, postgresStudentDAO.getCreditsOfCourse( "CS301" ) );
        } catch ( Exception error ) {
            fail( "Database connection could not be closed" );
        }

    }

    @Test
    void getCreditsInSession() {
        // 25 due to invalid input parameters
        assertEquals( 25, postgresStudentDAO.getCreditsInSession( null, 2023, 2 ) );
        assertEquals( 25, postgresStudentDAO.getCreditsInSession( "2020CSB1062", 0, 2 ) );
        assertEquals( 25, postgresStudentDAO.getCreditsInSession( "2020CSB1062", 2023, 0 ) );

        // Successful
        assertEquals( 21, postgresStudentDAO.getCreditsInSession( "2020CSB1062", 2022, 2 ) );

        try {
            Connection conn = postgresStudentDAO.getDatabaseConnection();
            conn.close();
            assertEquals( 25, postgresStudentDAO.getCreditsInSession( "2020CSB1062", 2023, 2 ) );
        } catch ( Exception error ) {
            fail( "Database connection could not be closed" );
        }
    }

    @Test
    void enroll() {
        // False because of invalid input parameters
        assertFalse( postgresStudentDAO.enroll( null, "2020CSB1062", 2023, 2, "HS", "HE" ) );
        assertFalse( postgresStudentDAO.enroll( "HS507", null, 2023, 2, "HS", "HE" ) );
        assertFalse( postgresStudentDAO.enroll( "HS507", "2020CSB1062", 0, 2, "HS", "HE" ) );
        assertFalse( postgresStudentDAO.enroll( "HS507", "2020CSB1062", 2023, 0, "HS", "HE" ) );
        assertFalse( postgresStudentDAO.enroll( "HS507", "2020CSB1062", 2023, 2, null, "HE" ) );
        assertFalse( postgresStudentDAO.enroll( "HS507", "2020CSB1062", 2023, 2, "HS", null ) );

        // Successful enrollment
        adminDAO.setSessionEvent( "ENROLLING", 2023, 2 );
        facultyDAO.setCourseCategory( "HS507", 2023, 2, "HE", "CS", new int[]{ 2021 }, "HS" );
        assertTrue( postgresStudentDAO.enroll( "HS507", "2021CSB1062", 2023, 2, "HS", "HE" ) );
        postgresStudentDAO.dropCourse( "HS507", "2021CSB1062", 2023, 2 );
        facultyDAO.dropCourseOffering( "FAC38", "HS507", 2023, 2 );
        facultyDAO.insertCourseOffering( "HS507", 2023, 2, "HS", "FAC38" );
        adminDAO.setSessionEvent( "RUNNING", 2023, 2 );

        try {
            Connection conn = postgresStudentDAO.getDatabaseConnection();
            conn.close();
            assertFalse( postgresStudentDAO.enroll( "HS507", "2020CSB1062", 2023, 2, "HS", "HE" ) );
        } catch ( Exception error ) {
            fail( "Database connection could not be closed" );
        }
    }

    @Test
    void dropCourse() {
        // False because of invalid arguments
        assertFalse( postgresStudentDAO.dropCourse( null, "2020CSB1062", 2023, 2 ) );
        assertFalse( postgresStudentDAO.dropCourse( "HS507", null, 2023, 2 ) );
        assertFalse( postgresStudentDAO.dropCourse( "HS507", "2020CSB1062", 0, 2 ) );
        assertFalse( postgresStudentDAO.dropCourse( "HS507", "2020CSB1062", 2023, 0 ) );

        // Successful drop
        adminDAO.setSessionEvent( "ENROLLING", 2023, 2 );
        facultyDAO.setCourseCategory( "HS507", 2023, 2, "HE", "CS", new int[]{ 2021 }, "HS" );
        postgresStudentDAO.enroll( "HS507", "2021CSB1062", 2023, 2, "HS", "HE" );
        assertTrue( postgresStudentDAO.dropCourse( "HS507", "2021CSB1062", 2023, 2 ) );
        facultyDAO.dropCourseOffering( "FAC38", "HS507", 2023, 2 );
        facultyDAO.insertCourseOffering( "HS507", 2023, 2, "HS", "FAC38" );
        adminDAO.setSessionEvent( "RUNNING", 2023, 2 );

        try {
            Connection conn = postgresStudentDAO.getDatabaseConnection();
            conn.close();
            assertFalse( postgresStudentDAO.dropCourse( "HS507", "2020CSB1062", 2023, 2 ) );
        } catch ( Exception error ) {
            fail( "Database connection could not be closed" );
        }

    }

    @Test
    void getAllRecords() {
        // Empty array because of invalid argument
        assertArrayEquals( new String[][]{}, postgresStudentDAO.getAllRecords( null ) );

        // One record because one offering exists in the database
        adminDAO.setSessionEvent( "ENROLLING", 2023, 2 );
        facultyDAO.setCourseCategory( "HS507", 2023, 2, "HE", "CS", new int[]{ 2021 }, "HS" );
        postgresStudentDAO.enroll( "HS507", "2021CSB1062", 2023, 2, "HS", "HE" );

        assertArrayEquals( new String[][]{ { "3.0", "-" } }, postgresStudentDAO.getAllRecords( "2021CSB1062" ) );

        postgresStudentDAO.dropCourse( "HS507", "2021CSB1062", 2023, 2 );
        facultyDAO.dropCourseOffering( "FAC38", "HS507", 2023, 2 );
        facultyDAO.insertCourseOffering( "HS507", 2023, 2, "HS", "FAC38" );
        adminDAO.setSessionEvent( "RUNNING", 2023, 2 );

        try {
            Connection conn = postgresStudentDAO.getDatabaseConnection();
            conn.close();
            assertArrayEquals( new String[][]{}, postgresStudentDAO.getAllRecords( "2020CSB1062" ) );
        } catch ( Exception error ) {
            fail( "Database connection could not be closed" );
        }
    }

    @Test
    void getOfferedCourses() {
        // Empty array due to invalid input arguments
        assertArrayEquals( new String[][]{}, postgresStudentDAO.getOfferedCourses( 0, 2 ) );
        assertArrayEquals( new String[][]{}, postgresStudentDAO.getOfferedCourses( 2023, 0 ) );

        // Checking the valid input argument
        String[] course1 = { "MA628", "FINANCIAL DERIVATIVES PRICING", "DR ARUN KUMAR", "{}", "MA", "" };
        String[] course2 = { "NS104", "NSS IV", "DR BALESH KUMAR", "{}", "MA", "" };
        String[] course3 = { "CS539", "INTERNET OF THINGS", "DR SUJATA PAL", "{}", "CS", "" };
        String[] course4 = { "CP303", "CAPSTONE II", "DR ABHINAV DHALL", "{CP302}", "CS", "" };
        String[] course5 = { "CS550", "RESEARCH METHODOLOGIES IN COMPUTER SCIENCE", "DR MUKESH SAINI", "{}", "CS", "" };
        String[] course6 = { "HS507", "POSITIVE PSYCHOLOGY AND WELL-BEING", "DR PARWINDER SINGH", "{}", "HS", "" };
        String[] course7 = { "CS999", "TEST COURSE", "TEST FACULTY", "{}", "CS", "" };
        assertArrayEquals( new String[][]{ course1, course2, course3, course4, course5, course6, course7 }, postgresStudentDAO.getOfferedCourses( 2023, 2 ) );

        try {
            Connection conn = postgresStudentDAO.getDatabaseConnection();
            conn.close();
            assertArrayEquals( new String[][]{}, postgresStudentDAO.getOfferedCourses( 2023, 2 ) );
        } catch ( Exception error ) {
            fail( "Database connection could not be closed" );
        }
    }

    @Test
    void isCurrentEventEnrolling() {
        // False because of invalid input parameters
        assertFalse( postgresStudentDAO.isCurrentEventEnrolling( 0, 2 ) );
        assertFalse( postgresStudentDAO.isCurrentEventEnrolling( 2023, 0 ) );

        // True
        adminDAO.setSessionEvent( "ENROLLING", 2023, 2 );
        assertTrue( postgresStudentDAO.isCurrentEventEnrolling( 2023, 2 ) );
        adminDAO.setSessionEvent( "RUNNING", 2023, 2 );

        try {
            Connection conn = postgresStudentDAO.getDatabaseConnection();
            conn.close();
            assertFalse( postgresStudentDAO.isCurrentEventEnrolling( 2023, 2 ) );
        } catch ( Exception error ) {
            fail( "Database connection could not be closed" );
        }
    }

    @Test
    void getCGPACriteria() {
        // False because of invalid input parameters
        assertEquals( 11, postgresStudentDAO.getCGPACriteria( null, 2022, 2, "HS" ) );
        assertEquals( 11, postgresStudentDAO.getCGPACriteria( "HS507", 0, 2, "HS" ) );
        assertEquals( 11, postgresStudentDAO.getCGPACriteria( "HS507", 2022, 0, "HS" ) );
        assertEquals( 11, postgresStudentDAO.getCGPACriteria( "HS507", 2022, 2, null ) );

        // True
        assertEquals( 0, postgresStudentDAO.getCGPACriteria( "HS507", 2023, 2, "HS" ) );

        try {
            Connection conn = postgresStudentDAO.getDatabaseConnection();
            conn.close();
            assertEquals( 11, postgresStudentDAO.getCGPACriteria( "HS507", 2022, 2, "HS" ) );
        } catch ( Exception error ) {
            fail( "Database connection could not be closed" );
        }
    }

    @Test
    void getCourseCategory() {
        // "" because of invalid input parameters
        assertEquals( "", postgresStudentDAO.getCourseCategory( null, 2023, 2, "HS", "CS", 2021 ) );
        assertEquals( "", postgresStudentDAO.getCourseCategory( "HS301", 0, 2, "HS", "CS", 2021 ) );
        assertEquals( "", postgresStudentDAO.getCourseCategory( "HS301", 2023, 0, "HS", "CS", 2021 ) );
        assertEquals( "", postgresStudentDAO.getCourseCategory( "HS301", 2023, 2, null, "CS", 2021 ) );
        assertEquals( "", postgresStudentDAO.getCourseCategory( "HS301", 2023, 2, "HS", null, 2021 ) );
        assertEquals( "", postgresStudentDAO.getCourseCategory( "HS301", 2023, 2, "HS", "CS", 0 ) );

        // The course category has been set up in the test database
        assertEquals( "PC", postgresStudentDAO.getCourseCategory( "CS101", 2020, 1, "CS", "CS", 2020 ) );

        try {
            Connection conn = postgresStudentDAO.getDatabaseConnection();
            conn.close();
            assertEquals( "", postgresStudentDAO.getCourseCategory( "HS301", 2023, 2, "HS", "CS", 2021 ) );
        } catch ( Exception error ) {
            fail( "Database connection could not be closed" );
        }
    }
}