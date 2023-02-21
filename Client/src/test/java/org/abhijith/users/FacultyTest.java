package org.abhijith.users;

import org.abhijith.dal.PostgresAdminDAO;
import org.abhijith.dal.PostgresStudentDAO;
import org.abhijith.daoInterfaces.AdminDAO;
import org.abhijith.daoInterfaces.StudentDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FacultyTest {
    Faculty    faculty;
    StudentDAO studentDAO;
    AdminDAO   adminDAO;

    @BeforeEach
    void setUp() {
        adminDAO = new PostgresAdminDAO(
                "jdbc:postgresql://localhost:5432/mini_project",
                "postgres",
                "admin"
        );
        studentDAO = new PostgresStudentDAO(
                "jdbc:postgresql://localhost:5432/mini_project",
                "postgres",
                "admin"
        );
        faculty = new Faculty( "FAC38" );
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void offerCourse() {
        // Fails because of null
        assertFalse( faculty.offerCourse( null, "CS" ) );
        assertFalse( faculty.offerCourse( "CS301", null ) );

        // Fails because the course code does not exist
        assertFalse( faculty.offerCourse( "AAAAA", "CS" ) );
        // Fails because it is not the offering event
        assertFalse( faculty.offerCourse( "CS101", "CS" ) );

        // Course is offered successfully
        adminDAO.setSessionEvent( "OFFERING", 2023, 2 );
        assertTrue( faculty.offerCourse( "CS101", "CS" ) );
        faculty.dropCourseOffering( "CS101", "CS" );
        adminDAO.setSessionEvent( "RUNNING", 2023, 2 );
    }

    @Test
    void setCGAndPrerequisites() {
        // First 3 fail due to null arguments
        assertFalse( faculty.setCGAndPrerequisites( null, "CS", 0.0, new String[][]{} ) );
        assertFalse( faculty.setCGAndPrerequisites( "CS101", "CS", -1, new String[][]{} ) );
        assertFalse( faculty.setCGAndPrerequisites( "CS101", "CS", 0.0, null ) );
        assertFalse( faculty.setCGAndPrerequisites( "CS101", null, 0.0, new String[][]{} ) );

        // Fails because it is not the offering event right now
        assertFalse( faculty.setCGAndPrerequisites( "CS101", "CS", 8.0, new String[][]{} ) );

        adminDAO.setSessionEvent( "OFFERING", 2023, 2 );

        // Fails because the course has not been offered by the instructor
        assertFalse( faculty.setCGAndPrerequisites( "CS101", "CS", 8.0, new String[][]{} ) );

        // Successfully executed
        assertTrue( faculty.setCGAndPrerequisites( "HS507", "HS", 8.0, new String[][]{} ) );
        assertTrue( faculty.setCGAndPrerequisites( "HS507", "HS", 9.0, new String[][]{ { "CS101", "8" } } ) );

        faculty.dropCourseOffering( "HS507", "HS" );
        faculty.offerCourse( "HS507", "HS" );
        adminDAO.setSessionEvent( "RUNNING", 2023, 2 );
    }

    @Test
    void dropCourseOffering() {
        // Fails because of null
        assertFalse( faculty.dropCourseOffering( null, "CS" ) );
        assertFalse( faculty.dropCourseOffering( "CS539", null ) );

        // Fails because it is not the offering event right now
        assertFalse( faculty.dropCourseOffering( "HS507", "HS" ) );

        adminDAO.setSessionEvent( "OFFERING", 2023, 2 );
        // Fails because no such course has been offered by the instructor
        assertFalse( faculty.dropCourseOffering( "CS539", "CS" ) );

        // Successfully executed
        assertTrue( faculty.dropCourseOffering( "HS507", "HS" ) );

        faculty.offerCourse( "HS507", "HS" );
        adminDAO.setSessionEvent( "RUNNING", 2023, 2 );
    }

    @Test
    void setCourseCategory() {
        // Failures due to null
        assertFalse( faculty.setCourseCategory( null, "HS", "HE", "HS", new int[]{ 2021 } ) );
        assertFalse( faculty.setCourseCategory( "HS507", "HS", null, "HS", new int[]{ 2021 } ) );
        assertFalse( faculty.setCourseCategory( "HS507", "HS", "HE", null, new int[]{ 2021 } ) );
        assertFalse( faculty.setCourseCategory( "HS507", "HS", "HE", "HS", null ) );
        assertFalse( faculty.setCourseCategory( "HS507", null, "HE", "HS", new int[]{ 2021 } ) );

        // Fails because it is not the offering event
        assertFalse( faculty.setCourseCategory( "HS507", "HS", "HE", "CS", new int[]{ 2021 } ) );

        adminDAO.setSessionEvent( "OFFERING", 2023, 2 );
        // Fails because this has not been offered by the instructor
        assertFalse( faculty.setCourseCategory( "CS539", "HS", "PE", "CS", new int[]{ 2021 } ) );

        // Fails because this particular course is not program core and hence cannot be made offered as program core
        assertFalse( faculty.setCourseCategory( "HS507", "HS", "PC", "CS", new int[]{ 2021 } ) );

        // Successful as all parts of the query are valid
        faculty.offerCourse( "HS301", "HS" );
        assertTrue( faculty.setCourseCategory( "HS301", "HS", "PC", "CS", new int[]{ 2020 } ) );
        faculty.dropCourseOffering( "HS301", "HS" );
        adminDAO.setSessionEvent( "RUNNING", 2023, 2 );
    }

    @Test
    void getGradesOfStudent() {
        assertArrayEquals( new String[][][]{ {}, {}, {}, {}, {}, {} }, faculty.getGradesOfStudent( "2021CSB1062" ) );
    }

    @Test
    void getGradesOfOffering() {
        assertArrayEquals( new String[][]{}, faculty.getGradesOfOffering( "HS507", 2023, 2, "HS" ) );
        assertArrayEquals( new String[][]{ { "2020CSB1062", "A" } }, faculty.getGradesOfOffering( "CS101", 2020, 1, "CS" ) );
    }

    @Test
    void generateGradeCSV() {
        // Fails because it is not his own course
        assertFalse( faculty.generateGradeCSV( "CS539", 2023, 2, "CS" ) );
    }

    @Test
    void uploadGrades() {
    }
}