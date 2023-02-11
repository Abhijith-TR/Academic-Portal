package org.example.users;

import org.example.dal.StudentDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StudentTest {
    Student student;
    StudentDAO studentDAO;
    @BeforeEach
    void setUp() {
        studentDAO = new StudentDAO(
                "jdbc:postgresql://localhost:5432/mini_project",
                "postgres",
                "admin"
        );
        student = new Student("2020csb1062", studentDAO);
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