package org.abhijith.users;

import org.abhijith.dal.PostgresAdminDAO;
import org.abhijith.dal.PostgresFacultyDAO;
import org.abhijith.daoInterfaces.AdminDAO;
import org.abhijith.daoInterfaces.FacultyDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class StudentTest {
    Student    student;
    Student    student2;
    AdminDAO   adminDAO;
    FacultyDAO facultyDAO;

    @BeforeEach
    void setUp() {
        adminDAO = new PostgresAdminDAO(
                "jdbc:postgresql://localhost:5432/mini_project",
                "postgres",
                "admin"
        );
        facultyDAO = new PostgresFacultyDAO(
                "jdbc:postgresql://localhost:5432/mini_project",
                "postgres",
                "admin"
        );
        student = new Student( "2021CSB1062" );
        student2 = new Student( "2020CSB1062" );
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void enroll() {
        // Tests that fail at the first statement due to null values
        assertFalse( student.enroll( null, "CS" ) );
        assertFalse( student.enroll( "HS507", null ) );

        // Fails due it currently being running and student trying to enroll
        adminDAO.setSessionEvent( "RUNNING", 2023, 2 );
        assertFalse( student.enroll( "HS507", "HS" ) );

        // Fails because the course has not been offered in this session
        adminDAO.setSessionEvent( "ENROLLING", 2023, 2 );
        assertFalse( student.enroll( "CS101", "CS" ) );

        // Fails because the course catalog prerequisite is not met
        assertFalse( student.enroll( "CP303", "CS" ) );

        // Fails because of instructor prerequisites
        assertFalse( student.enroll( "CS539", "CS" ) );

        // Fails because of the CGPA criteria
        facultyDAO.setCGCriteria( "FAC38", "HS507", 10, new int[]{ 2023, 2 }, "HS" );
        assertFalse( student.enroll( "HS507", "HS" ) );
        facultyDAO.setCGCriteria( "FAC38", "HS507", 0, new int[]{ 2023, 2 }, "HS" );

        // Fails because of the credit limit
        assertFalse( student.enroll( "CS999", "CS" ) );

        // Fails because of wrong department
        assertFalse( student.enroll( "HS507", "CS" ) );

        // Fails because it has not been offered for this department
        assertFalse( student.enroll( "HS507", "HS" ) );

        // Enrollment is successful
        facultyDAO.setCourseCategory( "HS507", 2023, 2, "HE", "CS", new int[]{ 2021 }, "HS" );
        assertTrue( student.enroll( "HS507", "HS" ) );

        // Fails because the student is already enrolled in this course
        assertFalse( student.enroll( "HS507", "HS" ) );
        student.drop( "HS507" );
        facultyDAO.dropCourseOffering( "FAC38", "HS507", 2023, 2, "HS" );
        facultyDAO.insertCourseOffering( "HS507", 2023, 2, "HS", "FAC38" );
        adminDAO.setSessionEvent( "RUNNING", 2023, 2 );
    }

    @Test
    void drop() {
        // Fails because of the null argument
        assertFalse( student.drop( null ) );

        // Fails because courses can only be dropped during the enrolling event
        adminDAO.setSessionEvent( "OFFERING", 2023, 2 );
        assertFalse( student.drop( "HS507" ) );

        // Drop is successful
        adminDAO.setSessionEvent( "ENROLLING", 2023, 2 );
        facultyDAO.setCourseCategory( "HS507", 2023, 2, "HE", "CS", new int[]{ 2021 }, "HS" );
        student.enroll( "HS507", "HS" );
        assertTrue( student.drop( "HS507" ) );
        facultyDAO.dropCourseOffering( "FAC38", "HS507", 2023, 2, "HS");
        facultyDAO.insertCourseOffering( "HS507", 2023, 2, "HS", "FAC38" );
        adminDAO.setSessionEvent( "RUNNING", 2023, 2 );
    }

    @Test
    void getGradesForCourse() {
        // Happens when no records are found for a student
        assertArrayEquals( new String[][][]{ {}, {}, {}, {}, {}, {} }, student.getGradesForCourse() );

        // Student has one enrollment
        adminDAO.setSessionEvent( "ENROLLING", 2023, 2 );
        facultyDAO.setCourseCategory( "HS507", 2023, 2, "HE", "CS", new int[]{ 2021 }, "HS" );
        student.enroll( "HS507", "HS" );

        assertArrayEquals( new String[][][]{ {}, {}, {}, {}, {}, { { "HS507", "POSITIVE PSYCHOLOGY AND WELL-BEING", "-", "3.0" } } }, student.getGradesForCourse() );
        student.drop( "HS507" );
        facultyDAO.dropCourseOffering( "FAC38", "HS507", 2023, 2, "HS" );
        facultyDAO.insertCourseOffering( "HS507", 2023, 2, "HS", "FAC38" );
        adminDAO.setSessionEvent( "RUNNING", 2023, 2 );
    }

    @Test
    void getGrades() {
        // Both fail as 0 is an invalid value for year and semester
        assertArrayEquals( new String[][]{}, student.getGrades( 0, 1 ) );
        assertArrayEquals( new String[][]{}, student.getGrades( 1, 0 ) );

        // No record exists for the student as of now
        assertArrayEquals( new String[][]{}, student.getGrades( 2023, 2 ) );

        // This session has not yet occurred
        assertArrayEquals( new String[][]{}, student.getGrades( 2222, 2 ) );

        // Equal as the student has managed to enroll in the course
        adminDAO.setSessionEvent( "ENROLLING", 2023, 2 );
        facultyDAO.setCourseCategory( "HS507", 2023, 2, "HE", "CS", new int[]{ 2021 }, "HS" );
        student.enroll( "HS507", "HS" );
        assertArrayEquals( new String[][]{ { "HS507", "POSITIVE PSYCHOLOGY AND WELL-BEING", "-", "3.0" } }, student.getGrades( 2023, 2 ) );
        student.drop( "HS507" );
        facultyDAO.dropCourseOffering( "FAC38", "HS507", 2023, 2, "HS" );
        facultyDAO.insertCourseOffering( "HS507", 2023, 2, "HS", "FAC38" );
        adminDAO.setSessionEvent( "RUNNING", 2023, 2 );
    }

    @Test
    void getBatch() {
        // The student is of batch 2021
        assertEquals( 2021, student.getBatch() );
    }

    @Test
    void computeSGPA() {
        // 0 is returned if the input is invalid
        assertEquals( 0.0, student.getSGPA( null ) );
        assertEquals( 0.0, student.getSGPA( new String[][]{ null, null } ) );

        // 0 is returned if the records contain no actual entries
        assertEquals( 0.0, student.getSGPA( new String[][]{ { "", "", "" } } ) );
        assertEquals( 9.0, student.getSGPA( new String[][]{ { "", "", "A-", "4.0" } } ) );
    }

    @Test
    void getCGPA() {
        // Requires a null for testing
        assertEquals( 10.0, student2.getCGPA() );
        assertEquals( 0.0, student.getCGPA() );
    }

    @Test
    void getAvailableCourses() {
        String[] course1 = { "MA628", "FINANCIAL DERIVATIVES PRICING", "DR ARUN KUMAR", "{}", "MA", "" };
        String[] course2 = { "NS104", "NSS IV", "DR BALESH KUMAR", "{}", "MA", "" };
        String[] course3 = { "CS539", "INTERNET OF THINGS", "DR SUJATA PAL", "{}", "CS", "" };
        String[] course4 = { "CP303", "CAPSTONE II", "DR ABHINAV DHALL", "{CP302}", "CS", "" };
        String[] course5 = { "CS550", "RESEARCH METHODOLOGIES IN COMPUTER SCIENCE", "DR MUKESH SAINI", "{}", "CS", "" };
        String[] course6 = { "HS507", "POSITIVE PSYCHOLOGY AND WELL-BEING", "DR PARWINDER SINGH", "{}", "HS", "" };
        String[] course7 = { "CS999", "TEST COURSE", "TEST FACULTY", "{}", "CS", "" };
        assertArrayEquals( new String[][]{course1, course2, course3, course4, course5, course6, course7 }, student.getAvailableCourses() );
    }

    @Test
    void getRemainingCreditRequirements() {
        HashMap<String, Double> expectedResult = new HashMap<>();
        expectedResult.put( "SC", 0.0 );
        expectedResult.put( "SE", 0.0 );
        expectedResult.put( "GR", 0.0 );
        expectedResult.put( "PC", 0.0 );
        expectedResult.put( "PE", 0.0 );
        expectedResult.put( "HC", 0.0 );
        expectedResult.put( "HE", 3.0 );
        expectedResult.put( "NN", 0.0 );
        expectedResult.put( "II", 0.0 );
        expectedResult.put( "CP", 0.0 );
        expectedResult.put( "OE", 0.0 );

        assertTrue( expectedResult.equals( student2.getRemainingCreditRequirements() ));
    }
}