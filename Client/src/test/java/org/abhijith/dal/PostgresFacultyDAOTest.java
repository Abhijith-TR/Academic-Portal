package org.abhijith.dal;

import org.abhijith.daoInterfaces.AdminDAO;
import org.abhijith.daoInterfaces.FacultyDAO;
import org.abhijith.users.Admin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class PostgresFacultyDAOTest {
    PostgresFacultyDAO facultyDAO;

    @BeforeEach
    void setUp() {
        facultyDAO = new PostgresFacultyDAO(
                "jdbc:postgresql://localhost:5432/mini_project",
                "postgres",
                "admin"
        );
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void insertCourseOffering() {
        // False because of invalid input parameters
        assertFalse( facultyDAO.insertCourseOffering( null, 2023, 2, "HS", "FAC1" ) );
        assertFalse( facultyDAO.insertCourseOffering( "HS507", 0, 2, "HS", "FAC1" ) );
        assertFalse( facultyDAO.insertCourseOffering( "HS507", 2023, 0, "HS", "FAC1" ) );
        assertFalse( facultyDAO.insertCourseOffering( "HS507", 2023, 2, null, "FAC1" ) );
        assertFalse( facultyDAO.insertCourseOffering( "HS507", 2023, 2, "HS", null ) );

        // False because the course already exists
        assertFalse( facultyDAO.insertCourseOffering( "HS507", 2023, 2, "HS", "ADMIN1" ) );

        // Successful because the course does not exist
        assertTrue( facultyDAO.insertCourseOffering( "HS301", 2023, 2, "HS", "FAC38" ) );
        facultyDAO.dropCourseOffering( "FAC38", "HS301", 2023, 2 );
    }

    @Test
    void getDepartment() {
        // Returns "" as the arguement is invalid
        assertEquals( "", facultyDAO.getDepartment( null ) );

        // Gets the faculty ID of the faculty who exists
        assertEquals( "HS", facultyDAO.getDepartment( "FAC38" ) );

        // The faculty does not exist in the database, hence it returns the value of ""
        assertEquals( "", facultyDAO.getDepartment( "FAC99" ) );
    }

    @Test
    void setCGCriteria() {
        // False due to invalid input parameters
        assertFalse( facultyDAO.setCGCriteria( null, "HS507", 8, new int[]{ 2023, 2 }, "HS" ) );
        assertFalse( facultyDAO.setCGCriteria( "FAC38", null, 8, new int[]{ 2023, 2 }, "HS" ) );
        assertFalse( facultyDAO.setCGCriteria( "FAC38", "HS507", 11, new int[]{ 2023, 2 }, "HS" ) );
        assertFalse( facultyDAO.setCGCriteria( "FAC38", "HS507", -1, new int[]{ 2023, 2 }, "HS" ) );
        assertFalse( facultyDAO.setCGCriteria( "FAC38", "HS507", 8, null, "HS" ) );
        assertFalse( facultyDAO.setCGCriteria( "FAC38", "HS507", 8, new int[]{ 2023, 2 }, null ) );

        // Returns true as the CG criteria is valid
        assertTrue( facultyDAO.setCGCriteria( "FAC38", "HS507", 8, new int[]{ 2023, 2 }, "HS" ) );

        // Second time to show that updating it to the same value for a second time is allowed
        assertTrue( facultyDAO.setCGCriteria( "FAC38", "HS507", 8, new int[]{ 2023, 2 }, "HS" ) );

        facultyDAO.setCGCriteria( "FAC38", "HS507", 0, new int[]{ 2023, 2 }, "HS" );

        // Fails as the database connection is closed. This is the only error that can occur in the try block
        try {
            Connection conn = facultyDAO.getDatabaseConnection();
            conn.close();
            assertFalse( facultyDAO.setCGCriteria( "FAC38", "HS507", 0, new int[]{ 2023, 2 }, "HS" ) );
        } catch ( Exception error ) {
            fail( "Database connection could not be closed" );
        }
    }

    @Test
    void setInstructorPrerequisites() {
        // False due to invalid input parameters
        assertFalse( facultyDAO.setInstructorPrerequisites( null, "CS3003", new String[][]{}, new int[]{ 2023, 2} ) );
        assertFalse( facultyDAO.setInstructorPrerequisites( "CS", null, new String[][]{}, new int[]{ 2023, 2} ) );
        assertFalse( facultyDAO.setInstructorPrerequisites( "CS", "CS3003", null, new int[]{ 2023, 2} ) );
        assertFalse( facultyDAO.setInstructorPrerequisites( "CS", "CS3003", new String[][]{}, null ) );

    }

    @Test
    void dropCourseOffering() {
    }

    @Test
    void checkIfOfferedBySelf() {
    }

    @Test
    void setCourseCategory() {
    }

    @Test
    void verifyCore() {
    }

    @Test
    void getGradesOfCourse() {
    }

    @Test
    void getCourseEnrollmentsList() {
    }

    @Test
    void getListOfStudents() {
    }

    @Test
    void isCurrentEventOffering() {
    }

    @Test
    void isCurrentEventGradeSubmission() {
    }

    @Test
    void uploadCourseGrades() {
    }

    @Test
    void isCourseAlreadyOffered() {
    }
}