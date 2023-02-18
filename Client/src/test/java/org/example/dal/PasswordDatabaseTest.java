package org.example.dal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PasswordDatabaseTest {
    PasswordDatabase   passwordDatabase;
    PostgresStudentDAO student;

    @BeforeEach
    void setUp() {
        passwordDatabase = new PasswordDatabase(
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
    }

    @Test
    void updatePhoneNumber() {
    }
}