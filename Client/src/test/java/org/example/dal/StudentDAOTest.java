package org.example.dal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StudentDAOTest {
    StudentDAO student;
    @BeforeEach
    void setUp() {
        student = new StudentDAO(
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
        assertTrue(student.checkStudentEligibility("CS301", "2020csb1062"));
    }

    @Test
    void enroll() {
    }
}