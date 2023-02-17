package org.example.dal;

import org.example.daoInterfaces.AdminDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PostgresAdminDAOTest {
    AdminDAO admin;

    @Test
    void getGradesOfCourse() {
    }

    @BeforeEach
    void setUp() {
        admin = new PostgresAdminDAO(
                "jdbc:postgresql://localhost:5432/mini_project",
                "postgres",
                "admin"
        );
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testGetGradesOfCourse() {
        admin.getGradesOfCourse("CS101", 2020, 1);
    }

    @Test
    void checkAllPrerequisites() {
        assertTrue(admin.checkAllPrerequisites(new String[]{"CS550"}));
        assertFalse(admin.checkAllPrerequisites(new String[]{"CS555"}));
//        admin.checkAllPrerequisites(new String[]{"CS555"});
    }

    @Test
    void dropCourseFromCatalog() {
        assertTrue(admin.dropCourseFromCatalog("CS403"));
    }

    @Test
    void insertCourse() {
        assertTrue(admin.insertCourse("CS403", "ADVANCED OS", new double[]{4, 1, 0, 5, 3}, new String[]{"CS550"}, "CS"));
    }

    @Test
    void insertCoreCourse() {
    }
}