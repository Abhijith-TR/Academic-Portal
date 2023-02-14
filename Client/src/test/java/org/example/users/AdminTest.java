package org.example.users;

import org.example.dal.PostgresAdminDAO;
import org.example.daoInterfaces.AdminDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static org.junit.jupiter.api.Assertions.*;

class AdminTest {
    Admin admin;
    @BeforeEach
    void setUp() {
        AdminDAO adminDAO = new PostgresAdminDAO(
                "jdbc:postgresql://localhost:5432/mini_project",
                "postgres",
                "admin"
        );
        admin = new Admin("ADMIN1", adminDAO);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getGradesOfOffering() {
    }

    @Test
    void insertStudent() {
    }

    @Test
    void insertFaculty() {
    }

    @Test
    void insertCourse() {
//        admin.insertCourse("CS111", "")
    }

    @Test
    void testGetGradesOfOffering() {
    }

    @Test
    void testInsertStudent() {
    }

    @Test
    void testInsertFaculty() {
    }

    @Test
    void insertCourseIntoCatalog() {
    }

    @Test
    void dropCourseFromCatalog() {
    }

    @Test
    void getGradesOfStudent() {
    }

    @Test
    void createBatch() {
    }

    @Test
    void insertCoreCourses() {
        try {
            assertTrue(admin.insertCoreCourses( 2020, new BufferedReader( new FileReader( "C:\\Users\\abhij\\Downloads\\Book1.csv" ) ) ));
        } catch ( FileNotFoundException e ) {
            throw new RuntimeException( e );
        }
    }
}