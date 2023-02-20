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
        assertFalse( faculty.offerCourse( null ) );
        // Fails because the course code does not exist
        assertFalse( faculty.offerCourse( "AAAAA" ) );
        // Fails because it is not the offering event
        assertFalse( faculty.offerCourse( "CS101" ) );

        // Course is offered successfully
        adminDAO.setSessionEvent( "OFFERING", 2023, 2 );
        assertTrue( faculty.offerCourse( "CS101" ) );
        faculty.dropCourseOffering( "CS101" );
        adminDAO.setSessionEvent( "RUNNING", 2023, 2 );
    }

    @Test
    void setCGAndPrerequisites() {
        // First 3 fail due to null arguments
        assertFalse( faculty.setCGAndPrerequisites( null, 0.0, new String[][]{} ) );
        assertFalse( faculty.setCGAndPrerequisites( "CS101", -1, new String[][]{} ) );
        assertFalse( faculty.setCGAndPrerequisites( "CS101", 0.0, null ) );

        // Fails because it is not the offering event right now
        assertFalse( faculty.setCGAndPrerequisites( "CS101", 8.0, new String[][]{} ) );

        adminDAO.setSessionEvent( "OFFERING", 2023, 2 );

        // Fails because the course has not been offered by the instructor
        assertFalse( faculty.setCGAndPrerequisites( "CS101", 8.0, new String[][]{} ) );

        // Successfully executed
        assertTrue( faculty.setCGAndPrerequisites( "HS507", 8.0, new String[][]{} ) );
        assertTrue( faculty.setCGAndPrerequisites( "HS507", 9.0, new String[][]{ { "CS101", "8" } } ) );

        faculty.dropCourseOffering( "HS507" );
        faculty.offerCourse( "HS507" );
        adminDAO.setSessionEvent( "RUNNING", 2023, 2 );
    }

    @Test
    void dropCourseOffering() {
        // Fails because of null
        assertFalse( faculty.dropCourseOffering( null ) );

        // Fails because it is not the offering event right now
        assertFalse( faculty.dropCourseOffering( "HS507" ) );

        adminDAO.setSessionEvent( "OFFERING", 2023, 2 );
        // Fails because no such course has been offered by the instructor
        assertFalse( faculty.dropCourseOffering( "CS539" ) );

        // Successfully executed
        assertTrue( faculty.dropCourseOffering( "HS507" ) );

        faculty.offerCourse( "HS507" );
        adminDAO.setSessionEvent( "RUNNING", 2023, 2 );
    }

    @Test
    void setCourseCategory() {
        // Failures due to null
        assertFalse( faculty.setCourseCategory( null, "HE", "HS", new int[]{ 2021 } ) );
        assertFalse( faculty.setCourseCategory( "HS507", null, "HS", new int[]{ 2021 } ) );
        assertFalse( faculty.setCourseCategory( "HS507", "HE", null, new int[]{ 2021 } ) );
        assertFalse( faculty.setCourseCategory( "HS507", "HE", "HS", null ) );

        // Fails because it is not the offering event
        assertFalse( faculty.setCourseCategory( "HS507", "HE", "CS", new int[]{ 2021 } ) );

        adminDAO.setSessionEvent( "OFFERING", 2023, 2 );
        // Fails because this has not been offered by the instructor
        assertFalse( faculty.setCourseCategory( "CS539", "PE", "CS", new int[]{ 2021 } ) );

        // Fails because this particular course is not program core and hence cannot be made offered as program core
        assertFalse( faculty.setCourseCategory( "HS507", "PC", "CS", new int[]{ 2021 } ) );

        // Successful as all parts of the query are valid
        faculty.offerCourse( "HS301" );
        assertTrue( faculty.setCourseCategory( "HS301", "PC", "CS", new int[]{ 2020 } ) );
        faculty.dropCourseOffering( "HS301" );
        adminDAO.setSessionEvent( "RUNNING", 2023, 2 );
    }

    @Test
    void getGradesOfStudent() {
        assertArrayEquals( new String[][][]{ {}, {}, {}, {}, {}, {} }, faculty.getGradesOfStudent( "2021CSB1062" ) );
    }

    @Test
    void getGradesOfOffering() {
        assertArrayEquals( new String[][]{}, faculty.getGradesOfOffering( "HS507", 2023, 2 ) );
        assertArrayEquals( new String[][]{ { "2020CSB1062", "A" } }, faculty.getGradesOfOffering( "CS101", 2020, 1 ) );
    }

    @Test
    void generateGradeCSV() {
        // Fails because it is not his own course
        assertFalse( faculty.generateGradeCSV("CS539", 2023, 2) );
    }

    @Test
    void uploadGrades() {
    }
}