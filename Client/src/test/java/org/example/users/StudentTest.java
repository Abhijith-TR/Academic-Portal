package org.example.users;

import org.example.dal.PostgresStudentDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        student = new Student("2020csb1062", postgresStudentDAO);
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
        student.getGrades();
    }

    @Test
    void testGetGrades() {
        student.getGrades(2023, 1);
        student.getGrades(2020, 1);
    }
}