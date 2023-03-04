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
        String courseCode       = "HS507";
        String courseDepartment = "HS";
        int    currentYear      = 2023;
        int    currentSemester  = 2;
        int    previousYear     = 2022;

        // Successful because course exists from this department in this particular session
        assertTrue( postgresStudentDAO.checkCourseOffering( courseCode, currentYear, currentSemester, courseDepartment ) );

        // Fails because this course does not exist in this particular year and semester
        assertFalse( postgresStudentDAO.checkCourseOffering( "CS101", previousYear, currentSemester, "CS" ) );

        // Fails because of invalid year and semester and strings being null
        assertFalse( postgresStudentDAO.checkCourseOffering( courseCode, -1, currentSemester, courseDepartment ) );
        assertFalse( postgresStudentDAO.checkCourseOffering( courseCode, previousYear, -1, courseDepartment ) );
        assertFalse( postgresStudentDAO.checkCourseOffering( null, currentYear, currentSemester, courseDepartment ) );
        assertFalse( postgresStudentDAO.checkCourseOffering( courseCode, currentYear, currentSemester, null ) );


        try {
            Connection conn = postgresStudentDAO.getDatabaseConnection();
            conn.close();
            assertFalse( postgresStudentDAO.checkCourseOffering( courseCode, currentYear, currentSemester, courseDepartment ) );
        } catch ( Exception error ) {
            System.out.println( error.getMessage() );
            fail( "Database connection could not be closed" );
        }
    }

    @Test
    void checkStudentPassStatus() {
        String completedCourse = "CS101";
        String failedCourse    = "CS999";
        String invalidCourse   = "ZZ123";
        String courseCode      = "CS555";
        String entryNumber     = "2020CSB1062";
        int    gradeCutoff     = 8;

        // Successful because this particular student has done and passed this course before
        assertTrue( postgresStudentDAO.checkStudentPassStatus( completedCourse, gradeCutoff, entryNumber ) );

        // False because the student has failed in the course
        assertFalse( postgresStudentDAO.checkStudentPassStatus( failedCourse, gradeCutoff, entryNumber ) );

        // False because no such course exists
        assertFalse( postgresStudentDAO.checkStudentPassStatus( invalidCourse, gradeCutoff, entryNumber ) );

        // False because of invalid input parameters
        assertFalse( postgresStudentDAO.checkStudentPassStatus( null, gradeCutoff, entryNumber ) );
        assertFalse( postgresStudentDAO.checkStudentPassStatus( invalidCourse, 11, entryNumber ) );
        assertFalse( postgresStudentDAO.checkStudentPassStatus( invalidCourse, -1, entryNumber ) );
        assertFalse( postgresStudentDAO.checkStudentPassStatus( invalidCourse, gradeCutoff, null ) );

        try {
            Connection conn = postgresStudentDAO.getDatabaseConnection();
            conn.close();
            assertFalse( postgresStudentDAO.checkStudentPassStatus( courseCode, gradeCutoff, entryNumber ) );
        } catch ( Exception error ) {
            fail( "Database connection could not be closed" );
        }
    }

    @Test
    void getCourseCatalogPrerequisites() {
        String   invalidCourse = "CS777";
        String   courseCode    = "CS202";
        String[] prerequisites = new String[]{ "CS201" };

        // Null because such a course does not exist
        assertNull( postgresStudentDAO.getCourseCatalogPrerequisites( invalidCourse ) );

        // Prerequisites exist
        assertArrayEquals( prerequisites, postgresStudentDAO.getCourseCatalogPrerequisites( courseCode ) );

        // False because of invalid input parameters
        assertNull( postgresStudentDAO.getCourseCatalogPrerequisites( null ) );

        try {
            Connection conn = postgresStudentDAO.getDatabaseConnection();
            conn.close();
            assertNull( postgresStudentDAO.getCourseCatalogPrerequisites( courseCode ) );
        } catch ( Exception error ) {
            fail( "Database connection could not be closed" );
        }

    }

    @Test
    void getInstructorPrerequisites() {
        String     courseCode             = "CS539";
        String     department             = "CS";
        int        year                   = 2023;
        int        semester               = 2;
        String[][] instructorPrerequisite = new String[][]{ { "CS303", "8" } };

        // null because of invalid input parameters
        assertNull( postgresStudentDAO.getInstructorPrerequisites( null, year, semester, department ) );
        assertNull( postgresStudentDAO.getInstructorPrerequisites( courseCode, -1, semester, department ) );
        assertNull( postgresStudentDAO.getInstructorPrerequisites( courseCode, year, -1, department ) );
        assertNull( postgresStudentDAO.getInstructorPrerequisites( courseCode, year, semester, null ) );

        // Instructor prerequisites exist
        assertArrayEquals( instructorPrerequisite, postgresStudentDAO.getInstructorPrerequisites( courseCode, year, semester, department ) );

        try {
            Connection conn = postgresStudentDAO.getDatabaseConnection();
            conn.close();
            assertArrayEquals( null, postgresStudentDAO.getInstructorPrerequisites( courseCode, year, semester, department ) );
        } catch ( Exception error ) {
            fail( "Database connection could not be closed" );
        }
    }

    @Test
    void getCreditsOfCourse() {
        String courseCode      = "CS302";
        int    maximumCredits  = 24;
        int    expectedCredits = 3;

        // 24 because of invalid input
        assertEquals( maximumCredits, postgresStudentDAO.getCreditsOfCourse( null ) );

        // Actual credits of course
        assertEquals( expectedCredits, postgresStudentDAO.getCreditsOfCourse( courseCode ) );

        try {
            Connection conn = postgresStudentDAO.getDatabaseConnection();
            conn.close();
            assertEquals( maximumCredits, postgresStudentDAO.getCreditsOfCourse( courseCode ) );
        } catch ( Exception error ) {
            fail( "Database connection could not be closed" );
        }

    }

    @Test
    void getCreditsInSession() {
        String entryNumber        = "2020CSB1062";
        int    currentYear        = 2023;
        int    currentSemester    = 2;
        int    previousYear       = 2022;
        int    previousSemester   = 2;
        int    creditsInSession   = 21;
        int    creditLimitPlusOne = 25;

        // 25 due to invalid input parameters
        assertEquals( creditLimitPlusOne, postgresStudentDAO.getCreditsInSession( null, currentYear, currentSemester ) );
        assertEquals( creditLimitPlusOne, postgresStudentDAO.getCreditsInSession( entryNumber, -1, currentSemester ) );
        assertEquals( creditLimitPlusOne, postgresStudentDAO.getCreditsInSession( entryNumber, currentYear, -1 ) );

        // Successful
        assertEquals( creditsInSession, postgresStudentDAO.getCreditsInSession( entryNumber, previousYear, previousSemester ) );

        try {
            Connection conn = postgresStudentDAO.getDatabaseConnection();
            conn.close();
            assertEquals( creditLimitPlusOne, postgresStudentDAO.getCreditsInSession( entryNumber, currentYear, currentSemester ) );
        } catch ( Exception error ) {
            fail( "Database connection could not be closed" );
        }
    }

    @Test
    void enroll() {
        String courseCode           = "HS507";
        String entryNumber          = "2020CSB1062";
        String enrollingEntryNumber = "2021CSB1062";
        String offeringDepartment   = "HS";
        String enrollingDepartment  = "CS";
        String courseCategory       = "HE";
        String enrollingEvent       = "ENROLLING";
        String runningEvent         = "RUNNING";
        String facultyID            = "FAC38";
        int    currentYear          = 2023;
        int    currentSemester      = 2;
        int[]  eligibleBatches      = new int[]{ 2021 };

        // False because of invalid input parameters
        assertFalse( postgresStudentDAO.enroll( null, entryNumber, currentYear, currentSemester, offeringDepartment, courseCategory ) );
        assertFalse( postgresStudentDAO.enroll( courseCode, null, currentYear, currentSemester, offeringDepartment, courseCategory ) );
        assertFalse( postgresStudentDAO.enroll( courseCode, entryNumber, -1, currentSemester, offeringDepartment, courseCategory ) );
        assertFalse( postgresStudentDAO.enroll( courseCode, entryNumber, currentYear, -1, offeringDepartment, courseCategory ) );
        assertFalse( postgresStudentDAO.enroll( courseCode, entryNumber, currentYear, currentSemester, null, courseCategory ) );
        assertFalse( postgresStudentDAO.enroll( courseCode, entryNumber, currentYear, currentSemester, offeringDepartment, null ) );

        // Successful enrollment
        adminDAO.setSessionEvent( enrollingEvent, currentYear, currentSemester );
        facultyDAO.setCourseCategory( courseCode, currentYear, currentSemester, courseCategory, enrollingDepartment, eligibleBatches, offeringDepartment );
        assertTrue( postgresStudentDAO.enroll( courseCode, enrollingEntryNumber, currentYear, currentSemester, offeringDepartment, courseCategory ) );
        postgresStudentDAO.dropCourse( courseCode, enrollingEntryNumber, currentYear, currentSemester );
        facultyDAO.dropCourseOffering( facultyID, courseCode, currentYear, currentSemester, offeringDepartment );
        facultyDAO.insertCourseOffering( courseCode, currentYear, currentSemester, offeringDepartment, facultyID );
        adminDAO.setSessionEvent( runningEvent, currentYear, currentSemester );

        try {
            Connection conn = postgresStudentDAO.getDatabaseConnection();
            conn.close();
            assertFalse( postgresStudentDAO.enroll( courseCode, entryNumber, currentYear, 2, offeringDepartment, courseCategory ) );
        } catch ( Exception error ) {
            fail( "Database connection could not be closed" );
        }
    }

    @Test
    void dropCourse() {
        String entryNumber          = "2020CSB1062";
        String enrollingEntryNumber = "2021CSB1062";
        String courseCode           = "HS507";
        String enrollingEvent       = "ENROLLING";
        String runningEvent         = "RUNNING";
        String facultyID            = "FAC38";
        String courseCategory       = "HE";
        String offeringDepartment   = "HS";
        String offeredDepartment    = "CS";
        int    currentYear          = 2023;
        int    currentSemester      = 2;
        int[]  offeredBatches       = new int[]{ 2021 };

        // False because of invalid arguments
        assertFalse( postgresStudentDAO.dropCourse( null, entryNumber, currentYear, currentSemester ) );
        assertFalse( postgresStudentDAO.dropCourse( courseCode, null, currentYear, currentSemester ) );
        assertFalse( postgresStudentDAO.dropCourse( courseCode, entryNumber, -1, currentSemester ) );
        assertFalse( postgresStudentDAO.dropCourse( courseCode, entryNumber, currentYear, -1 ) );

        // Successful drop
        adminDAO.setSessionEvent( enrollingEvent, currentYear, currentSemester );
        facultyDAO.setCourseCategory( courseCode, currentYear, currentSemester, courseCategory, offeredDepartment, offeredBatches, offeringDepartment );
        postgresStudentDAO.enroll( courseCode, enrollingEntryNumber, currentYear, currentSemester, offeringDepartment, courseCategory );
        assertTrue( postgresStudentDAO.dropCourse( courseCode, enrollingEntryNumber, currentYear, currentSemester ) );
        facultyDAO.dropCourseOffering( facultyID, courseCode, currentYear, currentSemester, offeringDepartment );
        facultyDAO.insertCourseOffering( courseCode, currentYear, currentSemester, offeringDepartment, facultyID );
        adminDAO.setSessionEvent( runningEvent, currentYear, currentSemester );

        try {
            Connection conn = postgresStudentDAO.getDatabaseConnection();
            conn.close();
            assertFalse( postgresStudentDAO.dropCourse( courseCode, entryNumber, currentYear, currentSemester ) );
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
        facultyDAO.dropCourseOffering( "FAC38", "HS507", 2023, 2, "HS" );
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