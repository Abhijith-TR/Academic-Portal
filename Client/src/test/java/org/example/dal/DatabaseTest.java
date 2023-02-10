package org.example.dal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {
    Database database;
    StudentDAO student;

    @BeforeEach
    void setUp() {
        database = new Database(
                "jdbc:postgresql://localhost:5432/mini_project",
                "postgres",
                "admin"
        );
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
    void authenticateUser() {
    }

    @Test
    void getCurrentAcademicSession() {
        assertArrayEquals(new int[]{2020, 1}, student.getCurrentAcademicSession());
        assertFalse(Arrays.equals(new int[]{2022, 2}, student.getCurrentAcademicSession()));
    }

    @Test
    void checkCourseOffering() {
        assertTrue(student.checkCourseOffering("CS101"));
        assertFalse(student.checkCourseOffering("HS101"));
        assertFalse(student.checkCourseOffering("CS888"));
    }

    @Test
    void updatePhoneNumber() {
    }
}