package org.abhijith.dal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.*;

class PostgresAdminDAOTest {
    PostgresAdminDAO adminDAO;

    @BeforeEach
    void setUp() {
        adminDAO = new PostgresAdminDAO();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getGradesOfCourse() {
        String     courseCode         = "CS539";
        int        year               = 2023;
        int        semester           = 2;
        String     offeringDepartment = "CS";
        String     invalidDepartment  = "HS";
        String[][] emptyArray         = new String[][]{};
        String[][] expectedResult     = new String[][]{ { "2020CSB1062", "A" } };

        // [][] because of invalid input arguments
        assertArrayEquals( emptyArray, adminDAO.getGradesOfCourse( null, year, semester, offeringDepartment ) );
        assertArrayEquals( emptyArray, adminDAO.getGradesOfCourse( courseCode, -1, semester, offeringDepartment ) );
        assertArrayEquals( emptyArray, adminDAO.getGradesOfCourse( courseCode, year, -1, offeringDepartment ) );
        assertArrayEquals( emptyArray, adminDAO.getGradesOfCourse( courseCode, year, semester, null ) );

        // [][] because no such subject will be found
        assertArrayEquals( emptyArray, adminDAO.getGradesOfCourse( courseCode, year, semester, invalidDepartment ) );

        // Returns one entry that is present in the test data
        assertArrayEquals( expectedResult, adminDAO.getGradesOfCourse( courseCode, year, semester, offeringDepartment ) );

        // because the connection has been closed
        try {
            Connection databaseConnection = adminDAO.getDatabaseConnection();
            databaseConnection.close();
            assertArrayEquals( emptyArray, adminDAO.getGradesOfCourse( courseCode, year, semester, offeringDepartment ) );
        } catch ( Exception error ) {
            fail( "Could not close database connection" );
        }
    }

    @Test
    void insertStudent() {
        String entryNumber  = "2021CSB1063";
        String departmentID = "CS";
        String name         = "SURESH";
        int    batch        = 2021;

        // False because of invalid input parameters
        assertFalse( adminDAO.insertStudent( null, name, departmentID, batch ) );
        assertFalse( adminDAO.insertStudent( entryNumber, null, departmentID, batch ) );
        assertFalse( adminDAO.insertStudent( entryNumber, name, null, batch ) );
        assertFalse( adminDAO.insertStudent( entryNumber, name, departmentID, -1 ) );

        // True because there is no such student, and he will be inserted
        assertTrue( adminDAO.insertStudent( entryNumber, name, departmentID, batch ) );

        // False because you have already inserted the student
        assertFalse( adminDAO.insertStudent( entryNumber, name, departmentID, batch ) );

        // Cleaning the database after these operations
        try {
            Connection databaseConnection = adminDAO.getDatabaseConnection();

            PreparedStatement removeStudent = databaseConnection.prepareStatement( "DELETE FROM student WHERE entry_number = ?" );
            removeStudent.setString( 1, entryNumber );
            removeStudent.executeUpdate();

            PreparedStatement removeUser = databaseConnection.prepareStatement( "DELETE FROM common_user_details WHERE id = ?" );
            removeUser.setString( 1, entryNumber );
            removeUser.executeUpdate();
        } catch ( Exception error ) {
            fail( "Could not remove student from the database" );
        }

    }

    @Test
    void insertFaculty() {
        String facultyID    = "NEW1";
        String name         = "Suresh";
        String departmentID = "CS";

        // False due to invalid input parameters
        assertFalse( adminDAO.insertFaculty( null, name, departmentID ) );
        assertFalse( adminDAO.insertFaculty( facultyID, null, departmentID ) );
        assertFalse( adminDAO.insertFaculty( facultyID, name, null ) );

        // True as the faculty has been inserted
        assertTrue( adminDAO.insertFaculty( facultyID, name, departmentID ) );

        // False as the faculty already exists
        assertFalse( adminDAO.insertFaculty( facultyID, name, departmentID ) );

        try {
            Connection databaseConnection = adminDAO.getDatabaseConnection();

            PreparedStatement deleteQuery = databaseConnection.prepareStatement( "DELETE FROM faculty WHERE faculty_id = ?" );
            deleteQuery.setString( 1, facultyID );
            deleteQuery.executeUpdate();

            deleteQuery = databaseConnection.prepareStatement( "DELETE FROM common_user_details WHERE id = ?" );
            deleteQuery.setString( 1, facultyID );
            deleteQuery.executeUpdate();
        } catch ( Exception error ) {
            fail( "Could not delete contents from database" );
        }
    }

    @Test
    void insertCourse() {
        String   courseCode      = "CS888";
        String   courseTitle     = "New Course";
        double[] creditStructure = new double[]{ 1, 1, 1, 1, 1 };
        String[] prerequisites   = new String[]{ "CS101" };

        // False because of invalid input parameters
        assertFalse( adminDAO.insertCourse( null, courseTitle, creditStructure, prerequisites ) );
        assertFalse( adminDAO.insertCourse( courseCode, null, creditStructure, prerequisites ) );
        assertFalse( adminDAO.insertCourse( courseCode, courseTitle, null, prerequisites ) );
        assertFalse( adminDAO.insertCourse( courseCode, courseTitle, new double[]{}, prerequisites ) );
        assertFalse( adminDAO.insertCourse( courseCode, courseTitle, new double[]{ -1, -1, -1, -1, -1 }, prerequisites ) );
        assertFalse( adminDAO.insertCourse( courseCode, courseTitle, creditStructure, new String[]{ null } ) );
        assertFalse( adminDAO.insertCourse( courseCode, courseTitle, creditStructure, null ) );

        // True because the course is inserted into the database
        assertTrue( adminDAO.insertCourse( courseCode, courseTitle, creditStructure, prerequisites ) );

        // False because the course already exists in the database
        assertFalse( adminDAO.insertCourse( courseCode, courseTitle, creditStructure, prerequisites ) );
        adminDAO.dropCourseFromCatalog( courseCode );
    }

    @Test
    void checkAllPrerequisites() {
        // False because of invalid input parameters
        assertFalse( adminDAO.checkAllPrerequisites( null ) );
        assertFalse( adminDAO.checkAllPrerequisites( new String[]{ null } ) );

        // False because some courses do not exist in the database
        assertFalse( adminDAO.checkAllPrerequisites( new String[]{ "CS888" } ) );

        // True because all courses exist in the database
        assertTrue( adminDAO.checkAllPrerequisites( new String[]{ "CS101" } ) );

        // False because the exception is triggered
        try {
            Connection connection = adminDAO.getDatabaseConnection();
            connection.close();
            assertFalse( adminDAO.checkAllPrerequisites( new String[]{ "CS101" } ) );
        } catch ( Exception error ) {
            fail( "Could not close database connection" );
        }
    }

    @Test
    void dropCourseFromCatalog() {
        String   courseCode      = "CS888";
        String   courseTitle     = "New Course";
        double[] creditStructure = new double[]{ 1, 1, 1, 1, 1 };
        String[] prerequisites   = new String[]{ "CS101" };

        // False due to invalid input parameters
        assertFalse( adminDAO.dropCourseFromCatalog( null ) );

        // False because such a course does not exist
        assertFalse( adminDAO.dropCourseFromCatalog( courseCode ) );

        // True because the course is dropped from the database
        adminDAO.insertCourse( courseCode, courseTitle, creditStructure, prerequisites );
        assertTrue( adminDAO.dropCourseFromCatalog( courseCode ) );

        // False because the exception is triggered
        try {
            Connection connection = adminDAO.getDatabaseConnection();
            connection.close();
            assertFalse( adminDAO.dropCourseFromCatalog( courseCode ) );
        } catch ( Exception error ) {
            fail( "Could not close database connection" );
        }
    }

    @Test
    void createBatch() {
        int batch = 9999;
        // False due to invalid input parameters
        assertFalse( adminDAO.createBatch( -1 ) );

        // True because the batch is inserted
        assertTrue( adminDAO.createBatch( batch ) );

        // False because the batch already exists
        assertFalse( adminDAO.createBatch( batch ) );

        try {
            Connection        connection  = adminDAO.getDatabaseConnection();
            PreparedStatement deleteQuery = connection.prepareStatement( "DELETE FROM batch WHERE year = ?" );
            deleteQuery.setInt( 1, batch );
            deleteQuery.executeUpdate();
        } catch ( Exception error ) {
            fail( "Could not delete from database" );
        }
    }

    @Test
    void createCurriculum() {
        int      batch      = 9999;
        double[] curriculum = new double[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };

        // False because of invalid input parameters
        assertFalse( adminDAO.createCurriculum( -1, curriculum ) );
        assertFalse( adminDAO.createCurriculum( batch, null ) );
        assertFalse( adminDAO.createCurriculum( batch, new double[]{ 1 } ) );
        assertFalse( adminDAO.createCurriculum( batch, new double[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, -1, 1 } ) );

        // True because the curriculum was inserted
        adminDAO.createBatch( 9999 );
        assertTrue( adminDAO.createCurriculum( batch, curriculum ) );

        // False because the curriculum already exists
        assertFalse( adminDAO.createCurriculum( batch, curriculum ) );

        try {
            Connection        connection  = adminDAO.getDatabaseConnection();
            PreparedStatement deleteQuery = connection.prepareStatement( "DELETE FROM batch WHERE year = ?" );
            deleteQuery.setInt( 1, batch );
            deleteQuery.executeUpdate();
        } catch ( Exception error ) {
            fail( "Could not delete from database" );
        }
    }

    @Test
    void insertCoreCourse() {
        String   courseCode      = "CS888";
        String[] departmentCodes = new String[]{ "CS", "CE" };
        int      batch           = 2020;
        String   courseCategory  = "PC";

        // False due to invalid input parameters
        assertFalse( adminDAO.insertCoreCourse( null, departmentCodes, batch, courseCategory ) );
        assertFalse( adminDAO.insertCoreCourse( courseCode, null, batch, courseCategory ) );
        assertFalse( adminDAO.insertCoreCourse( courseCode, departmentCodes, -1, courseCategory ) );
        assertFalse( adminDAO.insertCoreCourse( courseCode, departmentCodes, batch, null ) );
        assertFalse( adminDAO.insertCoreCourse( courseCode, new String[]{ null }, batch, courseCategory ) );

        // True because the course is inserted
        assertTrue( adminDAO.insertCoreCourse( courseCode, departmentCodes, batch, courseCategory ) );

        // False because the courses already exist
        assertFalse( adminDAO.insertCoreCourse( courseCode, departmentCodes, batch, courseCategory ) );

        try {
            Connection        connection  = adminDAO.getDatabaseConnection();
            PreparedStatement deleteQuery = connection.prepareStatement( "DELETE FROM core_courses WHERE course_code = ?" );
            deleteQuery.setString( 1, courseCode );
            deleteQuery.executeUpdate();
        } catch ( Exception error ) {
            fail( "Could not delete from database" );
        }
    }

    @Test
    void resetCoreCoursesList() {
        try {
            Connection connection = adminDAO.getDatabaseConnection();
            PreparedStatement tempQuery = connection.prepareStatement( "INSERT INTO batch VALUES( 9999 )" );
            tempQuery.executeUpdate();
            tempQuery = connection.prepareStatement( "INSERT INTO core_courses VALUES( 'CS101', 'CS', 9999, 'SC')" );
            tempQuery.executeUpdate();

            assertTrue( adminDAO.resetCoreCoursesList( 9999 ) );
            tempQuery = connection.prepareStatement( "DELETE FROM batch WHERE year = 9999" );
            tempQuery.executeUpdate();

            connection.close();
            // False when an exception occurs
            assertFalse( adminDAO.resetCoreCoursesList( 9999 ) );
        } catch ( Exception error ) {
            fail( "Could not close database connection" );
        }
    }

    @Test
    void findEntryNumber() {
        // False due to invalid input parameters
        assertFalse( adminDAO.findEntryNumber( null ) );

        // True because the entry number exists
        assertTrue( adminDAO.findEntryNumber( "2020CSB1062" ) );

        // False because the entry number does not exist
        assertFalse( adminDAO.findEntryNumber( "9999" ) );

        // False due to an exception
        try {
            Connection connection = adminDAO.getDatabaseConnection();
            connection.close();
            assertFalse( adminDAO.findEntryNumber( "2020CSB1062" ) );
        } catch ( Exception error ) {
            fail( "Could not close database connection" );
        }
    }

    @Test
    void getCoreCourses() {
        int    batch             = 2021;
        String studentDepartment = "CS";

        // Null due to invalid input parameters
        assertNull( adminDAO.getCoreCourses( -1, studentDepartment ) );
        assertNull( adminDAO.getCoreCourses( batch, null ) );

        // Expected result
        adminDAO.insertCoreCourse( "CS999", new String[]{ "CS" }, 2021, "PC" );
        assertArrayEquals( new String[]{ "CS999" }, adminDAO.getCoreCourses( batch, studentDepartment ) );

        try {
            Connection        connection  = adminDAO.getDatabaseConnection();
            PreparedStatement deleteQuery = connection.prepareStatement( "DELETE FROM core_courses WHERE course_code = ?" );
            deleteQuery.setString( 1, "CS999" );
            deleteQuery.executeUpdate();

            connection.close();
            assertArrayEquals( null, adminDAO.getCoreCourses( batch, studentDepartment ) );
        } catch ( Exception error ) {
            fail( "Could not delete from database / close database connection" );
        }
    }

    @Test
    void getListOfStudentsInBatch() {
        int    batch      = 2020;
        String department = "CS";

        // Null due to invalid input parameters
        assertNull( adminDAO.getListOfStudentsInBatch( -1, department ) );
        assertNull( adminDAO.getListOfStudentsInBatch( batch, null ) );

        // Expected result
        assertArrayEquals( new String[]{ "2020CSB1062" }, adminDAO.getListOfStudentsInBatch( batch, department ) );

        // Null due to exception
        try {
            Connection connection = adminDAO.getDatabaseConnection();
            connection.close();
            assertArrayEquals( null, adminDAO.getListOfStudentsInBatch( batch, department ) );
        } catch ( Exception error ) {
            fail( "Could not close database connection" );
        }
    }

    @Test
    void checkIfSessionCompleted() {
        int year     = 2023;
        int semester = 2;

        // False due to invalid input parameters
        assertFalse( adminDAO.checkIfSessionCompleted( -1, 1 ) );
        assertFalse( adminDAO.checkIfSessionCompleted( 2020, -1 ) );

        // False because the session was not found
        assertFalse( adminDAO.checkIfSessionCompleted( 2020, 1 ) );

        // False because the event is not completed
        assertFalse( adminDAO.checkIfSessionCompleted( year, semester ) );

        // True because the event is completed
        adminDAO.setSessionEvent( "COMPLETED", year, semester );
        assertTrue( adminDAO.checkIfSessionCompleted( year, semester ) );
        adminDAO.setSessionEvent( "RUNNING", year, semester );

        // False because of an exception
        try {
            Connection connection = adminDAO.getDatabaseConnection();
            connection.close();
            assertFalse( adminDAO.checkIfSessionCompleted( year, semester ) );
        } catch ( Exception error ) {
            fail( "Could not close database connection" );
        }
    }

    @Test
    void createNewSession() {
        int newYear     = 2024;
        int newSemester = 1;

        // False due to invalid input parameters
        assertFalse( adminDAO.createNewSession( -1, 2 ) );
        assertFalse( adminDAO.createNewSession( 2020, -1 ) );

        // True because the session was created
        assertTrue( adminDAO.createNewSession( newYear, newSemester ) );

        // False because the session already exists
        assertFalse( adminDAO.createNewSession( newYear, newSemester ) );

        try {
            Connection        connection  = adminDAO.getDatabaseConnection();
            PreparedStatement deleteQuery = connection.prepareStatement( "DELETE FROM current_year_and_semester WHERE year = ? AND semester = ?" );
            deleteQuery.setInt( 1, newYear );
            deleteQuery.setInt( 2, newSemester );
            deleteQuery.executeUpdate();
        } catch ( Exception error ) {
            System.out.println( error.getMessage() );
            fail( "Could not delete from database" );
        }
    }

    @Test
    void setSessionEvent() {
        String event    = "ENROLLING";
        int    year     = 2023;
        int    semester = 2;

        // False due to invalid input parameters
        assertFalse( adminDAO.setSessionEvent( null, year, semester ) );
        assertFalse( adminDAO.setSessionEvent( event, -1, semester ) );
        assertFalse( adminDAO.setSessionEvent( event, year, -1 ) );

        // False as the event is invalid
        assertFalse( adminDAO.setSessionEvent( "RANDOM", year, semester ) );

        // True as the update is successful
        assertTrue( adminDAO.setSessionEvent( event, year, semester ) );
        adminDAO.setSessionEvent( "RUNNING", 2023, 2 );

        // False due to the exception
        try {
            Connection connection = adminDAO.getDatabaseConnection();
            connection.close();
            assertFalse( adminDAO.setSessionEvent( event, year, semester ) );
        } catch ( Exception error ) {
            fail( "Could not close database connection" );
        }
    }

    @Test
    void verifyNoMissingGrades() {
        int currentYear     = 2023;
        int currentSemester = 2;

        // False due to invalid input parameters
        assertFalse( adminDAO.verifyNoMissingGrades( -1, currentSemester) );
        assertFalse( adminDAO.verifyNoMissingGrades( currentYear, -1 ) );

        // True as there are no unentered grades in this semester
        assertTrue( adminDAO.verifyNoMissingGrades( currentYear, currentSemester ) );

        // False due to the exception
        try {
            Connection connection = adminDAO.getDatabaseConnection();

            PreparedStatement tempQuery = connection.prepareStatement( "INSERT INTO student_course_registration VALUES('2020CSB1062', 'HS507', 2023, 2, '-', 'HS', 'HE')" );
            tempQuery.executeUpdate();
            // False as there are unentered grades in this semester
            assertFalse( adminDAO.verifyNoMissingGrades( currentYear, currentSemester ) );
            tempQuery = connection.prepareStatement("DELETE FROM student_course_registration WHERE entry_number = '2020CSB1062' AND course_code = 'HS507'");
            tempQuery.executeUpdate();

            connection.close();
            assertFalse( adminDAO.verifyNoMissingGrades( currentYear, currentSemester ) );
        } catch ( Exception error ) {
            fail( "Could not close database connection" );
        }
    }
}