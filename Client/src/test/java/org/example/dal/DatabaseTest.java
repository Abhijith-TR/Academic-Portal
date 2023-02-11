package org.example.dal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {
    Database           database;
    PostgresStudentDAO student;

    @BeforeEach
    void setUp() {
        database = new Database(
                "jdbc:postgresql://localhost:5432/mini_project",
                "postgres",
                "admin"
        );
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
    void authenticateUser() {
    }

    @Test
    void getCurrentAcademicSession() {
        assertArrayEquals(new int[]{2020, 1}, student.getCurrentAcademicSession());
        assertFalse(Arrays.equals(new int[]{2022, 2}, student.getCurrentAcademicSession()));
    }

    @Test
    void checkCourseOffering() {
        // Course exists and is in the correct session
        assertTrue(student.checkCourseOffering("CS101", 2020, 1));
        // Course exists but is in the wrong session
        assertFalse(student.checkCourseOffering("HS101", 2020, 1));
        // Course does not exist
        assertFalse(student.checkCourseOffering("CS888", 2022, 1));
    }

    @Test
    void updatePhoneNumber() {
    }
}