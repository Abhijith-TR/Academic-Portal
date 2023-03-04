package org.abhijith.dal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.*;

class PostgresAdminDAOTest {
    PostgresAdminDAO adminDAO;

    @BeforeEach
    void setUp() {
        adminDAO = new PostgresAdminDAO(
                "jdbc:postgresql://localhost:5432/mini_project",
                "postgres",
                "admin"
        );
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
        assertFalse( adminDAO.insertStudent( null, name ,departmentID, batch) );
        assertFalse( adminDAO.insertStudent( entryNumber, null ,departmentID, batch) );
        assertFalse( adminDAO.insertStudent( entryNumber, name ,null, batch) );
        assertFalse( adminDAO.insertStudent( entryNumber, name ,departmentID, -1) );

        // True because there is no such student, and he will be inserted
        assertTrue( adminDAO.insertStudent( entryNumber, name ,departmentID, batch) );

        // False because you have already inserted the student
        assertFalse( adminDAO.insertStudent( entryNumber, name, departmentID, batch ) );

        // Cleaning the database after these operations
        try {
            Connection databaseConnection = adminDAO.getDatabaseConnection();

            PreparedStatement removeStudent = databaseConnection.prepareStatement( "DELETE FROM student WHERE entry_number = ?");
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

    }

    @Test
    void insertCourse() {
    }

    @Test
    void checkAllPrerequisites() {
    }

    @Test
    void dropCourseFromCatalog() {
    }

    @Test
    void createBatch() {
    }

    @Test
    void createCurriculum() {
    }

    @Test
    void insertCoreCourse() {
    }

    @Test
    void resetCoreCoursesList() {
    }

    @Test
    void findEntryNumber() {
    }

    @Test
    void getCoreCourses() {
    }

    @Test
    void getListOfStudentsInBatch() {
    }

    @Test
    void checkIfSessionCompleted() {
    }

    @Test
    void createNewSession() {
    }

    @Test
    void setSessionEvent() {
    }

    @Test
    void verifyNoMissingGrades() {
    }
}