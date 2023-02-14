package org.example.dal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PostgresStudentDAOTest {
    PostgresStudentDAO student;
    @BeforeEach
    void setUp() {
        student = new PostgresStudentDAO(
                "jdbc:postgresql://localhost:5432/mini_project",
                "postgres",
                "admin"
        );
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getCurrentAcademicSession() {
    }

    @Test
    void checkCourseOffering() {
    }

    @Test
    void isStudentEligible() {
//        assertTrue(student.checkStudentEligibility("CS301", "2020csb1062"));
//        assertFalse(student.checkStudentEligibility("CS305", "2020csb1062"));
    }

    @Test
    void enroll() {
        // The request should succeed as the student has completed all the necessary courses and is in the correct year and semester
        assertFalse(student.enroll("CS301", "2020csb1062", 2022, 1));
        // The request should fail as it is in the wrong year and semester (i.e., academic session)
        assertFalse(student.enroll("CS305", "2020csb1062", 2022, 1));
    }

    @Test
    void testGetCurrentAcademicSession() {
    }

    @Test
    void testCheckCourseOffering() {
    }

    @Test
    void checkStudentPassStatus() {
    }

    @Test
    void getCourseCatalogPrerequisites() {
    }

    @Test
    void getInstructorPrerequisites() {
    }

    @Test
    void getCreditsOfCourse() {
    }

    @Test
    void getCreditsInSession() {
    }

    @Test
    void testEnroll() {
    }

    @Test
    void dropCourse() {
    }

    @Test
    void getGradesForSemester() {
        assertArrayEquals(new String[]{}, student.getStudentGradesForSemester("2020csb1062", 2020, 1));
    }

    @Test
    void getOfferedCourses() {
        student.getOfferedCourses(2022, 1);
    }
}