package org.example.users;

import org.example.dal.PostgresStudentDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StudentTest {
    Student            student;
    PostgresStudentDAO postgresStudentDAO;
    @BeforeEach
    void setUp() {
        postgresStudentDAO = new PostgresStudentDAO(
                "jdbc:postgresql://localhost:5432/mini_project",
                "postgres",
                "admin"
        );
        student = new Student("2020CSB1062", postgresStudentDAO);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void updateProfile() {
    }

    @Test
    void enroll() {
    }

    @Test
    void drop() {
    }

    @Test
    void getGrades() {
        student.getGradesForCourse();
    }

    @Test
    void testGetGrades() {
        student.getGrades(2023, 1);
        student.getGrades(2020, 1);
    }

    @Test
    void testUpdateProfile() {
    }

    @Test
    void testEnroll() {
    }

    @Test
    void testDrop() {
    }

    @Test
    void getGradesForCourse() {
        student.getGradesForCourse();
    }

    @Test
    void getBatch() {
    }

    @Test
    void computeSGPA() {
    }

    @Test
    void getCGPA() {
    }

    @Test
    void getAvailableCourses() {
    }

    @Test
    void getRemainingCreditRequirements() {
        student.getRemainingCreditRequirements();
    }
}