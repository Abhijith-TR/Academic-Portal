package org.abhijith.dal;

import org.abhijith.daoInterfaces.AdminDAO;
import org.abhijith.daoInterfaces.StudentDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class PostgresFacultyDAOTest {
    PostgresFacultyDAO facultyDAO;
    StudentDAO studentDAO = new PostgresStudentDAO(
            "jdbc:postgresql://localhost:5432/mini_project",
            "postgres",
            "admin"
    );
    AdminDAO           adminDAO = new PostgresAdminDAO(
            "jdbc:postgresql://localhost:5432/mini_project",
            "postgres",
            "admin"
    );

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
        assertFalse( facultyDAO.insertCourseOffering( "HS507", -1, 2, "HS", "FAC1" ) );
        assertFalse( facultyDAO.insertCourseOffering( "HS507", 2023, 0, "HS", "FAC1" ) );
        assertFalse( facultyDAO.insertCourseOffering( "HS507", 2023, 2, null, "FAC1" ) );
        assertFalse( facultyDAO.insertCourseOffering( "HS507", 2023, 2, "HS", null ) );

        // False because the course already exists
        assertFalse( facultyDAO.insertCourseOffering( "HS507", 2023, 2, "HS", "ADMIN1" ) );

        // Successful because the course does not exist
        assertTrue( facultyDAO.insertCourseOffering( "HS301", 2023, 2, "HS", "FAC38" ) );
        facultyDAO.dropCourseOffering( "FAC38", "HS301", 2023, 2, "HS" );
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
        assertFalse( facultyDAO.setCGCriteria( "FAC38", "HS507", 8, new int[]{ -1, 2 }, "HS" ) );
        assertFalse( facultyDAO.setCGCriteria( "FAC38", "HS507", 8, new int[]{ 2023, 0 }, "HS" ) );

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
        assertFalse( facultyDAO.setInstructorPrerequisites( null, "CS3003", new String[][]{}, new int[]{ 2023, 2 } ) );
        assertFalse( facultyDAO.setInstructorPrerequisites( "CS", null, new String[][]{}, new int[]{ 2023, 2 } ) );
        assertFalse( facultyDAO.setInstructorPrerequisites( "CS", "CS3003", null, new int[]{ 2023, 2 } ) );
        assertFalse( facultyDAO.setInstructorPrerequisites( "CS", "CS3003", new String[][]{}, null ) );
        assertFalse( facultyDAO.setInstructorPrerequisites( "CS", "CS3003", new String[][]{}, new int[]{ -1, 2 } ) );
        assertFalse( facultyDAO.setInstructorPrerequisites( "CS", "CS3003", new String[][]{}, new int[]{ 2023, 0 } ) );
        assertFalse( facultyDAO.setInstructorPrerequisites( "CS", "CS3003", new String[][]{}, new int[]{ 2023 } ) );

        // Fails because of the odd length prerequisites array
        assertFalse( facultyDAO.setInstructorPrerequisites( "HS", "HS507", new String[][]{ { "CS101", "8", "7" } }, new int[]{ 2023, 2 } ) );

        // Returns true as all input arguments obey constraints
        assertTrue( facultyDAO.setInstructorPrerequisites( "HS", "HS507", new String[][]{ { "CS101", "8" } }, new int[]{ 2023, 2 } ) );

        facultyDAO.setInstructorPrerequisites( "HS", "HS507", new String[][]{}, new int[]{ 2023, 2 } );

        // False as the database connection is closed and the records cannot be inserted
        try {
            Connection databaseConnection = facultyDAO.getDatabaseConnection();
            databaseConnection.close();
            assertFalse( facultyDAO.setInstructorPrerequisites( "HS", "HS507", new String[][]{}, new int[]{ 2023, 2 } ) );
        } catch ( Exception error ) {
            fail( "Database connection could not be closed" );
        }
    }

    @Test
    void dropCourseOffering() {
        // False because of invalid input parameters
        assertFalse( facultyDAO.dropCourseOffering( null, "HS507", 2023, 2, "HS" ) );
        assertFalse( facultyDAO.dropCourseOffering( "FAC38", null, 2023, 2, "HS" ) );
        assertFalse( facultyDAO.dropCourseOffering( "FAC38", "HS507", 0, 2, "HS" ) );
        assertFalse( facultyDAO.dropCourseOffering( "FAC38", "HS507", 2023, 0, "HS" ) );
        assertFalse( facultyDAO.dropCourseOffering( "FAC38", "HS507", 2023, 2, null ) );

        // False because this course was not offered by this faculty and therefore nothing was dropped
        assertFalse( facultyDAO.dropCourseOffering( "FAC38", "CS303", 2023, 2, "HS" ) );

        // True because this course was offered by this instructor
        assertTrue( facultyDAO.dropCourseOffering( "FAC38", "HS507", 2023, 2, "HS" ) );
        facultyDAO.insertCourseOffering( "HS507", 2023, 2, "HS", "FAC38" );

        try {
            Connection databaseConnection = facultyDAO.getDatabaseConnection();
            databaseConnection.close();
            assertFalse( facultyDAO.dropCourseOffering( "FAC38", "HS507", 2023, 2, "HS" ) );
        } catch ( Exception error ) {
            fail( "Could not disconnect from database" );
        }
    }

    @Test
    void checkIfOfferedBySelf() {
        // False because of invalid input arguments
        assertFalse( facultyDAO.checkIfOfferedBySelf( null, "HS507", 2023, 2, "HS" ) );
        assertFalse( facultyDAO.checkIfOfferedBySelf( "FAC38", null, 2023, 2, "HS" ) );
        assertFalse( facultyDAO.checkIfOfferedBySelf( "FAC38", "HS507", 0, 2, "HS" ) );
        assertFalse( facultyDAO.checkIfOfferedBySelf( "FAC38", "HS507", 2023, 0, "HS" ) );
        assertFalse( facultyDAO.checkIfOfferedBySelf( "FAC38", "HS507", 2023, 0, null ) );

        // False because this course is not offered by the instructor
        assertFalse( facultyDAO.checkIfOfferedBySelf( "FAC38", "HS301", 2023, 2, "HS" ) );

        // True because this course is offered by this instructor in this semester
        assertTrue( facultyDAO.checkIfOfferedBySelf( "FAC38", "HS507", 2023, 2, "HS" ) );

        try {
            Connection databaseConnection = facultyDAO.getDatabaseConnection();
            databaseConnection.close();
            assertFalse( facultyDAO.checkIfOfferedBySelf( "FAC38", "HS507", 2023, 2, "HS" ) );
        } catch ( Exception error ) {
            fail( "Could not close database connection" );
        }
    }

    @Test
    void setCourseCategory() {
        // False because of invalid input arguments
        assertFalse( facultyDAO.setCourseCategory( null, 2023, 2, "PE", "CS", new int[]{ 2021 }, "HS" ) );
        assertFalse( facultyDAO.setCourseCategory( "HS507", -1, 2, "PE", "CS", new int[]{ 2021 }, "HS" ) );
        assertFalse( facultyDAO.setCourseCategory( "HS507", 2023, 0, "PE", "CS", new int[]{ 2021 }, "HS" ) );
        assertFalse( facultyDAO.setCourseCategory( "HS507", 2023, 2, null, "CS", new int[]{ 2021 }, "HS" ) );
        assertFalse( facultyDAO.setCourseCategory( "HS507", 2023, 2, "PE", null, new int[]{ 2021 }, "HS" ) );
        assertFalse( facultyDAO.setCourseCategory( "HS507", 2023, 2, "PE", "CS", null, "HS" ) );
        assertFalse( facultyDAO.setCourseCategory( "HS507", 2023, 2, "PE", "CS", new int[]{ -1 }, "HS" ) );

        // True because it is a successful insertion
        assertTrue( facultyDAO.setCourseCategory( "HS507", 2023, 2, "HE", "CS", new int[]{ 2021 }, "HS" ) );

        // False because course has already been offered for 2021-CS and the exception is triggered
        assertFalse( facultyDAO.setCourseCategory( "HS507", 2023, 2, "HE", "CS", new int[]{ 2021 }, "HS" ) );

        facultyDAO.dropCourseOffering( "FAC38", "HS507", 2023, 2, "HS" );
        facultyDAO.insertCourseOffering( "HS507", 2023, 2, "HS", "FAC38" );
    }

    @Test
    void verifyCore() {
        // False because of invalid input arguments
        assertFalse( facultyDAO.verifyCore( null, "CS", 2022 ) );
        assertFalse( facultyDAO.verifyCore( "HS507", null, 2022 ) );
        assertFalse( facultyDAO.verifyCore( "HS507", "CS", -1 ) );

        // False because the course is not a core course
        assertFalse( facultyDAO.verifyCore( "HS507", "CS", 2020 ) );

        // True because the course is a core course
        assertTrue( facultyDAO.verifyCore( "CS301", "CS", 2020 ) );

        try {
            Connection databaseConnection = facultyDAO.getDatabaseConnection();
            databaseConnection.close();
            assertFalse( facultyDAO.verifyCore( "CS301", "CS", 2020 ) );
        } catch ( Exception error ) {
            fail( "Could not close database connection" );
        }
    }

    @Test
    void getGradesOfCourse() {
        // [][] because of invalid input arguments
        assertArrayEquals( new String[][]{}, facultyDAO.getGradesOfCourse( null, 2022, 2, "HS" ) );
        assertArrayEquals( new String[][]{}, facultyDAO.getGradesOfCourse( "CS301", -1, 2, "HS" ) );
        assertArrayEquals( new String[][]{}, facultyDAO.getGradesOfCourse( "CS301", 2022, 0, "HS" ) );
        assertArrayEquals( new String[][]{}, facultyDAO.getGradesOfCourse( "CS301", 2022, 2, null ) );

        // [][] because no such course exists
        assertArrayEquals( new String[][]{}, facultyDAO.getGradesOfCourse( "CS301", 2022, 2, "HS" ) );

        // The expected value from the test database
        assertArrayEquals( new String[][]{ { "2020CSB1062", "A" } }, facultyDAO.getGradesOfCourse( "CS539", 2023, 2, "CS" ) );

        try {
            Connection databaseConnection = facultyDAO.getDatabaseConnection();
            databaseConnection.close();
            assertArrayEquals( new String[][]{}, facultyDAO.getGradesOfCourse( "CS539", 2023, 2, "CS" ) );
        } catch ( Exception error ) {
            fail( "Could not close database connection" );
        }
    }

    @Test
    void getCourseEnrollmentsList() {
        // [][] because of invalid input arguments
        assertArrayEquals( new String[][]{}, facultyDAO.getCourseEnrollmentsList( null, 2023, 2, "CS" ) );
        assertArrayEquals( new String[][]{}, facultyDAO.getCourseEnrollmentsList( "CS539", -1, 2, "CS" ) );
        assertArrayEquals( new String[][]{}, facultyDAO.getCourseEnrollmentsList( "CS539", 2023, -1, "CS" ) );
        assertArrayEquals( new String[][]{}, facultyDAO.getCourseEnrollmentsList( "CS539", 2023, 2, null ) );

        // [][] because no such offering exists
        assertArrayEquals( new String[][]{}, facultyDAO.getCourseEnrollmentsList( "CS539", 2023, 2, "HS" ) );

        // Gets the correct entries from the database
        assertArrayEquals( new String[][]{ { "ABHIJITH T R", "2020CSB1062" } }, facultyDAO.getCourseEnrollmentsList( "CS539", 2023, 2, "CS" ) );

        try {
            Connection databaseConnection = facultyDAO.getDatabaseConnection();
            databaseConnection.close();
            assertArrayEquals( new String[][]{}, facultyDAO.getCourseEnrollmentsList( "CS539", 2023, 2, "CS" ) );
        } catch ( Exception error ) {
            fail( "Could not close database connection" );
        }
    }

    @Test
    void getListOfStudents() {
        // [] because of invalid input arguments
        assertArrayEquals( new String[]{}, facultyDAO.getListOfStudents( null, 2023, 2, "HS" ) );
        assertArrayEquals( new String[]{}, facultyDAO.getListOfStudents( "CS539", -1, 2, "HS" ) );
        assertArrayEquals( new String[]{}, facultyDAO.getListOfStudents( "CS539", 2023, -1, "HS" ) );
        assertArrayEquals( new String[]{}, facultyDAO.getListOfStudents( "CS539", 2023, 2, null ) );

        // [] because no such course has been offered
        assertArrayEquals( new String[]{}, facultyDAO.getListOfStudents( "CS539", 2023, 2, "HS" ) );

        // Returns the corresponding student
        assertArrayEquals( new String[]{ "2020CSB1062" }, facultyDAO.getListOfStudents( "CS539", 2023, 2, "CS" ) );

        try {
            Connection databaseConnection = facultyDAO.getDatabaseConnection();
            databaseConnection.close();
            assertArrayEquals( new String[]{}, facultyDAO.getListOfStudents( "CS539", 2023, 2, "CS" ) );
        } catch ( Exception error ) {
            fail( "Could not close database connection" );
        }
    }

    @Test
    void isCurrentEventOffering() {
        // False due to invalid input arguments
        assertFalse( facultyDAO.isCurrentEventOffering( -1, 2 ) );
        assertFalse( facultyDAO.isCurrentEventOffering( 2023, -1 ) );

        // False because the current event is not offering
        assertFalse( facultyDAO.isCurrentEventOffering( 2023, 2 ) );

        adminDAO.setSessionEvent( "OFFERING", 2023, 2 );
        // True because the event has been set
        assertTrue( facultyDAO.isCurrentEventOffering( 2023, 2 ) );

        try {
            Connection databaseConnection = facultyDAO.getDatabaseConnection();
            databaseConnection.close();
            assertFalse( facultyDAO.isCurrentEventOffering( 2023, 2 ) );
        } catch ( Exception error ) {
            fail( "Could not close database connection" );
        }
        adminDAO.setSessionEvent( "RUNNING", 2023, 2 );
    }

    @Test
    void isCurrentEventGradeSubmission() {
        // False due to invalid input arguments
        assertFalse( facultyDAO.isCurrentEventGradeSubmission( -1, 2 ) );
        assertFalse( facultyDAO.isCurrentEventGradeSubmission( 2023, -1 ) );

        // False because the current event is not offering
        assertFalse( facultyDAO.isCurrentEventGradeSubmission( 2023, 2 ) );

        adminDAO.setSessionEvent( "GRADE SUBMISSION", 2023, 2 );
        // True because the event has been set
        assertTrue( facultyDAO.isCurrentEventGradeSubmission( 2023, 2 ) );

        try {
            Connection databaseConnection = facultyDAO.getDatabaseConnection();
            databaseConnection.close();
            assertFalse( facultyDAO.isCurrentEventGradeSubmission( 2023, 2 ) );
        } catch ( Exception error ) {
            fail( "Could not close database connection" );
        }
        adminDAO.setSessionEvent( "RUNNING", 2023, 2 );
    }

    @Test
    void uploadCourseGrades() {
        // False due to invalid input parameters
        assertFalse( facultyDAO.uploadCourseGrades( null, 2022, 2, "CS", new String[]{}, new String[]{} ) );
        assertFalse( facultyDAO.uploadCourseGrades( "HS301", -1, 2, "CS", new String[]{}, new String[]{} ) );
        assertFalse( facultyDAO.uploadCourseGrades( "HS301", 2022, -1, "CS", new String[]{}, new String[]{} ) );
        assertFalse( facultyDAO.uploadCourseGrades( "HS301", 2022, 2, null, new String[]{}, new String[]{} ) );
        assertFalse( facultyDAO.uploadCourseGrades( "HS301", 2022, 2, "CS", null, new String[]{} ) );
        assertFalse( facultyDAO.uploadCourseGrades( "HS301", 2022, 2, "CS", new String[]{}, null ) );
        assertFalse( facultyDAO.uploadCourseGrades( "HS301", 2022, 2, "CS", new String[]{}, new String[]{ "A" } ) );

        // False because the string arrays contain null
        assertFalse( facultyDAO.uploadCourseGrades( "HS301", 2022, 2, "CS", new String[]{ "Abhijith " }, new String[]{ null } ) );
        assertFalse( facultyDAO.uploadCourseGrades( "HS301", 2022, 2, "CS", new String[]{ null }, new String[]{ "A" } ) );

        // Successful as the entry is completely valid
        facultyDAO.setCourseCategory( "HS507", 2023, 2, "HE", "CS", new int[]{ 2020 }, "HS" );
        studentDAO.enroll( "HS507", "2020CSB1062", 2023, 2, "HS", "HE" );
        assertTrue( facultyDAO.uploadCourseGrades( "HS507", 2023, 2, "HS", new String[]{ "2020CSB1062" }, new String[]{ "A" } ));
        studentDAO.dropCourse( "HS507", "2020CSB1062", 2023, 2 );
        facultyDAO.dropCourseOffering( "FAC38", "HS507", 2023, 2, "HS" );
        facultyDAO.insertCourseOffering( "HS507", 2023, 2, "HS", "FAC38" );

        try {
            Connection databaseConnection = facultyDAO.getDatabaseConnection();
            databaseConnection.close();
            assertFalse( facultyDAO.uploadCourseGrades( "HS507", 2023, 2, "HS", new String[]{ "2020CSB1062" }, new String[]{ "A" } ));
        } catch ( Exception error ) {
            fail( "Could not close database connection" );
        }
    }

    @Test
    void isCourseAlreadyOffered() {
        // True because of invalid input arguments
        assertTrue( facultyDAO.isCourseAlreadyOffered( null, 2023, 2, "HS" ) );
        assertTrue( facultyDAO.isCourseAlreadyOffered( "HS507", -1, 2, "HS" ) );
        assertTrue( facultyDAO.isCourseAlreadyOffered( "HS507", 2023, -1, "HS" ) );
        assertTrue( facultyDAO.isCourseAlreadyOffered( "HS507", 2023, 2, null ) );

        // True because the course has already been offered by your department
        assertTrue( facultyDAO.isCourseAlreadyOffered( "HS507", 2023, 2, "HS" ));

        // False because the course has not been offered by your department
        assertFalse( facultyDAO.isCourseAlreadyOffered( "HS301", 2023, 2, "HS" ));

        try {
            Connection databaseConnection = facultyDAO.getDatabaseConnection();
            databaseConnection.close();
            assertTrue( facultyDAO.isCourseAlreadyOffered( "HS301", 2023, 2, "HS" ));
        } catch ( Exception error ) {
            fail( "Could not close connection to database" );
        }
    }
}